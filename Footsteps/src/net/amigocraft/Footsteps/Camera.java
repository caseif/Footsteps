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
		pitch += amount;
	}

	public void setYaw(float amount){
		yaw += amount;
		if (yaw >= 360)
			yaw -= 360;
		if (yaw < 0)
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

	public void lookThrough(){
		position.setX(position.getX() + velocity.getX());
		position.setY(position.getY() + velocity.getY());
		position.setZ(position.getZ() + velocity.getZ());
		GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
		GL11.glTranslatef(position.x, position.y, position.z);
	}
}
