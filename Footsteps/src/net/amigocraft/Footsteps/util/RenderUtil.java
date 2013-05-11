package net.amigocraft.Footsteps.util;

import static org.lwjgl.opengl.GL11.*;
import java.awt.Color;
import java.awt.Font;

import net.amigocraft.Footsteps.Face;
import net.amigocraft.Footsteps.Footsteps;
import net.amigocraft.Footsteps.Location;
import net.amigocraft.Footsteps.Model;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.obj.TextureCoordinate;
import com.obj.Vertex;
import com.obj.WavefrontObject;

public class RenderUtil {

	// GUI related variables
	public static UnicodeFont font;
	public static UnicodeFont backFont;

	public static Vector3f getNormal(Vector3f p1, Vector3f p2, Vector3f p3){
		Vector3f v = new Vector3f();

		Vector3f calU = new Vector3f(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
		Vector3f calV = new Vector3f(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);

		v.setX(calU.getY() * calV.getZ() - calU.getZ() * calV.getY());
		v.setY(calU.getZ() * calV.getX() - calU.getX() * calV.getZ());
		v.setZ(calU.getX() * calV.getY() - calU.getY() * calV.getX());

		return (Vector3f)v.normalise();
	}

	public static void drawModel(WavefrontObject m){

		glEnable(GL_NORMALIZE);

		int i = 0;
		for (Vertex v : m.getVertices()){
			if (m.getCurrentMaterial() != null && m.getTextures() != null){
				if (m.getCurrentMaterial().getTexture() != null && m.getTextures().get(i) != null){
					glBindTexture(GL_TEXTURE_2D, m.getCurrentMaterial().getTexture().getTextureID());
					TextureCoordinate tc = m.getTextures().get(i);
					glTexCoord2f(tc.getU(), tc.getV());
				}
			}
			glVertex3f(v.getX(), v.getY(), v.getZ());
			if (m.getNormals().size() > i){
				Vertex n = m.getNormals().get(i);
				glNormal3f(n.getX(), n.getY(), n.getZ());
			}
			i += 1;
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
