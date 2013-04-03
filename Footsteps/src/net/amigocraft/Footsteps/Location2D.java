package net.amigocraft.Footsteps;

public class Location2D {
	
	private float x;
	private float y;
	
	public Location2D(float x, float y){
		this.x = x;
		this.y = y;
	}

	public float getX(){
		return this.x;
	}
	
	public float getY(){
		return this.y;
	}
	
	public boolean equals(Location l){
		return this.x == l.getX() && this.y == l.getY();
	}
	
	public int hashCode(){
		return 41 * (int)(x + y + 41);
	}	
}
