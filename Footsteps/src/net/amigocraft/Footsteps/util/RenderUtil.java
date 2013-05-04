package net.amigocraft.Footsteps.util;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NORMALIZE;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex3f;

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

	public static void drawModel(Model m){
		
		glEnable(GL_NORMALIZE);
		
		//Texture t = null;
		//Material lastMaterial = null;
		for (Face f : m.faces){
			/*if (f.material.getTexture() != null){
				if (f.material != lastMaterial){
					lastMaterial = f.material;
					try {
						t = TextureLoader.getTexture("PNG", this.getClass().getClassLoader().getResourceAsStream(f.material.getTexture()));
						t.bind();
					}
					catch (Exception ex){
						ex.printStackTrace();
					}
				}
			}*/

			//Vector3f t1 = m.textures.get((int)f.texture.x - 1);
			//glVertex3f(t1.x, t1.y, t1.z);
			Vector3f n1 = m.normals.get((int)f.normal.x - 1);
			glNormal3f(n1.x, n1.y, n1.z);
			Vector3f v1 = m.vertices.get((int)f.vertex.x - 1);
			glVertex3f(v1.x, v1.y, v1.z);

			//Vector3f t2 = m.textures.get((int)f.texture.y - 1);
			//glVertex3f(t2.x, t2.y, t2.z);
			Vector3f n2 = m.normals.get((int)f.normal.y - 1);
			glNormal3f(n2.x, n2.y, n2.z);
			Vector3f v2 = m.vertices.get((int)f.vertex.y - 1);
			glVertex3f(v2.x, v2.y, v2.z);

			//Vector3f t3 = m.textures.get((int)f.texture.z - 1);
			//glVertex3f(t3.x, t3.y, t3.z);
			Vector3f n3 = m.normals.get((int)f.normal.z - 1);
			glNormal3f(n3.x, n3.y, n3.z);
			Vector3f v3 = m.vertices.get((int)f.vertex.z - 1);
			glVertex3f(v3.x, v3.y, v3.z);
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
