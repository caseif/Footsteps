package net.amigocraft.Footsteps;

public class Location {
	
	private float x;
	private float y;
	private float z;
	
	public Location(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
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
	
	public boolean equals(Location l){
		return this.x == l.getX() && this.y == l.getY() && this.z == l.getZ();
	}
	
	public int hashCode(){
		return 41 * (int)(x + y + z + 41);
	}
	
	public boolean xZEquals(Location l){
		return x == l.getX() && z == l.getZ();
	}	
}
