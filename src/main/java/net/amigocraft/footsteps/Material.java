package net.amigocraft.footsteps;

public class Material {

	private String name;
	private float[] ambientLight;
	private float[] diffuseLight;
	private float[] specularLight;
	private float transparency;
	private float illumination;
	private String texture;

	public Material(){}
	
	public Material(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public float[] getAmbientLight(){
		return ambientLight;
	}
	
	public float[] getDiffuseLight(){
		return diffuseLight;
	}
	
	public float[] getSpecularLight(){
		return specularLight;
	}
	
	public float getTransparency(){
		return transparency;
	}
	
	public float getIllumination(){
		return illumination;
	}
	
	public String getTexture(){
		return texture;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setAmbientLight(float[] l){
		this.ambientLight = l;
	}
	
	public void setDiffuseLight(float[] l){
		this.diffuseLight = l;
	}
	
	public void setSpecularLight(float[] l){
		this.specularLight = l;
	}
	
	public void setTransparency(float t){
		this.transparency = t;
	}
	
	public void setIllumination(float i){
		this.illumination = i;
	}
	
	public void setTexture(String t){
		this.texture = t;
	}
	
	public boolean equals(Object obj){
		Material mat = (Material)obj;
		return name.equals(mat.getName()) &&
				ambientLight == mat.getAmbientLight() &&
				diffuseLight == mat.getDiffuseLight() &&
				specularLight == mat.getSpecularLight() &&
				transparency == mat.getTransparency() &&
				illumination == mat.getIllumination() &&
				texture.equals(mat.getTexture());
	}
	
	/*public int hashCode(){
		return 41 * (int)(((ambientLight[0] + ambientLight[1] + ambientLight[2]) * 100) +
				((diffuseLight[0] + diffuseLight[1] + diffuseLight[2]) * 100) +
				((specularLight[0] + specularLight[1] + specularLight[2]) * 100) +
				(transparency * 100) + (illumination * 100) + name.hashCode() + texture.hashCode() + 41);
	}*/
	
}
