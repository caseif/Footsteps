package net.amigocraft.footsteps;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class Model {

	public List<Vector3f> vertices = new ArrayList<Vector3f>();
	public List<Vector3f> normals = new ArrayList<Vector3f>();
	public List<Face> faces = new ArrayList<Face>();
	public List<float[]> textureCoords = new ArrayList<float[]>();

	public boolean contains(Location l){
		for (Vector3f v : vertices){
			return Location.getLocation(v).approxEquals(l);
		}
		return false;
	}
}
