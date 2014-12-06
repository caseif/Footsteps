package net.amigocraft.footsteps;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class Sound {

	public static SoundSystem soundSystem;

	private String id;
	private String path;
	private Location loc;
	private Location velocity;
	private int source;
	private int buffer;

	public static void initialize(){
		try {
			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
		}
		catch (SoundSystemException ex){
			System.err.println("An exception occurred while linking the Codec-JOrbis plugin");
		}
		soundSystem = new SoundSystem();
	}

	public Sound(String identifier, String path, Location loc, Location velocity){
		this.id = identifier;
		this.path = path;
		this.loc = loc;
		this.velocity = velocity;
		soundSystem.loadSound(Sound.class.getResource(path), id);
		soundSystem.setPosition(id, loc.getX(), loc.getY(), loc.getZ());
		soundSystem.setVelocity(id, velocity.getX(), velocity.getY(), velocity.getZ());
	}
	
	public Sound(String identifier, String path, Location loc){
		this(identifier, path, loc, new Location(0, 0, 0));
	}
	
	public Sound(String identifier, String path){
		this(identifier, path, new Location(0, 0, 0));
	}

	/*public void initialize(){
		BufferedInputStream bIs = new BufferedInputStream(Sound.class.getResourceAsStream(path));
		WaveData data = WaveData.create(bIs);
		buffer = alGenBuffers();
		if (data == null)
			System.out.println("data");
		alBufferData(buffer, data.format, data.data, data.samplerate);
		data.dispose();
		source = alGenSources();
		alSourcei(source, AL_BUFFER, buffer);
	}*/

	public void play(){
		//alSource3f(source, AL_POSITION, loc.getX(), loc.getY(), loc.getZ());
		//alSource3f(source, AL_VELOCITY, velocity.getX(), velocity.getY(), velocity.getZ());
		//alSourcePlay(source);
		soundSystem.play(id);
	}

	public void dispose(){
		//alDeleteBuffers(buffer);
		soundSystem.unloadSound(id);
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
