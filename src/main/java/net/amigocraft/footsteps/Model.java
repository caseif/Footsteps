package net.amigocraft.footsteps;

import java.util.ArrayList;
import java.util.List;

import net.amigocraft.footsteps.util.Vector3f;

public class Model {

	public List<Vector3f> vertices = new ArrayList<>();
	public List<Vector3f> normals = new ArrayList<>();
	public List<Face> faces = new ArrayList<>();
	public List<float[]> textureCoords = new ArrayList<>();

	public boolean contains(Location l){
		for (Vector3f v : vertices){
			return Location.getLocation(v).approxEquals(l);
		}
		return false;
	}
}
