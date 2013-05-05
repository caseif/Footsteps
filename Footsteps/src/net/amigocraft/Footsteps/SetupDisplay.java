package net.amigocraft.Footsteps;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import static net.amigocraft.Footsteps.util.BufferUtil.*;
import static net.amigocraft.Footsteps.util.ImageUtil.*;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;

public class SetupDisplay {
	public static void setupDisplay(){

		try {
			Display.setDisplayMode(new DisplayMode(Display.getDesktopDisplayMode().getWidth() - 20, Display.getDesktopDisplayMode().getHeight() - 100));
			Display.setTitle("Footsteps");
			ByteBuffer[] icons = null;
			if (System.getProperty("os.name").startsWith("Windows")){
				icons = new ByteBuffer[2];
				BufferedImage icon1 = scaleImage(ImageIO.read(Footsteps.class.getClassLoader().getResourceAsStream("images/icon.png")), 16, 16);
				BufferedImage icon2 = scaleImage(ImageIO.read(Footsteps.class.getClassLoader().getResourceAsStream("images/icon.png")), 32, 32);;
				icons[0] = asByteBuffer(icon1);
				icons[1] = asByteBuffer(icon2);
			}
			else if (System.getProperty("os.name").startsWith("Mac")){
				icons = new ByteBuffer[1];
				BufferedImage icon = scaleImage(ImageIO.read(Footsteps.class.getClassLoader().getResourceAsStream("images/icon.png")), 128, 128);
				icons[0] = asByteBuffer(icon);
			}
			else {
				icons = new ByteBuffer[1];
				BufferedImage icon = scaleImage(ImageIO.read(Footsteps.class.getClassLoader().getResourceAsStream("images/icon.png")), 32, 32);
				icons[0] = asByteBuffer(icon);
			}
			Display.setIcon(icons);
			Display.create();
		}
		catch (Exception ex){
			ex.printStackTrace();
		}

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(30f, 1280f / 720f, 0.001f, 1000f);
		glOrtho(1, 1, 1, 1, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glShadeModel(GL_SMOOTH);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_LIGHTING);
		float ambientLight[] = {Footsteps.terrainBrightness, Footsteps.terrainBrightness, Footsteps.terrainBrightness, 1f};
		glLight(GL_LIGHT1, GL_AMBIENT, asFloatBuffer(ambientLight));

		glLightModel(GL_LIGHT_MODEL_AMBIENT, asFloatBuffer(new float[]{0.05f, 0.05f, 0.05f, 1f}));
		glLight(GL_LIGHT0, GL_DIFFUSE, asFloatBuffer(new float[]{1.5f, 1.5f, 1.5f, 1f}));
		glCullFace(GL_BACK);
		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT, GL_AMBIENT);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_ALPHA_TEST);
		glAlphaFunc(GL_GREATER, 0);

	}
}
