package net.caseif.footsteps.util;

import static net.caseif.footsteps.Footsteps.*;
import static net.caseif.footsteps.util.BufferUtil.asFloatBuffer;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.stb.STBTruetype.*;

import net.caseif.footsteps.Face;
import net.caseif.footsteps.Footsteps;
import net.caseif.footsteps.Location;
import net.caseif.footsteps.Model;
import net.caseif.footsteps.SkyFactory;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class RenderUtil {
	private static final float FONT_HEIGHT = 42f;
	private static final int FONT_BITMAP_W = 512;
	private static final int FONT_BITMAP_H = 512;

	private static ByteBuffer ttf;
	private static STBTTFontinfo fontInfo;
	private static STBTTBakedChar.Buffer fontBitmap;
	private static float fontAscent;
	private static float fontDescent;
	private static float fontLineGap;

	private static int fontTexture;

	public static void renderWorld(long window){
		glLightfv(GL_LIGHT1, GL_POSITION, asFloatBuffer(new float[]{lightPosition.x, lightPosition.y, lightPosition.z, 1f}));

		// skybox
		if (wireframe)
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glPushMatrix();
		glLoadIdentity();
		glRotatef(camera.getPitch(), 1, 0, 0);
		glRotatef(camera.getYaw(), 0, 1, 0);
		glRotatef(5, 0, 0, 1);
		glRotatef(-13, 1, 0, 0);
		glCallList(SkyFactory.getHandle());
		glPopMatrix();
		if (wireframe)
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

		drawString(window, 0, 0, " ", false);

		// terrain
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, Footsteps.grassTexture);
		glEnable(GL_LIGHT1);
		glCallList(Footsteps.terrainHandle);
		glDisable(GL_LIGHT1);
		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);

		// models
		/*float xOff = ((float)Math.sin(Math.toRadians(camera.getYaw())) * 1) - 3;
		float zOff = ((float)Math.cos(Math.toRadians(camera.getYaw())) * 1) + 5;
		glTranslatef(-camera.getX() + xOff, -camera.getY() - 5, (-camera.getZ() - zOff));
		glRotatef(90f, 1f, 0f, 0f);
		glRotatef(camera.getYaw(), 0f, 1f, 0f);
		System.out.println("x: " + (-camera.getX() + xOff));
		System.out.println("y: " + -camera.getY());
		System.out.println("z: " + (-camera.getZ() - zOff));
		System.out.println("xOff: " + (xOff * 3));
		System.out.println("zOff: " + (zOff * 3));
		glRotatef(90f, 0f, 0f, 1f);*/
		//glScalef(5f, 5f, 5f);
		glTranslatef(250, 39, 150);
		Footsteps.bunnyFrame += 1;
		if (OPENGL_VERSION >= 2.0){
			glUseProgram(shaderProgram);
			glUniform1f(diffuseModifierUniform, 10f);
		}
		glEnable(GL_LIGHT0);
		glEnable(GL_CULL_FACE);
		try {
			//glBindTexture(GL_TEXTURE_2D, TextureLoader.getTexture("PNG", this.getClass().getResourceAsStream("/models/skin_texture.png")).getTextureID());
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		glCallList(bunnyHandle);
		glDisable(GL_CULL_FACE);
		glDisable(GL_LIGHT0);
		if (OPENGL_VERSION >= 2.0){
			glUseProgram(0);
		}
	}

	public static Vector3f getNormal(Vector3f p1, Vector3f p2, Vector3f p3){
		Vector3f v = new Vector3f();

		Vector3f calU = new Vector3f(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
		Vector3f calV = new Vector3f(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);

		v.setX(calU.getY() * calV.getZ() - calU.getZ() * calV.getY());
		v.setY(calU.getZ() * calV.getX() - calU.getX() * calV.getZ());
		v.setZ(calU.getX() * calV.getY() - calU.getY() * calV.getX());

		return (Vector3f)v.normalise();
	}

	public static void drawModel(Model m){

		glEnable(GL_NORMALIZE);
		
		String currentMaterial = "";
		
		boolean quads = false;
		
		for (Face f : m.faces){
			if (f.vertex.length == 4 && !quads){
				glEnd();
				glBegin(GL_QUADS);
				quads = true;
			}
			else if (f.vertex.length == 3 && quads){
				glEnd();
				glBegin(GL_TRIANGLES);
				quads = false;
			}
			if (f.texture != null){
				if (currentMaterial != f.material.getName()){
					var tex = TextureUtil.loadTexture(f.material.getTexture());
					if (tex.isPresent()) {
						glBindTexture(GL_TEXTURE_2D, tex.getAsInt());
						currentMaterial = f.material.getName();
					}
				}
						
				for (int i : f.texture){
					float[] tc = m.textureCoords.get(i - 1);
					glTexCoord2f(tc[0], tc[1]);
				}
			}
			int l = 0;
			for (int i : f.vertex){
				Vector3f n = m.normals.get(f.normal[l] - 1);
				glNormal3f(n.getX(), n.getY(), n.getZ());
				Vector3f v = m.vertices.get(i - 1);
				glVertex3f(v.getX(), v.getY(), v.getZ());
				l += 1;
			}
		}

		glDisable(GL_NORMALIZE);
	}

	public static void drawString(long window, int x, int y, String str, boolean shadow){
		var winWidthBuf = BufferUtils.createIntBuffer(1);
		var winHeightBuf = BufferUtils.createIntBuffer(1);
		glfwGetWindowSize(window, winWidthBuf, winHeightBuf);
		var winWidth = winWidthBuf.get();
		var winHeight = winHeightBuf.get();

		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, winWidth, winHeight, 0, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
		glDisable(GL_LIGHTING);
		if (Footsteps.wireframe)
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		glEnableClientState(GL_VERTEX_ARRAY);

		glEnable(GL_TEXTURE_2D);

		glBindTexture(GL_TEXTURE_2D, fontTexture);

		if (shadow) {
			glPushMatrix();
			glTranslatef(0f, 0f, -1f);
			renderText(str, x - 3, y - 3, new Vector3f(0f, 0f, 0f));
			glPopMatrix();
		}

		renderText(str, x, y, new Vector3f(1f, 1f, 1f));

		glBindTexture(GL_TEXTURE_2D, 0);

		glDisable(GL_TEXTURE_2D);

		if (Footsteps.wireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		}

		glEnable(GL_LIGHTING);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
	}

	private static void renderText(String text, int x, int y, Vector3f color) {
		try (var stack = MemoryStack.stackPush()) {
			var pCodePoint = stack.mallocInt(1);

			var xBuf = stack.floats(0f);
			var yBuf = stack.floats(0f);

			var quad = STBTTAlignedQuad.mallocStack(stack);

			glColor4f(color.x, color.y, color.z, 1f);
			glPushMatrix();
			glTranslatef(x, y, 0f);

			glBegin(GL_QUADS);

			float scale = stbtt_ScaleForPixelHeight(fontInfo, FONT_HEIGHT);

			for (int i = 0, to = text.length(); i < to; ) {
				i += getCodePoint(text, to, i, pCodePoint);

				int cp = pCodePoint.get(0);

				if (cp < 32 || 128 <= cp) {
					continue;
				}

				stbtt_GetBakedQuad(fontBitmap, FONT_BITMAP_W, FONT_BITMAP_H, cp - 32, xBuf, yBuf, quad, true);
				xBuf.put(0, xBuf.get(0));
				if (i < to) {
					getCodePoint(text, to, i, pCodePoint);
					xBuf.put(0, xBuf.get(0) + stbtt_GetCodepointKernAdvance(fontInfo, cp, pCodePoint.get(0)) * scale);
				}

				glTexCoord2f(quad.s0(), quad.t0());
				glVertex2f(quad.x0(), quad.y0());
				glTexCoord2f(quad.s1(), quad.t0());
				glVertex2f(quad.x1(), quad.y0());
				glTexCoord2f(quad.s1(), quad.t1());
				glVertex2f(quad.x1(), quad.y1());
				glTexCoord2f(quad.s0(), quad.t1());
				glVertex2f(quad.x0(), quad.y1());
			}

			glEnd();

			glPopMatrix();
		}
	}

	@SuppressWarnings("unchecked")
	public static void setUpFont(){
		try (var ttfIs = RenderUtil.class.getResourceAsStream("/fonts/open-sans-bold.ttf")) {
			var ttfBytes = ttfIs.readAllBytes();
			ttf = BufferUtils.createByteBuffer(ttfBytes.length + 1);
			ttf.put(ttfBytes);
			ttf.flip();
		} catch (IOException ex) {
			throw new RuntimeException("Failed to load font", ex);
		}

		fontInfo = STBTTFontinfo.create();
		if (!stbtt_InitFont(fontInfo, ttf)) {
			throw new RuntimeException("Failed to initialize font");
		}

		try (MemoryStack stack = MemoryStack.stackPush()) {
			var pAscent  = stack.mallocInt(1);
			var pDescent = stack.mallocInt(1);
			var pLineGap = stack.mallocInt(1);

			stbtt_GetFontVMetrics(fontInfo, pAscent, pDescent, pLineGap);

			fontAscent = pAscent.get();
			fontDescent = pDescent.get();
			fontLineGap = pLineGap.get();
		}

		STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);
		ByteBuffer bitmap = BufferUtils.createByteBuffer(FONT_BITMAP_W * FONT_BITMAP_H);

		stbtt_BakeFontBitmap(ttf, FONT_HEIGHT, bitmap, FONT_BITMAP_W, FONT_BITMAP_H, 32, cdata);

		fontTexture = glGenTextures();

		glBindTexture(GL_TEXTURE_2D, fontTexture);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, FONT_BITMAP_W, FONT_BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

		glBindTexture(GL_TEXTURE_2D, 0);

		fontBitmap = cdata;
	}

	public void moveCameraSmooth(Location oldLoc, Location newLoc, int stages){
		float xDiff = newLoc.getX() - oldLoc.getX();
		float yDiff = newLoc.getY() - oldLoc.getY();
		float zDiff = newLoc.getZ() - oldLoc.getZ();

		float xPerStage = xDiff / stages;
		float yPerStage = yDiff / stages;
		float zPerStage = zDiff / stages;

		for (int i = 0; i < stages; i++){
			Footsteps.camera.position.x += xPerStage;
			Footsteps.camera.position.y += yPerStage;
			Footsteps.camera.position.z += zPerStage;
		}
	}

	private static int getCodePoint(String text, int to, int i, IntBuffer codePointOut) {
		char c1 = text.charAt(i);
		if (Character.isHighSurrogate(c1) && i + 1 < to) {
			char c2 = text.charAt(i + 1);
			if (Character.isLowSurrogate(c2)) {
				codePointOut.put(0, Character.toCodePoint(c1, c2));
				return 2;
			}
		}
		codePointOut.put(0, c1);
		return 1;
	}
}
