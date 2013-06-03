package net.amigocraft.Footsteps.util;

import static net.amigocraft.Footsteps.Footsteps.*;
import static net.amigocraft.Footsteps.util.BufferUtil.asFloatBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import net.amigocraft.Footsteps.Face;
import net.amigocraft.Footsteps.Footsteps;
import net.amigocraft.Footsteps.Location;
import net.amigocraft.Footsteps.Model;
import net.amigocraft.Footsteps.SkyFactory;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.TextureLoader;

public class RenderUtil {

	// GUI related variables
	public static UnicodeFont font;
	public static UnicodeFont backFont;
	
	public static void renderWorld(){
		glLight(GL_LIGHT1, GL_POSITION, asFloatBuffer(new float[]{lightPosition.x, lightPosition.y, lightPosition.z, 1f}));

		// skybox
		if (wireframe)
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glPushMatrix();
		glLoadIdentity();
		glRotatef(camera.getPitch() - 13, 1, 0, 0);
		glRotatef(camera.getYaw(), 0, 1, 0);
		glRotatef(5, 0, 0, 1);
		glCallList(SkyFactory.getHandle());
		glPopMatrix();
		if (wireframe)
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

		drawString(0, 0, " ", false);

		// terrain
		Footsteps.grassTexture.bind();
		glEnable(GL_LIGHT1);
		glCallList(Footsteps.terrainHandle);
		glDisable(GL_LIGHT1);
		glBindTexture(GL_TEXTURE_2D, 0);

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
				try {
					if (currentMaterial != f.material.getName()){
						glBindTexture(GL_TEXTURE_2D, TextureLoader.getTexture(
								"PNG", RenderUtil.class.getClassLoader().getResourceAsStream(
										f.material.getTexture())).getTextureID());
						currentMaterial = f.material.getName();
					}
						
				}
				catch (IOException ex){
					ex.printStackTrace();
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

	public static void drawString(int x, int y, String str, boolean shadow){
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
		glDisable(GL_LIGHTING);
		if (Footsteps.wireframe)
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		if (shadow)
			backFont.drawString(x - 3, y - 3, str);
		font.drawString(x, y, str);
		if (Footsteps.wireframe)
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glEnable(GL_LIGHTING);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
	}

	@SuppressWarnings("unchecked")
	public static void setUpFont(){
		float size = 28F;
		Font awtFont = new Font("Verdana", Font.BOLD, (int)size);
		font = new UnicodeFont(awtFont.deriveFont(0, size));
		font.addAsciiGlyphs();
		ColorEffect e = new ColorEffect();
		e.setColor(Color.WHITE);
		font.getEffects().add(e);
		backFont = new UnicodeFont(awtFont.deriveFont(0, size));
		backFont.addAsciiGlyphs();
		ColorEffect e2 = new ColorEffect();
		e.setColor(Color.BLACK);
		backFont.getEffects().add(e2);
		try {
			font.loadGlyphs();
			backFont.loadGlyphs();
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
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
}
