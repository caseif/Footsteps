package net.caseif.footsteps;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import net.caseif.footsteps.util.GluEmulation;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class SkyFactory {

	private static int skyHandle = 0;

	public SkyFactory(){
		Texture skybox = null;
		try {
			skybox = TextureLoader.getTexture("PNG", this.getClass().getClassLoader().getResourceAsStream("images/skybox.png"));
		}
		catch (Exception ex){
			ex.printStackTrace();
		}

		skyHandle = glGenLists(1);
		glNewList(skyHandle, GL_COMPILE);
		{
			GluEmulation.gluLookAt(
					0, 0, 0,
					Footsteps.camera.getX(), Footsteps.camera.getY(), Footsteps.camera.getZ(),
					0, 1, 0);
			
			glPushAttrib(GL_ENABLE_BIT);
			glEnable(GL_TEXTURE_2D);
			glDisable(GL_DEPTH_TEST);
			glDisable(GL_LIGHTING);
			glDisable(GL_BLEND);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			
			glColor4f(1, 1, 1, 1);

			// front
			glBindTexture(GL_TEXTURE_2D, skybox.getTextureID());
			glBegin(GL_QUADS);
			glTexCoord2f(0.25f, 0.5f);
			glVertex3f(0.5f, 0.5f, -0.5f);
			glTexCoord2f(0.5f, 0.5f);
			glVertex3f(-0.5f, 0.5f, -0.5f);
			glTexCoord2f(0.5f, 1f);
			glVertex3f(-0.5f, -0.5f, -0.5f);
			glTexCoord2f(0.25f, 1f);
			glVertex3f(0.5f, -0.5f, -0.5f);
			glEnd();

			// left
			glBegin(GL_QUADS);
			glTexCoord2f(0f, 0.5f);
			glVertex3f(0.5f, 0.5f, 0.5f);
			glTexCoord2f(0.25f, 0.5f);
			glVertex3f(0.5f, 0.5f, -0.5f);
			glTexCoord2f(0.25f, 1f);
			glVertex3f(0.5f, -0.5f, -0.5f);
			glTexCoord2f(0f, 1f);
			glVertex3f(0.5f, -0.5f, 0.5f);
			glEnd();

			// back
			glBegin(GL_QUADS);
			glTexCoord2f(0.75f, 0.5f);
			glVertex3f(-0.5f, 0.5f, 0.5f);
			glTexCoord2f(1f, 0.5f);
			glVertex3f(0.5f, 0.5f, 0.5f);
			glTexCoord2f(1f, 1f);
			glVertex3f(0.5f, -0.5f, 0.5f);
			glTexCoord2f(0.75f, 1f);
			glVertex3f(-0.5f, -0.5f, 0.5f);

			glEnd();

			// right
			glBegin(GL_QUADS);
			glTexCoord2f(0.5f, 0.5f);
			glVertex3f(-0.5f, 0.5f, -0.5f);
			glTexCoord2f(0.75f, 0.5f);
			glVertex3f(-0.5f, 0.5f, 0.5f);
			glTexCoord2f(0.75f, 1f);
			glVertex3f(-0.5f, -0.5f, 0.5f);
			glTexCoord2f(0.5f, 1f);
			glVertex3f(-0.5f, -0.5f, -0.5f);
			glEnd();

			// top
			glBegin(GL_QUADS);
			glTexCoord2f(0.25f, 0f);
			glVertex3f(0.5f, 0.5f, 0.5f);
			glTexCoord2f(0.5f, 0f);
			glVertex3f(-0.5f, 0.5f, 0.5f);
			glTexCoord2f(0.5f, 0.5f);
			glVertex3f(-0.5f, 0.5f, -0.5f);
			glTexCoord2f(0.25f, 0.5f);
			glVertex3f(0.5f, 0.5f, -0.5f);
			glEnd();
			
			glEnable(GL_DEPTH_TEST);
			glEnable(GL_LIGHTING);
			glEnable(GL_BLEND);
			
			glBindTexture(GL_TEXTURE_2D, 0);

			glPopAttrib();
		}
		glEndList();
	}

	public static int getHandle(){
		return skyHandle;
	}
}
