package net.amigocraft.footsteps;

import net.java.games.input.Controller;
import net.java.games.input.Component.Identifier;

public class GamepadHandler {

	public static Joystick joystick = new Joystick(Controller.Type.STICK, Controller.Type.GAMEPAD);
	
	public static boolean handleGamepad(){
		if (Footsteps.gamepad){
			if (!joystick.pollController())
				Footsteps.gamepad = false;
		}

		if (Footsteps.gamepad){
			float leftStickX = (joystick.getX_LeftJoystick_Percentage() - 50) / 50f;
			float leftStickY = (joystick.getY_LeftJoystick_Percentage() - 50) / 50f;
			float rightStickX = (joystick.getX_RightJoystick_Percentage() - 50) / 50f;
			float rightStickY = (joystick.getY_RightJoystick_Percentage() - 50) / 50f;

			if (leftStickX > .04 || leftStickX < -.04 || leftStickY > .04 || leftStickY < -.04){
				boolean backward = false;
				if (leftStickY > 0)
					backward = true;
				int angle = Math.round(leftStickX * 90);
				float larger = Math.abs(leftStickX);
				if (Math.abs(leftStickY) > Math.abs(leftStickX))
					larger = Math.abs(leftStickY);
				if (Footsteps.movementSpeed * Footsteps.delta / 1000 < 50) // just in case
					Footsteps.camera.moveCustom(angle, Footsteps.movementSpeed * larger * Footsteps.delta / 1000, backward);
				Footsteps.moved = true;
			}
			else
				Footsteps.camera.freezeXAndZ();

			if (rightStickX > .04 || rightStickX < -.04 || rightStickY > .04 || rightStickY < -.04){
				Footsteps.camera.setYaw(Footsteps.camera.getYaw() + rightStickX * Footsteps.joystickPovSpeed * Footsteps.delta / 1000);
				Footsteps.camera.setPitch(Footsteps.camera.getPitch() + rightStickY * Footsteps.joystickPovSpeed * Footsteps.delta / 1000);
			}

			float a = joystick.getComponentValue(Identifier.Button._0);
			if (a > 0 && !Footsteps.jumping && !Footsteps.falling)
				Footsteps.jumping = true;

			float start = joystick.getComponentValue(Identifier.Button._7);
			if (start > 0)
				return true;

			float select = joystick.getComponentValue(Identifier.Button._6);
			if (select > 0){
				if (System.currentTimeMillis() - Footsteps.lastPress > 200){
					if (Footsteps.debug)
						Footsteps.debug = false;
					else
						Footsteps.debug = true;
					Footsteps.lastPress = System.currentTimeMillis();
				}
			}

			float x = joystick.getComponentValue(Identifier.Button._2);
			if (x > 0){
				if (System.currentTimeMillis() - Footsteps.lastPress > 200){
					if (Footsteps.wireframe)
						Footsteps.wireframe = false;
					else
						Footsteps.wireframe = true;
					Footsteps.lastPress = System.currentTimeMillis();
				}
			}
		}
		
		return false;
	}
	
}
