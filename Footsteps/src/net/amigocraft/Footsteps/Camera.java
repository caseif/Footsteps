package net.amigocraft.Footsteps;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	Vector3f position = null;
	Vector3f velocity = null;
	//the rotation around the Y axis of the camera
	float yaw = 0.0f;
	//the rotation around the X axis of the camera
	public float pitch = 0.0f;

	public Camera(float x, float y, float z){
		position = new Vector3f(x * -1f, y * -1f, z * -1f);
		velocity = new Vector3f(0f, 0f, 0f);
	}

	public void setPitch(float amount){
		pitch += amount;
	}

	public void setYaw(float amount){
		yaw += amount;
	}

	public void walkForward(float distance){
		velocity.setX(distance * -(float)Math.sin(Math.toRadians(yaw)));
		velocity.setZ(distance * (float)Math.cos(Math.toRadians(yaw)));
	}

	public void walkBackward(float distance){
		velocity.setX(distance * (float)Math.sin(Math.toRadians(yaw)));
		velocity.setZ(distance * -(float)Math.cos(Math.toRadians(yaw)));
	}

	public void strafeLeft(float distance){
		velocity.setX(distance * -(float)Math.sin(Math.toRadians(yaw - 90)));
		velocity.setZ(distance * (float)Math.cos(Math.toRadians(yaw - 90)));
	}

	public void strafeRight(float distance){
		velocity.setX(distance * -(float)Math.sin(Math.toRadians(yaw + 90)));
		velocity.setZ(distance * (float)Math.cos(Math.toRadians(yaw + 90)));
	}

	public void flyUp(float distance){
		velocity.setY(-distance);
	}

	public void flyDown(float distance){
		velocity.setY(distance);
	}

	public void lookThrough(){
		position.setX(position.getX() + velocity.getX());
		position.setY(position.getY() + velocity.getY());
		position.setZ(position.getZ() + velocity.getZ());
		GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
		GL11.glTranslatef(position.x, position.y, position.z);
	}
}
