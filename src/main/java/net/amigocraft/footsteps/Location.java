package net.amigocraft.footsteps;

import net.amigocraft.footsteps.util.Vector3f;

public class Location {

	private float x;
	private float y;
	private float z;

	public Location(){}

	public Location(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static Location getLocation(Vector3f v){
		return new Location(v.getX(), v.getY(), v.getZ());
	}

	public float getX(){
		return this.x;
	}

	public float getY(){
		return this.y;
	}

	public float getZ(){
		return this.z;
	}
	
	public void setX(float x){
		this.x = x;
	}
	
	public void setY(float y){
		this.y = y;
	}
	
	public void setZ(float z){
		this.z = z;
	}

	public boolean equals(Location l){
		return this.x == l.getX() && this.y == l.getY() && this.z == l.getZ();
	}

	public int hashCode(){
		return 41 * (int)(x + y + z + 41);
	}

	public boolean xZEquals(Location l){
		return x == l.getX() && z == l.getZ();
	}

	public boolean approxEquals(Location l){
		int x1 = (int)this.x;
		int y1 = (int)this.y;
		int z1 = (int)this.z;

		int x2 = (int)l.getX();
		int y2 = (int)l.getY();
		int z2 = (int)l.getZ();

		if (x1 == x2 && y1 == y2 && z1 == z2)
			return true;

		return false;
	}
}
