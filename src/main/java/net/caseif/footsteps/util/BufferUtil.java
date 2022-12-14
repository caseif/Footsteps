package net.caseif.footsteps.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class BufferUtil {
	public static ByteBuffer asByteBuffer(BufferedImage img){
		ByteBuffer buffer = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4);
		for(int y = 0; y < img.getHeight(); y++){
			for(int x = 0; x < img.getWidth(); x++){
				buffer.put((byte)(new Color(img.getRGB(x, y), true).getRed() & 0xFF));
				buffer.put((byte)(new Color(img.getRGB(x, y), true).getGreen() & 0xFF));
				buffer.put((byte)(new Color(img.getRGB(x, y), true).getBlue() & 0xFF));
				buffer.put((byte)(new Color(img.getRGB(x, y), true).getAlpha() & 0xFF));
			}
		}
		((Buffer) buffer).flip();
		return buffer; 
	}
	
	public static ByteBuffer asByteBuffer(byte[] b){
		ByteBuffer buffer = BufferUtils.createByteBuffer(b.length);
		buffer.put(b);
		((Buffer) buffer).flip();
		buffer.order();
		return buffer;
	}
	
	public static FloatBuffer asFloatBuffer(float[] f){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(f.length);
		buffer.put(f);
		((Buffer) buffer).flip();
		buffer.order();
		return buffer;
	}
	
	public static DoubleBuffer asDoubleBuffer(double[] d){
		DoubleBuffer buffer = BufferUtils.createDoubleBuffer(d.length);
		buffer.put(d);
		((Buffer) buffer).flip();
		buffer.order();
		return buffer;
	}

	public static IntBuffer asIntBuffer(int[] i){
		IntBuffer buffer = BufferUtils.createIntBuffer(i.length);
		buffer.put(i);
		((Buffer) buffer).flip();
		buffer.order();
		return buffer;
	}
}
