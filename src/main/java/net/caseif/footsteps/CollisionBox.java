package net.caseif.footsteps;

public class CollisionBox {

	private Location p1;
	private Location p2;

	public CollisionBox(Location p1, Location p2, Location p3, Location p4){
		this.p1 = p1;
		this.p2 = p2;
	}

	public Location getP1(){
		return this.p1;
	}

	public Location getP2(){
		return this.p2;
	}
	
	public float getHeight(){
		if (p1.getY() < p2.getY())
			return p2.getY() - p1.getY();
		return p1.getY() - p2.getY();
	}
	
	public float getWidth(){
		if (p1.getX() < p2.getX())
			return p2.getX() - p1.getX();
		return p1.getX() - p2.getX();
	}
	
	public float getDepth(){
		if (p1.getZ() < p2.getZ())
			return p2.getZ() - p1.getZ();
		return p1.getZ() - p2.getZ();
	}

	public boolean equals(Object o){
		if (o instanceof CollisionBox){
			CollisionBox b = (CollisionBox)o;
			return p1.equals(b.getP1()) && p2.equals(b.getP2());
		}
		return false;
	}

	public int hashCode(){
		return 41 * (41 + p1.hashCode() + p2.hashCode());
	}

	public boolean contains(Location l){
		float minX = p1.getX();
		float minY = p1.getY();
		float minZ = p1.getZ();
		float maxX = p2.getX();
		float maxY = p2.getY();
		float maxZ = p2.getZ();
		if (p1.getX() > p2.getX()){
			minX = p2.getX();
			maxX = p1.getX();
		}
		if (p1.getY() > p2.getY()){
			minY = p2.getY();
			maxY = p1.getY();
		}
		if (p1.getZ() > p2.getZ()){
			minZ = p2.getZ();
			maxZ = p1.getZ();
		}
		
		if (l.getX() >= minX && l.getX() <= maxX &&
				l.getY() >= minY && l.getY() <= maxY &&
				l.getZ() >= minZ && l.getZ() <= maxZ)
			return true;
		return false;
	}
}
