package net.amigocraft.Footsteps;

public class Face {

	public int[] vertex = null;
	public int[] normal = null;
	public Material material = new Material();
	public int[] texture = null;

	public Face(int[] vertex, int[] normal, int[] texture, Material mat){
		this.vertex = vertex;
		this.normal = normal;
		this.material = mat;
		this.texture = texture;
	}
	
	public Face(int[] vertex, int[] normal, Material mat){
		this.vertex = vertex;
		this.normal = normal;
		this.material = mat;
	}
	
	public Face(int[] vertex, int[] normal){
		this.vertex = vertex;
		this.normal = normal;
	}
}
