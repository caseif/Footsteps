package net.amigocraft.footsteps.util;

import net.amigocraft.footsteps.Footsteps;

import org.lwjgl.Sys;

public class MiscUtil {

	public static long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	public static void updateFps(){
		if (Footsteps.time - Footsteps.lastFps > 1000){
			Footsteps.currentFps = (int)(1000 / Footsteps.delta);
			Footsteps.lastFps = Footsteps.time;
		}
	}
}
