package net.caseif.footsteps;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbisAlloc;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedInputStream;
import java.io.IOException;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;

public class Sound {

	private String id;
	private String path;
	private Location loc;
	private Location velocity;
	private int source;
	private int buffer;

	public Sound(String identifier, String path, Location loc, Location velocity){
		this.id = identifier;
		this.path = path;
		this.loc = loc;
		this.velocity = velocity;
	}
	
	public Sound(String identifier, String path, Location loc){
		this(identifier, path, loc, new Location(0, 0, 0));
		initialize();
	}
	
	public Sound(String identifier, String path){
		this(identifier, path, new Location(0, 0, 0));
		initialize();
	}

	public void initialize(){
		try {
			BufferedInputStream bIs = new BufferedInputStream(Sound.class.getResourceAsStream(path));
			var soundBytes = bIs.readAllBytes();
			var soundBuf = BufferUtils.createByteBuffer(soundBytes.length);
			soundBuf.put(soundBytes);
			soundBuf.flip();
			var errBuf = BufferUtils.createIntBuffer(1);
			var allocBuf = BufferUtils.createByteBuffer(soundBytes.length);

			var soundHandle = stb_vorbis_open_memory(soundBuf, errBuf, null);
			if (soundHandle == 0) {
				System.err.println("Failed to open sound (rc " + errBuf.get() + ")");
				return;
			}

			try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
				stb_vorbis_get_info(soundHandle, info);

				var err = errBuf.get();
				if (err != 0) {
					System.err.println("STB Vorbis error: " + err);
					return;
				}

				buffer = alGenBuffers();
				int format;
				if (info.channels() == 1) {
					format = AL_FORMAT_MONO16;
				} else {
					format = AL_FORMAT_STEREO16;
				}

				var numSamples = stb_vorbis_stream_length_in_samples(soundHandle);

				var pcm = MemoryUtil.memAllocShort(numSamples);

				pcm.limit(stb_vorbis_get_samples_short_interleaved(soundHandle, info.channels(), pcm) * info.channels());

				alBufferData(buffer, format, pcm, info.sample_rate());
				source = alGenSources();
				alSourcei(source, AL_BUFFER, buffer);
			}

			stb_vorbis_close(soundHandle);
		} catch (IOException ex) {
			throw new RuntimeException("Failed to load sound", ex);
		}
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
