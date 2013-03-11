package net.amigocraft.Footsteps;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Footsteps implements ImageObserver {

	Camera camera = new Camera(250, 50, 150);
	float dx = 0.0f;
	float dy = 0.0f;
	float dt = 0.0f;
	float lastTime = 0.0f;
	float time = System.nanoTime();
	static Vector3f lightPosition = new Vector3f(-500f, -500f, 1000f);
	long lastPress = (int)System.currentTimeMillis();

	float mouseSensitivity = 0.05f;
	float movementSpeed = 20.0f;
	float maxPitch = 90;
	float minPitch = -90;
	public float jumpFrame = 0;
	public float jumpFreezeFrame = 0;
	public float increase = 0;

	public boolean jumping = false;
	public boolean falling = true;
	public boolean smoothing = false;

	public int playerHeight = 10;
	public float gravity = 0.5f;
	public float jumpSpeed = 0.5f;
	public float jumpDistance = 10;
	public float jumpFreezeLength = 1;

	public boolean wireframe = false;
	public boolean colorize = false;
	public boolean textured = true;
	public boolean colorSky = true;

	List<Location> terrainCap = new ArrayList<Location>();

	public Texture texture;

	public static void main(String[] args){
		new Footsteps();
	}

	public Footsteps(){
		try {
			Display.setDisplayMode(new DisplayMode(1280, 720));
			Display.setTitle("3D Demo");
			Display.create();
		}
		catch (LWJGLException ex){
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

		if (colorSky)
			GL11.glClearColor(0, 0.7f, 0.9f, 1.1f);

		try {
			texture = TextureLoader.getTexture("PNG", this.getClass().getClassLoader().getResourceAsStream("images/grass.png"));
		}
		catch (Exception ex){
			ex.printStackTrace();
		}

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
			if (textured)
				texture.bind();
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

			GL11.glCallList(heightMapListHandle);

			time = System.nanoTime();
			dt = (time - lastTime) / 1000000000f;
			float percentOf60 = (1 / (float)60) / dt;
			lastTime = time;

			dx = Mouse.getDX();
			dy = Mouse.getDY() * -1;


			camera.setYaw(dx * mouseSensitivity);

			if (camera.pitch + (dy * mouseSensitivity) < maxPitch && camera.pitch + (dy * mouseSensitivity) > minPitch)
				camera.setPitch(dy * mouseSensitivity);

			if (Keyboard.isKeyDown(Keyboard.KEY_W))
				camera.walkForward(movementSpeed * dt);

			if (Keyboard.isKeyDown(Keyboard.KEY_S))
				camera.walkBackwards(movementSpeed * dt);

			if (Keyboard.isKeyDown(Keyboard.KEY_A))
				camera.strafeLeft(movementSpeed * dt);

			if (Keyboard.isKeyDown(Keyboard.KEY_D))
				camera.strafeRight(movementSpeed * dt);

			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
				if (!jumping){
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

			/*if (Keyboard.isKeyDown(Keyboard.KEY_C)){
				if (System.currentTimeMillis() - lastPress > 200){
					if (colorize)
						colorize = false;
					else
						colorize = true;
					lastPress = System.currentTimeMillis();
				}
				System.out.println(colorize);
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_T)){
				if (System.currentTimeMillis() - lastPress > 200){
					if (textured)
						textured = false;
					else
						textured = true;
					lastPress = System.currentTimeMillis();
				}
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_K)){
				if (System.currentTimeMillis() - lastPress > 200){
					if (colorSky)
						colorSky = false;
					else
						colorSky = true;
					lastPress = System.currentTimeMillis();
				}
			}*/

			if (Mouse.isButtonDown(1)){
				if (System.currentTimeMillis() - lastPress > 200){
					if (Mouse.isGrabbed())
						Mouse.setGrabbed(false);
					else if (!Mouse.isGrabbed())
						Mouse.setGrabbed(true);
					lastPress = System.currentTimeMillis();
				}
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				camera.flyDown(movementSpeed * dt);

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
					else
						falling = true;
				}
			}
			if (falling && !jumping)
				//camera.position.y += 1;
				moveCameraSmooth(new Location(camera.position.x, camera.position.y, camera.position.z), new Location(camera.position.x, camera.position.y + (gravity / percentOf60), camera.position.z), 500);
			else if (jumping){
				if (jumpFrame < jumpDistance){
					if (jumpDistance - 1 == jumpFrame)
						moveCameraSmooth(new Location(camera.position.x, camera.position.y, camera.position.z), new Location(camera.position.x, camera.position.y - (jumpSpeed / 2 / percentOf60), camera.position.z), 500);
					else
						moveCameraSmooth(new Location(camera.position.x, camera.position.y, camera.position.z), new Location(camera.position.x, camera.position.y - (jumpSpeed / percentOf60), camera.position.z), 500);
					jumpFrame += percentOf60;
				}
				else if (jumpFreezeFrame < jumpFreezeLength){
					jumpFrame += percentOf60;
					jumpFreezeFrame += percentOf60;
				}
				else if (jumpFreezeFrame == jumpFreezeLength){
					moveCameraSmooth(new Location(camera.position.x, camera.position.y, camera.position.z), new Location(camera.position.x, camera.position.y + (gravity / 2 / percentOf60), camera.position.z), 500);
					jumpFreezeFrame += percentOf60;
				}
				else {
					jumping = false;
					falling = true;
					jumpFrame = 0;
					jumpFreezeFrame = 0;
				}
			}

			GL11.glLoadIdentity();

			camera.lookThrough();

			GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, asFloatBuffer(new float[]{lightPosition.x, lightPosition.y, lightPosition.z, 1f}));

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
		if (!smoothing){
			smoothing = true;

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
			
			smoothing = false;
		}
	}
}
