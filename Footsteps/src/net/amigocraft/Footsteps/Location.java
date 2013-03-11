package net.amigocraft.Footsteps;

public class Location {
	
	private int x;
	private int y;
	private int z;
	
	public Location(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}

	public int getZ(){
		return this.z;
	}
	
	public boolean equals(Location l){
		return this.x == l.getX() && this.y == l.getY() && this.z == l.getZ();
	}
	
	public int hashCode(){
		return 41 * (x + y + z + 41);
	}
	
	public boolean xZEquals(Location l){
		return x == l.getX() && z == l.getZ();
	}
	
}
