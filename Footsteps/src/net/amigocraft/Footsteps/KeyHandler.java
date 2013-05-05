package net.amigocraft.Footsteps;

import static org.lwjgl.input.Keyboard.KEY_A;
import static org.lwjgl.input.Keyboard.KEY_D;
import static org.lwjgl.input.Keyboard.KEY_ESCAPE;
import static org.lwjgl.input.Keyboard.KEY_F;
import static org.lwjgl.input.Keyboard.KEY_F3;
import static org.lwjgl.input.Keyboard.KEY_S;
import static org.lwjgl.input.Keyboard.KEY_SPACE;
import static org.lwjgl.input.Keyboard.KEY_W;
import static org.lwjgl.input.Keyboard.isKeyDown;
import static org.lwjgl.input.Mouse.isButtonDown;
import static org.lwjgl.input.Mouse.isGrabbed;
import static org.lwjgl.input.Mouse.setGrabbed;

public class KeyHandler {
	public static void handleKeys(){
		if (!Footsteps.moved){
			if (isKeyDown(KEY_W))
				Footsteps.forward = true;
			else if (Footsteps.forward){
				Footsteps.camera.walkForward(0);
				Footsteps.forward = false;
			}

			if (isKeyDown(KEY_S))
				Footsteps.backward = true;
			else if (Footsteps.backward){
				Footsteps.camera.walkBackward(0);
				Footsteps.backward = false;
			}

			if (isKeyDown(KEY_A))
				Footsteps.left = true;
			else if (Footsteps.left){
				Footsteps.camera.strafeLeft(0);
				Footsteps.left = false;
			}

			if (isKeyDown(KEY_D))
				Footsteps.right = true;
			else if (Footsteps.right){
				Footsteps.camera.strafeRight(0);
				Footsteps.right = false;
			}
		}

		if (isKeyDown(KEY_SPACE)){
			if (!Footsteps.jumping && !Footsteps.falling){
				Footsteps.jumping = true;
				Footsteps.jumpFrame = 0;
			}
		}

		if (isKeyDown(KEY_F)){
			if (System.currentTimeMillis() - Footsteps.lastPress > 200){
				if (Footsteps.wireframe)
					Footsteps.wireframe = false;
				else
					Footsteps.wireframe = true;
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

		if (isKeyDown(KEY_F3)){
			if (System.currentTimeMillis() - Footsteps.lastPress > 200){
				if (Footsteps.debug)
					Footsteps.debug = false;
				else
					Footsteps.debug = true;
				Footsteps.lastPress = System.currentTimeMillis();
			}
		}

		// open menu
		if (isKeyDown(KEY_ESCAPE)){
			if (System.currentTimeMillis() - Footsteps.lastPress > 200){
				Footsteps.ingameMenu = true;
				Footsteps.lastPress = System.currentTimeMillis();
			}
		}

		if (isButtonDown(1)){
			if (System.currentTimeMillis() - Footsteps.lastPress > 200){
				if (isGrabbed())
					setGrabbed(false);
				else if (!isGrabbed())
					setGrabbed(true);
				Footsteps.lastPress = System.currentTimeMillis();
			}
		}
	}
}
