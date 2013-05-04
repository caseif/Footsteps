package net.amigocraft.Footsteps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class ObjLoader {

	public static Model loadModel(String path) throws IOException {
		InputStream getMtlIs = Footsteps.class.getClassLoader().getResourceAsStream(path);
		BufferedReader getMtlReader = new BufferedReader(new InputStreamReader(getMtlIs));
		String getMtlLine;
		boolean mtlLib = false;
		InputStream mtlIs = null;
		while ((getMtlLine = getMtlReader.readLine()) != null){
			if (getMtlLine.startsWith("mtllib") && !mtlLib){
				getMtlLine.replace("mtllib ", "");
				String mtlLibPath = "";
				String[] mtlPathArray = path.split("/");
				int i = 0;
				while (i < mtlPathArray.length - 1){
					mtlLibPath += mtlPathArray[i] + "/";
					i += 1;
				}
				mtlLibPath += getMtlLine.replace("mtllib ", "");
				mtlIs = Footsteps.class.getClassLoader().getResourceAsStream(mtlLibPath);
				if (mtlIs != null)
					mtlLib = true;
				break;
			}
		}
		getMtlReader.close();

		List<Material> mats = new ArrayList<Material>();
		if (mtlLib){
			BufferedReader mtlReader = new BufferedReader(new InputStreamReader(mtlIs));
			String mtlLine;
			Material currentMat = new Material();
			while ((mtlLine = mtlReader.readLine()) != null){
				if (mtlLine.startsWith("newmtl "))
					currentMat.setName(mtlLine.split(" ")[1]);
				else if (mtlLine.startsWith("Ka "))
					currentMat.setAmbientLight(new float[]{Float.valueOf(mtlLine.split(" ")[1]),
							Float.valueOf(mtlLine.split(" ")[2]),
							Float.valueOf(mtlLine.split(" ")[3])});
				else if (mtlLine.startsWith("Kd "))
					currentMat.setDiffuseLight(new float[]{Float.valueOf(mtlLine.split(" ")[1]),
							Float.valueOf(mtlLine.split(" ")[2]),
							Float.valueOf(mtlLine.split(" ")[3])});
				else if (mtlLine.startsWith("Ks "))
					currentMat.setSpecularLight(new float[]{Float.valueOf(mtlLine.split(" ")[1]),
							Float.valueOf(mtlLine.split(" ")[2]),
							Float.valueOf(mtlLine.split(" ")[3])});
				else if (mtlLine.startsWith("d ") || mtlLine.startsWith("Tr "))
					currentMat.setTransparency(Float.valueOf(mtlLine.split(" ")[1]));
				else if (mtlLine.startsWith("illum "))
					currentMat.setIllumination(Float.valueOf(mtlLine.split(" ")[1]));
				else if (mtlLine.startsWith("map_Kd ")){
					String texPath = "";
					String[] texPathArray = path.split("/");
					int i = 0;
					while (i < texPathArray.length - 1){
						texPath += texPathArray[i] + "/";
						i += 1;
					}
					texPath += mtlLine.split(" ")[1];
					currentMat.setTexture(texPath);
					mats.add(currentMat);
					currentMat = new Material();
				}
			}
			mtlReader.close();
			mtlIs.close();
		}

		InputStream is = Footsteps.class.getClassLoader().getResourceAsStream(path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		Model m = new Model();
		String line;
		try {
			Material currentMat = new Material();
			boolean material = false;
			boolean tex = false;
			while ((line = reader.readLine()) != null){
				String[] array = line.split(" ");
				if (line.startsWith("v ")){
					// vertex
					float x = Float.valueOf(array[1]);
					float y = Float.valueOf(array[2]);
					float z = Float.valueOf(array[3]);
					m.vertices.add(new Vector3f(x, y, z));
				}
				else if (line.startsWith("vn ")){
					// normal
					float x = Float.valueOf(array[1]);
					float y = Float.valueOf(array[2]);
					float z = Float.valueOf(array[3]);
					m.normals.add(new Vector3f(x, y, z));
				}
				else if (line.startsWith("vt ")){
					// texture coord
					float x = Float.valueOf(array[1]);
					float y = Float.valueOf(array[2]);
					m.textureCoords.add(new float[]{x, y});
				}
				else if (line.startsWith("f ")){
					// face
					Vector3f vertexIndices = new Vector3f(
							Float.valueOf(array[1].split("/")[0]),
							Float.valueOf(array[2].split("/")[0]),
							Float.valueOf(array[3].split("/")[0]));
					Vector3f textureIndices = new Vector3f();
					if (!array[1].split("/")[1].isEmpty() && !array[2].split("/")[1].isEmpty() && !array[3].split("/")[1].isEmpty()){
						textureIndices = new Vector3f(
								Float.valueOf(array[1].split("/")[1]),
								Float.valueOf(array[2].split("/")[1]),
								Float.valueOf(array[3].split("/")[1]));
						tex = true;
					}
					Vector3f normalIndices = new Vector3f(
							Float.valueOf(array[1].split("/")[2]),
							Float.valueOf(array[2].split("/")[2]),
							Float.valueOf(array[3].split("/")[2]));
					if (tex && material){
						m.faces.add(new Face(vertexIndices, normalIndices, textureIndices, currentMat));
						m.textures.add(textureIndices);
					}
					else
						m.faces.add(new Face(vertexIndices, normalIndices));
				}
				else if (line.startsWith("usemtl ")){
					for (Material mat : mats){
						if (mat.getName().equals(line.split(" ")[1])){
							currentMat = mat;
							material = true;
							break;
						}
					}
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
