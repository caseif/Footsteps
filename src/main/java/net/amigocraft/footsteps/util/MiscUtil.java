package net.amigocraft.footsteps.util;

import net.amigocraft.footsteps.Footsteps;

import static org.lwjgl.glfw.GLFW.*;

public class MiscUtil {

	public static long getTime() {
		return (long) (glfwGetTime() * 1000);
	}

	public static void updateFps(){
		if (Footsteps.time - Footsteps.lastFps > 1000){
			Footsteps.currentFps = (int)(1000 / Footsteps.delta);
			Footsteps.lastFps = Footsteps.time;
		}
	}
}
