package net.caseif.footsteps;

import static org.lwjgl.glfw.GLFW.*;

public class KeyHandler {
	public static void handleKeys(long window) {

		var mouseMode = glfwGetInputMode(window, GLFW_CURSOR);
		var isGrabbed = mouseMode == GLFW_CURSOR_DISABLED;

		if (!Footsteps.moved && isGrabbed) {
			if (glfwGetKey(window, GLFW_KEY_W) != 0)
				Footsteps.forward = true;
			else if (Footsteps.forward){
				Footsteps.camera.walkForward(0);
				Footsteps.forward = false;
			}

			if (glfwGetKey(window, GLFW_KEY_S) != 0)
				Footsteps.backward = true;
			else if (Footsteps.backward) {
				Footsteps.camera.walkBackward(0);
				Footsteps.backward = false;
			}

			if (glfwGetKey(window, GLFW_KEY_A) != 0)
				Footsteps.left = true;
			else if (Footsteps.left) {
				Footsteps.camera.strafeLeft(0);
				Footsteps.left = false;
			}

			if (glfwGetKey(window, GLFW_KEY_D) != 0)
				Footsteps.right = true;
			else if (Footsteps.right) {
				Footsteps.camera.strafeRight(0);
				Footsteps.right = false;
			}
		}

		if (glfwGetKey(window, GLFW_KEY_SPACE) != 0) {
			if (!Footsteps.jumping && !Footsteps.falling){
				Footsteps.jumping = true;
				Footsteps.jumpFrame = 0;
			}
		}

		if (glfwGetKey(window, GLFW_KEY_F) != 0) {
			if (System.currentTimeMillis() - Footsteps.lastPress > 200){
				Footsteps.wireframe = !Footsteps.wireframe;
				Footsteps.lastPress = System.currentTimeMillis();
			}
		}			

		/*if (isKeyDown(KEY_F11)){
		if (System.currentTimeMillis() - lastPress > 500){
			try {
				if (Display.isFullscreen()){
					Display.setDisplayMode(Display.getDesktopDisplayMode());
					Display.setFullscreen(false);
				}
				else {
					Display.setDisplayMode(Display.getDesktopDisplayMode());
					Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
				}
			}
			catch (LWJGLException ex){
				ex.printStackTrace();
			}
		}
	}*/

		if (glfwGetKey(window, GLFW_KEY_F3) != 0) {
			if (System.currentTimeMillis() - Footsteps.lastPress > 200){
				if (Footsteps.debug)
					Footsteps.debug = false;
				else
					Footsteps.debug = true;
				Footsteps.lastPress = System.currentTimeMillis();
			}
		}

		// open menu
		if (glfwGetKey(window, GLFW_KEY_ESCAPE) != 0) {
			if (System.currentTimeMillis() - Footsteps.lastPress > 200){
				Footsteps.ingameMenu = true;
				Footsteps.lastPress = System.currentTimeMillis();
			}
		}

		/*if (isButtonDown(1)) {
			if (System.currentTimeMillis() - Footsteps.lastPress > 200){
				if (isGrabbed())
					setGrabbed(false);
				else if (!isGrabbed())
					setGrabbed(true);
				Footsteps.lastPress = System.currentTimeMillis();
			}
		}*/
	}
}
