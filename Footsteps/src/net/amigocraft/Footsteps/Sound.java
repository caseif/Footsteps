package net.amigocraft.Footsteps;

import java.io.BufferedInputStream;

import static org.lwjgl.openal.AL10.*;

import org.lwjgl.util.WaveData;

public class Sound {

	private String path;
	private Location loc;
	private Location velocity;
	private int source;
	private int buffer;

	public Sound(String path, Location loc, Location velocity){
		this.path = path;
		this.loc = loc;
		this.velocity = velocity;
	}
	
	public Sound(String path, Location loc){
		this.path = path;
		this.loc = loc;
		this.velocity = new Location(0, 0, 0);
	}
	
	public Sound(String path){
		this.path = path;
		this.loc = new Location(0, 0, 0);
		this.velocity = new Location(0, 0, 0);
	}

	public void initialize(){
		WaveData data = WaveData.create(new BufferedInputStream(Sound.class.getClassLoader().getResourceAsStream(path)));
		buffer = alGenBuffers();
		alBufferData(buffer, data.format, data.data, data.samplerate);
		data.dispose();
		source = alGenSources();
		alSourcei(source, AL_BUFFER, buffer);
	}

	public void play(){
		alSource3f(source, AL_POSITION, loc.getX(), loc.getY(), loc.getZ());
		alSource3f(source, AL_VELOCITY, velocity.getX(), velocity.getY(), velocity.getZ());
		alSourcePlay(source);
	}

	public void dispose(){
		alDeleteBuffers(buffer);
	}

	public String getPath(){
		return path;
	}

	public Location getLocation(){
		return loc;
	}
	
	public Location getVelocity(){
		return velocity;
	}

	public float getX(){
		return loc.getX();
	}

	public float getY(){
		return loc.getY();
	}

	public float getZ(){
		return loc.getZ();
	}
	
	public void setLocation(Location l){
		this.loc = l;
	}
	
	public void setVelocity(Location l){
		this.velocity = l;
	}
	
	public void setX(float x){
		this.loc.setZ(x);
	}
	
	public void setY(float y){
		this.loc.setY(y);
	}
	
	public void setZ(float z){
		this.loc.setZ(z);
	}

	public int getBuffer(){
		return buffer;
	}

	public int getSource(){
		return source;
	}

}
