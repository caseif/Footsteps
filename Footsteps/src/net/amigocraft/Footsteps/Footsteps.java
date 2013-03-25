package net.amigocraft.Footsteps;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Footsteps implements ImageObserver {

	Camera camera = new Camera(250, 50, 150);
	float dx = 0.0f;
	float dy = 0.0f;
	float delta = 0;
	long lastTime = 0;
	long time = getTime();
	static Vector3f lightPosition = new Vector3f(-500f, -500f, 1000f);
	long lastPress = (int)System.currentTimeMillis();

	float mouseSensitivity = 0.05f;
	float movementSpeed = 20.0f;
	float maxPitch = 80;
	float minPitch = -80;
	public float jumpFrame = 0;
	public float jumpSpeedFrame = 0;
	public float jumpFreezeFrame = 0;

	public static boolean jumping = false;
	public boolean falling = true;
	public boolean left = false;
	public boolean right = false;
	public boolean forward = false;
	public boolean backward = false;
	public boolean ground = false;
	
	public boolean smoothing = false;
	public boolean debug = false;
	public boolean fullscreen = false;

	public int playerHeight = 10;
	public float gravity = 0.5f;
	public float jumpSpeed = 0.5f;
	public float jumpDistance = 13f;
	public float jumpFreezeLength = 2f;
	public float lastFps = 0f;
	public float currentTime = 0f;
	public int currentFps = 0;

	public boolean wireframe = false;
	public boolean colorize = false;
	public boolean textured = true;
	public boolean colorSky = true;

	List<Location> terrainCap = new ArrayList<Location>();
	public static List<CollisionBox> cBoxes = new ArrayList<CollisionBox>();

	public float[] skyColor = new float[]{0f, 0.7f, 0.9f, 1.1f};

	public Texture grassTexture;

	// GUI related variables
	private static UnicodeFont font;

	public static void main(String[] args){
		new Footsteps();
	}

	public Footsteps(){
		try {
			Display.setDisplayMode(new DisplayMode(Display.getDesktopDisplayMode().getWidth() - 20, Display.getDesktopDisplayMode().getHeight() - 100));
			Display.setTitle("Footsteps");
			ByteBuffer[] icons = null;
			if (System.getProperty("os.name").startsWith("Windows")){
				icons = new ByteBuffer[2];
				BufferedImage icon1 = scaleImage(ImageIO.read(Footsteps.class.getClassLoader().getResourceAsStream("images/icon.png")), 16, 16);
				BufferedImage icon2 = scaleImage(ImageIO.read(Footsteps.class.getClassLoader().getResourceAsStream("images/icon.png")), 32, 32);;
				icons[0] = asByteBuffer(icon1);
				icons[1] = asByteBuffer(icon2);
			}
			else if (System.getProperty("os.name").startsWith("Mac")){
				icons = new ByteBuffer[1];
				BufferedImage icon = scaleImage(ImageIO.read(Footsteps.class.getClassLoader().getResourceAsStream("images/icon.png")), 128, 128);
				icons[0] = asByteBuffer(icon);
			}
			else {
				icons = new ByteBuffer[1];
				BufferedImage icon = scaleImage(ImageIO.read(Footsteps.class.getClassLoader().getResourceAsStream("images/icon.png")), 32, 32);
				icons[0] = asByteBuffer(icon);
			}
			Display.setIcon(icons);
			Display.create();
		}
		catch (Exception ex){
			ex.printStackTrace();
		}

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(30f, 1280f / 720f, 0.001f, 1000f);
		GL11.glOrtho(1, 1, 1, 1, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_LIGHT0);
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, asFloatBuffer(new float[]{0.05f, 0.05f, 0.05f, 1f}));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, asFloatBuffer(new float[]{1.5f, 1.5f, 1.5f, 1f}));
		//GL11.glEnable(GL11.GL_CULL_FACE);
		//GL11.glCullFace(GL11.GL_FRONT);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0);

		if (colorSky)
			GL11.glClearColor(skyColor[0], skyColor[1], skyColor[2], skyColor[3]);

		try {
			grassTexture = TextureLoader.getTexture("PNG", this.getClass().getClassLoader().getResourceAsStream("images/grass.png"));
		}
		catch (Exception ex){
			ex.printStackTrace();
		}

		setUpFont();

		Mouse.setGrabbed(true);

		// this code is here as a reference for future models
		/*int bunnyListHandle = GL11.glGenLists(1);
		GL11.glNewList(bunnyListHandle, GL11.GL_COMPILE);
		{
			GL11.glBegin(GL11.GL_TRIANGLES);
			Model m = null;
			try {
				m = ObjLoader.loadModel(this.getClass().getClassLoader().getResourceAsStream("models/bunny.obj"));
			}
			catch (Exception ex){
				ex.printStackTrace();
				Display.destroy();
				System.exit(1);
			}

			GL11.glColor3f(0.35f, 0.2f, 0.1f);
			for (Face f : m.faces){
				Vector3f n1 = m.normals.get((int)f.normal.x - 1);
				GL11.glNormal3f(n1.x, n1.y, n1.z);
				Vector3f v1 = m.vertices.get((int)f.vertex.x - 1);
				GL11.glVertex3f(v1.x, v1.y, v1.z);
				Vector3f n2 = m.normals.get((int)f.normal.y - 1);
				GL11.glNormal3f(n2.x, n2.y, n2.z);
				Vector3f v2 = m.vertices.get((int)f.vertex.y - 1);
				GL11.glVertex3f(v2.x, v2.y, v2.z);
				Vector3f n3 = m.normals.get((int)f.normal.z - 1);
				GL11.glNormal3f(n3.x, n3.y, n3.z);
				Vector3f v3 = m.vertices.get((int)f.vertex.z - 1);
				GL11.glVertex3f(v3.x, v3.y, v3.z);
			}

			GL11.glEnd();
		}
		GL11.glEndList();*/

		int heightMapListHandle = GL11.glGenLists(1);
		GL11.glNewList(heightMapListHandle, GL11.GL_COMPILE);
		{
			BufferedImage hm = null;
			BufferedImage hmRef = null;
			try {
				hm = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("images/hm.png"));
				hmRef = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("images/hmColor.png"));
			}
			catch (IOException e){
				e.printStackTrace();
			}
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor3f(0.3f, 0.3f, 0.3f);
			GL11.glMaterialf(GL11.GL_BACK, GL11.GL_SHININESS, 1f);
			for (float x = 1; x < hm.getWidth(this); x++){
				for (float z = 1; z < hm.getHeight(this); z++){
					if (x < hm.getWidth(this) && z < hm.getHeight(this)){
						float yDivide = 5;

						// y1
						int rgb1 = hm.getRGB((int)x - 1, (int)z - 1);
						Color color1 = new Color(rgb1);
						int red1 = color1.getRed();
						int green1 = color1.getGreen();
						int blue1 = color1.getBlue();
						int shade1 = (red1 + green1 + blue1) / 3;
						float y1 = shade1 / yDivide;
						float pixel1 = (y1 * yDivide / 256) * hmRef.getWidth(this);
						Color c1 = new Color(hmRef.getRGB((int)pixel1, 0));
						float newRed1 = c1.getRed();
						float newGreen1 = c1.getGreen();
						float newBlue1 = c1.getBlue();

						// y2
						int rgb2 = hm.getRGB((int)x, (int)z - 1);
						Color color2 = new Color(rgb2);
						int red2 = color2.getRed();
						int green2 = color2.getGreen();
						int blue2 = color2.getBlue();
						int shade2 = (red2 + green2 + blue2) / 3;
						float y2 = shade2 / yDivide;

						// y3
						int rgb3 = hm.getRGB((int)x, (int)z);
						Color color3 = new Color(rgb3);
						int red3 = color3.getRed();
						int green3 = color3.getGreen();
						int blue3 = color3.getBlue();
						int shade3 = (red3 + green3 + blue3) / 3;
						float y3 = shade3 / yDivide;

						// y4
						int rgb4 = hm.getRGB((int)x - 1, (int)z);
						Color color4 = new Color(rgb4);
						int red4 = color4.getRed();
						int green4 = color4.getGreen();
						int blue4 = color4.getBlue();
						int shade4 = (red4 + green4 + blue4) / 3;
						float y4 = shade4 / yDivide;

						if (colorize)
							GL11.glColor3f(newRed1 / 256f, newGreen1 / 256f, newBlue1 / 256f);
						if (textured)
							GL11.glTexCoord2f(0, 0);
						GL11.glVertex3f(x, y1, z);
						if (textured)
							GL11.glTexCoord2f(1, 0);
						GL11.glVertex3f(x + 1f,y2, z);
						if (textured)
							GL11.glTexCoord2f(1, 1);
						GL11.glVertex3f(x + 1f, y3, z + 1f);
						if (textured)
							GL11.glTexCoord2f(0, 1);
						GL11.glVertex3f(x, y4, z + 1f);
						terrainCap.add(new Location(x, y1, z));
					}
				}
			}
			GL11.glEnd();
		}
		GL11.glEndList();

		while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

			if (wireframe)
				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			else
				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

			grassTexture.bind();
			GL11.glCallList(heightMapListHandle);
			//GL11.glCallList(bunnyListHandle);

			time = getTime();
			delta = time - lastTime;
			float percentOf60 = (1000 / delta) / 60;
			lastTime = time;

			dx = Mouse.getDX();
			dy = Mouse.getDY() * -1;

			updateFps();
			if (debug){
				drawString(10, 10, "fps: " + currentFps);
				drawString(10, 45, "x: " + camera.getX());
				drawString(10, 80, "y: " + camera.getY());
				drawString(10, 115, "z: " + camera.getZ());
				drawString(10, 150, "pitch: " + camera.getPitch());
				drawString(10, 185, "yaw: " + camera.getYaw());
				//drawString(10, 220, "ground: " + ground);
			}

			camera.setYaw(dx * mouseSensitivity);

			if (camera.pitch + (dy * mouseSensitivity) < maxPitch && camera.pitch + (dy * mouseSensitivity) > minPitch)
				camera.setPitch(dy * mouseSensitivity);

			if (Keyboard.isKeyDown(Keyboard.KEY_W))
				forward = true;
			else if (forward){
				camera.walkForward(0);
				forward = false;
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_S))
				backward = true;
			else if (backward){
				camera.walkBackward(0);
				backward = false;
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_A))
				left = true;
			else if (left){
				camera.strafeLeft(0);
				left = false;
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_D))
				right = true;
			else if (right){
				camera.strafeRight(0);
				right = false;
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
				if (!jumping && !falling){
					jumping = true;
					jumpFrame = 0;
				}
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_F)){
				if (System.currentTimeMillis() - lastPress > 200){
					if (wireframe)
						wireframe = false;
					else
						wireframe = true;
					lastPress = System.currentTimeMillis();
				}
			}

			/*if (Keyboard.isKeyDown(Keyboard.KEY_F11)){
				if (System.currentTimeMillis() - lastPress > 500){
					try {
						if (Display.isFullscreen())
							Display.setFullscreen(false);
						else {
							Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
						}
					}
					catch (LWJGLException ex){
						ex.printStackTrace();
					}
				}
			}*/

			if (Keyboard.isKeyDown(Keyboard.KEY_F3)){
				if (System.currentTimeMillis() - lastPress > 200){
					if (debug)
						debug = false;
					else
						debug = true;
					lastPress = System.currentTimeMillis();
				}
			}

			if (Mouse.isButtonDown(1)){
				if (System.currentTimeMillis() - lastPress > 200){
					if (Mouse.isGrabbed())
						Mouse.setGrabbed(false);
					else if (!Mouse.isGrabbed())
						Mouse.setGrabbed(true);
					lastPress = System.currentTimeMillis();
				}
			}

			/*if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				camera.flyDown(movementSpeed * delta);*/

			if (Keyboard.isKeyDown(Keyboard.KEY_L))
				lightPosition = new Vector3f(camera.position.x * -1, camera.position.y * -1, camera.position.z * -1);

			for (Location l : terrainCap){
				if (l.xZEquals(new Location((int)camera.position.x * -1, (int)camera.position.y * -1, (int)camera.position.z * -1))){
					if (camera.position.y + playerHeight + l.getY() <= 0 && camera.position.y + playerHeight + l.getY() >= -1){
						falling = false;
						break;
					}
					else if (camera.position.y + playerHeight + l.getY() > 0){
						falling = false;
						moveCameraSmooth(new Location(camera.position.x, camera.position.y, camera.position.z), new Location(camera.position.x, (l.getY() * -1) - playerHeight, camera.position.z), 500);
						break;
					}
					else {
						falling = true;
						break;
					}
				}
			}
			if (falling && !jumping){
				camera.flyDown(gravity / percentOf60);
				/*for (Location l : terrainCap){
					if (l.xZEquals(new Location((int)camera.position.x * -1, (int)camera.position.y * -1, (int)camera.position.z * -1))){
						if (l.getY() - camera.getY() > 10){
							ground = false;
							System.out.println(l.getY() - camera.getY());
							break;
						}
					}
				}*/
			}
			else if (jumping){
				ground = false;
				if (jumpFrame < jumpDistance){
					if (jumpDistance - 1f == jumpFrame)
						camera.flyUp(jumpSpeed / 2 / percentOf60);
					else
						camera.flyUp(jumpSpeed / percentOf60);
					jumpFrame += percentOf60;
				}
				else if (jumpFreezeFrame < jumpFreezeLength){
					jumpFrame += percentOf60;
					jumpFreezeFrame += percentOf60;
				}
				else if (jumpFreezeFrame == jumpFreezeLength){
					camera.flyDown(gravity / 2 / percentOf60);
					jumpFreezeFrame += percentOf60;
				}
				else {
					jumping = false;
					falling = true;
					jumpFrame = 0;
					jumpFreezeFrame = 0;
				}
			}
			else {
				camera.velocity.setY(0);
				ground = true;
			}

			if (forward && left)
				camera.forwardLeft(movementSpeed * delta / 1000);
			else if (forward && right)
				camera.forwardRight(movementSpeed * delta / 1000);
			else if (backward && left)
				camera.backwardLeft(movementSpeed * delta / 1000);
			else if (backward && right)
				camera.backwardRight(movementSpeed * delta / 1000);
			else if (left)
				camera.strafeLeft(movementSpeed * delta / 1000);
			else if (right)
				camera.strafeRight(movementSpeed * delta / 1000);
			else if (forward)
				camera.walkForward(movementSpeed * delta / 1000);
			else if (backward)
				camera.walkBackward(movementSpeed * delta / 1000);

			GL11.glLoadIdentity();

			camera.lookThrough();

			GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, asFloatBuffer(new float[]{lightPosition.x, lightPosition.y, lightPosition.z, 1f}));

			Display.sync(60);

			Display.update();
		}
		Display.destroy();
	}

	private static FloatBuffer asFloatBuffer(float[] f){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(f.length);
		buffer.put(f);
		buffer.flip();
		return buffer;
	}

	@Override
	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5){
		return false;
	}

	public void moveCameraSmooth(Location oldLoc, Location newLoc, int stages){
		float xDiff = newLoc.getX() - oldLoc.getX();
		float yDiff = newLoc.getY() - oldLoc.getY();
		float zDiff = newLoc.getZ() - oldLoc.getZ();

		float xPerStage = xDiff / stages;
		float yPerStage = yDiff / stages;
		float zPerStage = zDiff / stages;

		for (int i = 0; i < stages; i++){
			camera.position.x += xPerStage;
			camera.position.y += yPerStage;
			camera.position.z += zPerStage;
		}
	}

	@SuppressWarnings("unchecked")
	private static void setUpFont(){
		float size = 36F;
		Font awtFont = new Font("Verdana", Font.BOLD, (int)size);
		font = new UnicodeFont(awtFont.deriveFont(0, size));
		font.addAsciiGlyphs();
		ColorEffect e = new ColorEffect();
		e.setColor(Color.RED);
		font.getEffects().add(e);
		try {
			font.loadGlyphs();
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}

	public void drawString(int x, int y, String str){
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1, 1 );
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glDisable(GL11.GL_LIGHTING);
		if (wireframe)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		font.drawString(x, y, str);
		if (wireframe)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
	}

	public BufferedImage scaleImage(BufferedImage img, int width, int height){
		BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)newImg.getGraphics();
		g.scale(((double)width / (double)img.getWidth()), ((double)height / (double)img.getHeight()));
		g.drawImage(img, 0, 0, this);
		g.dispose();
		return newImg;
	}

	public ByteBuffer asByteBuffer(BufferedImage img){
		ByteBuffer buffer = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4);
		for(int y = 0; y < img.getHeight(); y++){
			for(int x = 0; x < img.getWidth(); x++){
				buffer.put((byte)(new Color(img.getRGB(x, y), true).getRed() & 0xFF));
				buffer.put((byte)(new Color(img.getRGB(x, y), true).getGreen() & 0xFF));
				buffer.put((byte)(new Color(img.getRGB(x, y), true).getBlue() & 0xFF));
				buffer.put((byte)(new Color(img.getRGB(x, y), true).getAlpha() & 0xFF));
			}
		}
		buffer.flip();
		return buffer; 
	}

	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	public void updateFps(){
		if (time - lastFps > 1000){
			currentFps = (int)(1000 / delta);
			lastFps = time;
		}
	}
}
