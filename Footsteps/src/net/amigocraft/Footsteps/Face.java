package net.amigocraft.Footsteps;

import org.lwjgl.util.vector.Vector3f;

public class Face {

	public Vector3f vertex = new Vector3f();
	public Vector3f normal = new Vector3f();
	public Material material = new Material();
	public Vector3f texture = new Vector3f();

	public Face(Vector3f vertex, Vector3f normal, Vector3f texture, Material mat){
		this.vertex = vertex;
		this.normal = normal;
		this.material = mat;
		this.texture = texture;
	}
	
	public Face(Vector3f vertex, Vector3f normal, Material mat){
		this.vertex = vertex;
		this.normal = normal;
		this.material = mat;
	}
	
	public Face(Vector3f vertex, Vector3f normal){
		this.vertex = vertex;
		this.normal = normal;
	}
}
