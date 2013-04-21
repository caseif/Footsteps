package net.amigocraft.Footsteps;

import java.io.BufferedInputStream;

import static org.lwjgl.openal.AL10.*;

import org.lwjgl.util.WaveData;

public class Sound {

	private String path;
	private Location loc;
	private int source;
	private int buffer;

	public Sound(String path, Location loc){
		this.path = path;
		this.loc = loc;
	}

	public void initialize(){
		WaveData data = WaveData.create(new BufferedInputStream(Sound.class.getClassLoader().getResourceAsStream(path)));
		buffer = alGenBuffers();
		alBufferData(buffer, data.format, data.data, data.samplerate);
		data.dispose();
		source = alGenSources();
		alSourcei(source, AL_BUFFER, buffer);
		alSource3f(source, AL_POSITION, loc.getX(), loc.getY(), loc.getZ());
	}

	public void play(){
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

	public float getX(){
		return loc.getX();
	}

	public float getY(){
		return loc.getY();
	}

	public float getZ(){
		return loc.getZ();
	}

	public int getBuffer(){
		return buffer;
	}

	public int getSource(){
		return source;
	}

}
