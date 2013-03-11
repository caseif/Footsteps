package net.amigocraft.Footsteps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.lwjgl.util.vector.Vector3f;

public class ObjLoader {

	public static Model loadModel(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		Model m = new Model();
		String line;
		try {
			while ((line = reader.readLine()) != null){
				if (line.startsWith("v ")){
					// vertex
					float x = Float.valueOf(line.split(" ")[1]);
					float y = Float.valueOf(line.split(" ")[2]);
					float z = Float.valueOf(line.split(" ")[3]);
					m.vertices.add(new Vector3f(x, y, z));
				}
				else if (line.startsWith("vn ")){
					// normal
					float x = Float.valueOf(line.split(" ")[1]);
					float y = Float.valueOf(line.split(" ")[2]);
					float z = Float.valueOf(line.split(" ")[3]);
					m.normals.add(new Vector3f(x, y, z));
				}
				else if (line.startsWith("f ")){
					// face
					Vector3f vertexIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[0]),
							Float.valueOf(line.split(" ")[2].split("/")[0]),
							Float.valueOf(line.split(" ")[3].split("/")[0]));
					Vector3f normalIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[2]),
							Float.valueOf(line.split(" ")[2].split("/")[2]),
							Float.valueOf(line.split(" ")[3].split("/")[2]));
					m.faces.add(new Face(vertexIndices, normalIndices));
				}
			}
			reader.close();
		}
		catch (Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
		return m;
	}

}
