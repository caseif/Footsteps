package net.caseif.footsteps.util;

import static net.caseif.footsteps.util.GluEmulation.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL21.*;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.OptionalLong;

import javax.imageio.ImageIO;

import static net.caseif.footsteps.util.BufferUtil.*;
import static net.caseif.footsteps.util.ImageUtil.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import net.caseif.footsteps.Footsteps;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;

public class SetupDisplay {
	public static OptionalLong setupDisplay(){
		long window;

		if (!glfwInit()) {
			System.err.println("Failed to init GLFW");
			return OptionalLong.empty();
		}

		try {
			var monitor = glfwGetPrimaryMonitor();
			if (monitor == 0) {
				monitor = glfwGetMonitors().get();
			}

			var widthBuf = BufferUtils.createIntBuffer(1);
			var heightBuf = BufferUtils.createIntBuffer(1);
			glfwGetMonitorWorkarea(monitor, null, null, widthBuf, heightBuf);

			glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
			glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_FALSE);
			glfwWindowHint(GLFW_OPENGL_CORE_PROFILE, GLFW_FALSE);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
			glfwWindowHint(GLFW_OPENGL_COMPAT_PROFILE, GLFW_FALSE);
			window = glfwCreateWindow(widthBuf.get(), heightBuf.get(), "footsteps", NULL, NULL);
			if (window == NULL) {
				return OptionalLong.empty();
			}

			ByteBuffer[] icons;
			if (System.getProperty("os.name").startsWith("Windows")){
				icons = new ByteBuffer[2];
				BufferedImage icon1 = scaleImage(ImageIO.read(Footsteps.class.getClassLoader().getResourceAsStream("images/icon.png")), 16, 16);
				BufferedImage icon2 = scaleImage(ImageIO.read(Footsteps.class.getClassLoader().getResourceAsStream("images/icon.png")), 32, 32);
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

			try (final GLFWImage.Buffer iconSet = GLFWImage.malloc(icons.length)) {
				for (var i = 0; i < icons.length; i++) {
					iconSet.put(i, new GLFWImage(icons[i]));
				}

				//glfwSetWindowIcon(window, iconSet);
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
			return OptionalLong.empty();
		}

		glfwMakeContextCurrent(window);

		GL.createCapabilities();

		glfwShowWindow(window);

		System.out.println("GL version: " + glGetString(GL_VERSION));

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(30f, 1280f / 720f, 0.001f, 1000f);
		glOrtho(1, 1, 1, 1, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glShadeModel(GL_SMOOTH);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_LIGHTING);
		float ambientLight[] = {Footsteps.terrainBrightness, Footsteps.terrainBrightness, Footsteps.terrainBrightness, 1f};
		glLightfv(GL_LIGHT1, GL_AMBIENT, asFloatBuffer(ambientLight));

		glLightModelfv(GL_LIGHT_MODEL_AMBIENT, asFloatBuffer(new float[]{0.05f, 0.05f, 0.05f, 1f}));
		glLightfv(GL_LIGHT0, GL_DIFFUSE, asFloatBuffer(new float[]{1.5f, 1.5f, 1.5f, 1f}));
		glCullFace(GL_BACK);
		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT, GL_AMBIENT);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_ALPHA_TEST);
		glAlphaFunc(GL_GREATER, 0);
		if (Footsteps.OPENGL_VERSION >= 2.1) {
			glEnable(GL_PIXEL_UNPACK_BUFFER);
		}

		return OptionalLong.of(window);
	}
}
