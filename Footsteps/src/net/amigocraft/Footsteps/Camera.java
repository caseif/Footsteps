package net.amigocraft.Footsteps;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	Vector3f position = null;
	//the rotation around the Y axis of the camera
	private float yaw = 0.0f;
	//the rotation around the X axis of the camera
	public float pitch = 0.0f;

	public Camera(float x, float y, float z){
		position = new Vector3f(x * -1f, y * -1f, z * -1f);
	}
	
	public void setPitch(float amount){
	    pitch += amount;
	}
	
	public void setYaw(float amount){
	    yaw += amount;
	}
	
	public void walkForward(float distance){
	    position.x -= distance * (float)Math.sin(Math.toRadians(yaw));
	    position.z += distance * (float)Math.cos(Math.toRadians(yaw));
	}
	
	public void walkBackwards(float distance){
	    position.x += distance * (float)Math.sin(Math.toRadians(yaw));
	    position.z -= distance * (float)Math.cos(Math.toRadians(yaw));
	}
	
	public void strafeLeft(float distance){
	    position.x -= distance * (float)Math.sin(Math.toRadians(yaw-90));
	    position.z += distance * (float)Math.cos(Math.toRadians(yaw-90));
	}
	
	public void strafeRight(float distance){
	    position.x -= distance * (float)Math.sin(Math.toRadians(yaw+90));
	    position.z += distance * (float)Math.cos(Math.toRadians(yaw+90));
	}
	
	public void flyUp(float distance){
		position.y -= distance;
	}
	
	public void flyDown(float distance){
		position.y += distance;
	}
	
	public void lookThrough(){
        GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(position.x, position.y, position.z);
    }
}
