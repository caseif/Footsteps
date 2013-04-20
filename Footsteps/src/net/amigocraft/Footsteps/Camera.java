package net.amigocraft.Footsteps;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	Vector3f position = null;
	Vector3f velocity = null;
	//the rotation around the Y axis of the camera
	float yaw = 0.0f;
	//the rotation around the X axis of the camera
	public float pitch = 0.0f;

	private float pitchFade = 10f;
	private float yawFade = 10f;

	private boolean moved = false;

	public Camera(float x, float y, float z){
		position = new Vector3f(x * -1f, y * -1f, z * -1f);
		velocity = new Vector3f(0f, 0f, 0f);
	}

	public Location getLocation(){
		return new Location(position.x, position.y, position.z);
	}

	public Vector3f getVector(){
		return position;
	}

	public Vector3f getVelocity(){
		return velocity;
	}

	public float getX(){
		return position.x;
	}

	public float getY(){
		return position.y;
	}

	public float getZ(){
		return position.z;
	}

	public float getPitch(){
		return pitch;
	}

	public float getYaw(){
		return yaw;
	}


	public void setPitch(float amount){
		if (Math.abs(amount - pitch) != 0) 
			moved = true;

		if (amount > 80)
			pitch = 80;
		else if (amount < -80)
			pitch = -80;
		else
			pitch = amount;
	}

	public void setYaw(float amount){
		if (Math.abs(amount - yaw) != 0)
			moved = true;

		yaw = amount;
		if (yaw >= 180)
			yaw -= 360;
		if (yaw < -180)
			yaw += 360;
	}

	public void walkForward(float distance){
		velocity.setX(distance * -(float)Math.sin(Math.toRadians(yaw)));
		velocity.setZ(distance * (float)Math.cos(Math.toRadians(yaw)));
		for (CollisionBox c : Footsteps.cBoxes){
			if (c.contains(new Location(position.getX() + velocity.getX(), position.getY(), position.getZ()))){
				velocity.setX(0);
				break;
			}
			if (c.contains(new Location(position.getX(), position.getY(), position.getZ() + velocity.getZ()))){
				velocity.setX(0);
				break;
			}
		}
	}

	public void walkBackward(float distance){
		velocity.setX(distance * (float)Math.sin(Math.toRadians(yaw)));
		velocity.setZ(distance * -(float)Math.cos(Math.toRadians(yaw)));
		for (CollisionBox c : Footsteps.cBoxes){
			if (c.contains(new Location(position.getX() + velocity.getX(), position.getY(), position.getZ()))){
				velocity.setX(0);
				break;
			}
			if (c.contains(new Location(position.getX(), position.getY(), position.getZ() + velocity.getZ()))){
				velocity.setZ(0);
				break;
			}
		}
	}

	public void strafeLeft(float distance){
		velocity.setX(distance * -(float)Math.sin(Math.toRadians(yaw - 90)));
		velocity.setZ(distance * (float)Math.cos(Math.toRadians(yaw - 90)));
		for (CollisionBox c : Footsteps.cBoxes){
			if (c.contains(new Location(position.getX() + velocity.getX(), position.getY(), position.getZ()))){
				velocity.setX(0);
				break;
			}
			if (c.contains(new Location(position.getX(), position.getY(), position.getZ() + velocity.getZ()))){
				velocity.setZ(0);
				break;
			}
		}
	}

	public void strafeRight(float distance){
		velocity.setX(distance * -(float)Math.sin(Math.toRadians(yaw + 90)));
		velocity.setZ(distance * (float)Math.cos(Math.toRadians(yaw + 90)));
		for (CollisionBox c : Footsteps.cBoxes){
			if (c.contains(new Location(position.getX() + velocity.getX(), position.getY(), position.getZ()))){
				velocity.setX(0);
				break;
			}
			if (c.contains(new Location(position.getX(), position.getY(), position.getZ() + velocity.getZ()))){
				velocity.setZ(0);
				break;
			}
		}
	}

	public void forwardLeft(float distance){
		velocity.setX(distance * -(float)Math.sin(Math.toRadians(yaw - 45)));
		velocity.setZ(distance * (float)Math.cos(Math.toRadians(yaw - 45)));
		for (CollisionBox c : Footsteps.cBoxes){
			if (c.contains(new Location(position.getX() + velocity.getX(), position.getY(), position.getZ()))){
				velocity.setX(0);
				break;
			}
			if (c.contains(new Location(position.getX(), position.getY(), position.getZ() + velocity.getZ()))){
				velocity.setZ(0);
				break;
			}
		}
	}

	public void forwardRight(float distance){
		velocity.setX(distance * -(float)Math.sin(Math.toRadians(yaw + 45)));
		velocity.setZ(distance * (float)Math.cos(Math.toRadians(yaw + 45)));
		for (CollisionBox c : Footsteps.cBoxes){
			if (c.contains(new Location(position.getX() + velocity.getX(), position.getY(), position.getZ()))){
				velocity.setX(0);
				break;
			}
			if (c.contains(new Location(position.getX(), position.getY(), position.getZ() + velocity.getZ()))){
				velocity.setZ(0);
				break;
			}
		}
	}

	public void backwardLeft(float distance){
		velocity.setX(distance * (float)Math.sin(Math.toRadians(yaw + 45)));
		velocity.setZ(distance * -(float)Math.cos(Math.toRadians(yaw + 45)));
		for (CollisionBox c : Footsteps.cBoxes){
			if (c.contains(new Location(position.getX() + velocity.getX(), position.getY(), position.getZ()))){
				velocity.setX(0);
				break;
			}
			if (c.contains(new Location(position.getX(), position.getY(), position.getZ() + velocity.getZ()))){
				velocity.setZ(0);
				break;
			}
		}
	}

	public void backwardRight(float distance){
		velocity.setX(distance * (float)Math.sin(Math.toRadians(yaw - 45)));
		velocity.setZ(distance * -(float)Math.cos(Math.toRadians(yaw - 45)));
		for (CollisionBox c : Footsteps.cBoxes){
			if (c.contains(new Location(position.getX() + velocity.getX(), position.getY(), position.getZ()))){
				velocity.setX(0);
				break;
			}
			if (c.contains(new Location(position.getX(), position.getY(), position.getZ() + velocity.getZ()))){
				velocity.setZ(0);
				break;
			}
		}
	}

	/**
	 * Moves the camera at a custom angle
	 * @param angle The angle at which to move on the x and z axes (-90 is left, 90 is right, 0 is straight ahead)
	 * @param distance The distance to move the camera
	 * @param backward Whether or not the camera should be moved backwards rather than forwards
	 */
	public void moveCustom(int angle, float distance, boolean backward){
		int zMult = 1;
		if (backward)
			zMult = -1;
		velocity.setX(distance * (-(float)Math.sin(Math.toRadians(yaw + (angle * zMult))) * zMult));
		velocity.setZ(distance * ((float)Math.cos(Math.toRadians(yaw + (angle * zMult))) * zMult));
		for (CollisionBox c : Footsteps.cBoxes){
			if (c.contains(new Location(position.getX() + velocity.getX(), position.getY(), position.getZ()))){
				velocity.setX(0);
				break;
			}
			if (c.contains(new Location(position.getX(), position.getY(), position.getZ() + velocity.getZ()))){
				velocity.setZ(0);
				break;
			}
		}
	}

	public void flyUp(float distance){
		velocity.setY(-distance);
		for (CollisionBox c : Footsteps.cBoxes){
			if (c.contains(new Location(position.getX() + velocity.getX(), position.getY(), position.getZ()))){
				velocity.setX(0);
				break;
			}
			if (c.contains(new Location(position.getX(), position.getY(), position.getZ() + velocity.getZ()))){
				velocity.setZ(0);
				break;
			}
		}
	}

	public void flyDown(float distance){
		velocity.setY(distance);
		for (CollisionBox c : Footsteps.cBoxes){
			if (c.contains(new Location(position.getX(), position.getY() + velocity.getY(), position.getZ()))){
				velocity.setY(0);
				Footsteps.jumping = false;
				break;
			}
			if (c.contains(new Location(position.getX(), position.getY() + velocity.getY(), position.getZ()))){
				velocity.setY(0);
				Footsteps.jumping = false;
				break;
			}
		}
	}

	public void freeze(){
		velocity.set(0, 0, 0);
	}

	public void freezeXAndZ(){
		velocity.setX(0);
		velocity.setZ(0);
	}

	public void lookThrough(){

		if (!Footsteps.ingameMenu){

			if (!moved){
				Random rand = new Random(10);
				pitch += pitchFade / 5 * rand.nextFloat();
				if (pitchFade > 0)
					pitchFade -= Footsteps.delta / 50 * rand.nextFloat();
				else
					pitchFade += Footsteps.delta / 50 * rand.nextFloat();


				yaw += yawFade / 5 * rand.nextFloat();
				if (yawFade > 0)
					yawFade -= Footsteps.delta / 50 * rand.nextFloat();
				else
					yawFade += Footsteps.delta / 50 * rand.nextFloat();
			}

			moved = false;

			position = add(position, velocity, true);
			GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
			GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
			GL11.glTranslatef(position.x, position.y, position.z);

		}
	}

	public Vector3f add(Vector3f vec, float x, float y, float z, boolean apply){
		Vector3f v = new Vector3f();
		v.setX(vec.getX() + x);
		v.setY(vec.getY() + y);
		v.setZ(vec.getZ() + z);
		if (apply)
			vec = v;
		return v;
	}

	public Vector3f subtract(Vector3f vec, float x, float y, float z, boolean apply){
		Vector3f v = new Vector3f();
		v.setX(vec.getX() - x);
		v.setY(vec.getY() - y);
		v.setZ(vec.getZ() - z);
		if (apply)
			vec = v;
		return v;
	}

	public Vector3f add(Vector3f vec, Vector3f vec2, boolean apply){
		Vector3f v = new Vector3f();
		v.setX(vec.getX() + vec2.getX());
		v.setY(vec.getY() + vec2.getY());
		v.setZ(vec.getZ() + vec2.getZ());
		if (apply)
			vec = v;
		return v;
	}

	public Vector3f subtract(Vector3f vec, Vector3f vec2, boolean apply){
		Vector3f v = new Vector3f();
		v.setX(vec.getX() - vec2.getX());
		v.setY(vec.getY() - vec2.getY());
		v.setZ(vec.getZ() - vec2.getZ());
		if (apply)
			vec = v;
		return v;
	}
}
