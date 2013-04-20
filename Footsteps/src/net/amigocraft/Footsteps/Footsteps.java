package net.amigocraft.Footsteps;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.java.games.input.Controller;
import net.java.games.input.Component.Identifier;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import static org.lwjgl.input.Keyboard.*;
import static org.lwjgl.input.Mouse.*;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * @title Footsteps
 * @author The Unknown Team
 * 
 * All Rights Reserved to The Unknown Team
 *
 */
public class Footsteps {

	static Camera camera = new Camera(250, 50, 150);
	float dx = 0.0f;
	float dy = 0.0f;
	public static float delta = 0;
	long lastTime = getTime();
	long time = getTime();
	static Vector3f lightPosition = new Vector3f(-500f, -100f, 500f);
	long lastPress = (int)System.currentTimeMillis();

	float mouseSensitivity = 0.05f;
	float movementSpeed = 20.0f;
	float joystickPovSpeed = 100.0f;
	public float jumpFrame = 0;
	public float jumpSpeedFrame = 0;
	public float jumpFreezeFrame = 0;
	public float fallFrame = 0f;
	public int bunnyFrame = 0;

	public static boolean jumping = false;
	public boolean falling = true;
	public boolean left = false;
	public boolean right = false;
	public boolean forward = false;
	public boolean backward = false;
	public boolean ground = false;
	public static boolean ingameMenu = false;

	private boolean gamepad = false;
	public boolean smoothing = false;
	private boolean debug = false;
	public boolean fullscreen = false;

	public int playerHeight = 10;
	public float gravity = 5f;
	public float jumpSpeed = 3f;
	public float jumpDistance = 2f;
	public float fallIncrease = 15f;
	public float lastFps = 0f;
	public float currentTime = 0f;
	private int currentFps = 0;
	private float terrainBrightness = 0.1f; // brightness is directly proportional to value

	private boolean wireframe = false;
	private boolean colorize = false;
	private boolean textured = true;
	
	public int buttonWidth = 350;
	public int buttonHeight = 50;

	public static Model bunnyModel;

	private static final String VERTEX_SHADER = "/net/amigocraft/Footsteps/shaders/shader.vs";
	private static final String FRAGMENT_SHADER = "/net/amigocraft/Footsteps/shaders/shader.fs";

	private static int shaderProgram;
	private static int diffuseModifierUniform;

	List<Location> terrainCap = new ArrayList<Location>();
	public static List<CollisionBox> cBoxes = new ArrayList<CollisionBox>();

	public Joystick joystick = new Joystick(Controller.Type.STICK, Controller.Type.GAMEPAD);

	public float[] skyColor = new float[]{0f, 0.7f, 0.9f, 1.1f};

	public Texture grassTexture, bunnyTexture;

	// GUI related variables
	private static UnicodeFont font;
	private static UnicodeFont backFont;

	public static void main(String[] args){
		new Footsteps();
	}

	public Footsteps(){
		try {
			Display.setDisplayMode(new DisplayMode(Display.getDesktopDisplayMode().getWidth() - 20, Display.getDesktopDisplayMode().getHeight() - 100));
			Display.setTitle("Footsteps");
			Display.setVSyncEnabled(true);
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

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(30f, 1280f / 720f, 0.001f, 1000f);
		glOrtho(1, 1, 1, 1, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glShadeModel(GL_SMOOTH);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_LIGHTING);
		float ambientLight[] = {terrainBrightness, terrainBrightness, terrainBrightness, 1f};
		glLight(GL_LIGHT1, GL_AMBIENT, asFloatBuffer(ambientLight));

		glLightModel(GL_LIGHT_MODEL_AMBIENT, asFloatBuffer(new float[]{0.05f, 0.05f, 0.05f, 1f}));
		glLight(GL_LIGHT0, GL_DIFFUSE, asFloatBuffer(new float[]{1.5f, 1.5f, 1.5f, 1f}));
		glCullFace(GL_BACK);
		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT, GL_AMBIENT);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_ALPHA_TEST);
		glAlphaFunc(GL_GREATER, 0);

		shaderProgram = ShaderLoader.loadShaderPair(VERTEX_SHADER, FRAGMENT_SHADER);
		diffuseModifierUniform = glGetUniformLocation(shaderProgram, "diffuseLightIntensity");

		if (joystick.isControllerConnected()){
			System.out.println("Gamepad \"" + joystick.getControllerName() + "\" found");
			gamepad = true;
		}

		try {
			grassTexture = TextureLoader.getTexture("PNG", this.getClass().getClassLoader().getResourceAsStream("images/grass.png"));
			bunnyTexture = TextureLoader.getTexture("PNG", this.getClass().getClassLoader().getResourceAsStream("images/rabbitfur.png"));
		}
		catch (Exception ex){
			ex.printStackTrace();
		}

		setUpFont();

		setGrabbed(true);

		// this code is here as a reference for future models
		int bunnyHandle = glGenLists(1);
		glNewList(bunnyHandle, GL_COMPILE);
		{
			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
			glBegin(GL_TRIANGLES);
			glMaterialf(GL_FRONT, GL_SHININESS, 10f);
			glMaterialf(GL_BACK, GL_SHININESS, 10f);
			glColor3f(0.45f, 0.35f, 0.1f);
			try {
				bunnyModel = ObjLoader.loadModel(this.getClass().getClassLoader().getResourceAsStream("models/bunny.obj"));
			}
			catch (Exception ex){
				ex.printStackTrace();
				Display.destroy();
				System.exit(1);
			}
			for (Face f : bunnyModel.faces){
				Vector3f n1 = bunnyModel.normals.get((int)f.normal.x - 1);
				glNormal3f(n1.x, n1.y, n1.z);
				Vector3f v1 = bunnyModel.vertices.get((int)f.vertex.x - 1);
				glVertex3f(v1.x, v1.y, v1.z);
				Vector3f n2 = bunnyModel.normals.get((int)f.normal.y - 1);
				glNormal3f(n2.x, n2.y, n2.z);
				Vector3f v2 = bunnyModel.vertices.get((int)f.vertex.y - 1);
				glVertex3f(v2.x, v2.y, v2.z);
				Vector3f n3 = bunnyModel.normals.get((int)f.normal.z - 1);
				glNormal3f(n3.x, n3.y, n3.z);
				Vector3f v3 = bunnyModel.vertices.get((int)f.vertex.z - 1);
				glVertex3f(v3.x, v3.y, v3.z);
			}
			glEnd();
			
			glDisable(GL_CULL_FACE);
		}
		glEndList();

		int terrainHandle = glGenLists(1);
		glNewList(terrainHandle, GL_COMPILE);
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
			glBegin(GL_TRIANGLES);
			glMaterialf(GL_FRONT, GL_SHININESS, 10f);
			glColor3f(0.85f, 1f, 0.85f);
			for (float x = 1; x < hm.getWidth(null); x++){
				for (float z = 1; z < hm.getHeight(null); z++){
					if (x < hm.getWidth(null) && z < hm.getHeight(null)){
						float yDivide = 5;

						// y1
						int rgb1 = hm.getRGB((int)x - 1, (int)z - 1);
						Color color1 = new Color(rgb1);
						int red1 = color1.getRed();
						int green1 = color1.getGreen();
						int blue1 = color1.getBlue();
						int shade1 = (red1 + green1 + blue1) / 3;
						float y1 = shade1 / yDivide;
						float pixel1 = (y1 * yDivide / 256) * hmRef.getWidth(null);
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
						int rgb3 = hm.getRGB((int)x - 1, (int)z);
						Color color3 = new Color(rgb3);
						int red3 = color3.getRed();
						int green3 = color3.getGreen();
						int blue3 = color3.getBlue();
						int shade3 = (red3 + green3 + blue3) / 3;
						float y3 = shade3 / yDivide;

						// y4
						int rgb4 = hm.getRGB((int)x, (int)z);
						Color color4 = new Color(rgb4);
						int red4 = color4.getRed();
						int green4 = color4.getGreen();
						int blue4 = color4.getBlue();
						int shade4 = (red4 + green4 + blue4) / 3;
						float y4 = shade4 / yDivide;

						if (colorize)
							glColor3f(newRed1 / 256f, newGreen1 / 256f, newBlue1 / 256f);

						Vector3f v1 = new Vector3f(x, y1, z);
						Vector3f v2 = new Vector3f(x + 1f, y2, z);
						Vector3f v3 = new Vector3f(x, y3, z + 1f);
						Vector3f v4 = new Vector3f(x + 1f, y4, z + 1f);

						// triangle 1
						if (textured)
							glTexCoord2f(0, 0);
						glVertex3f(v1.x, v1.y, v1.z);

						if (textured)
							glTexCoord2f(1, 0);
						glVertex3f(v2.x, v2.y, v2.z);

						if (textured)
							glTexCoord2f(0, 1);
						glVertex3f(v3.x, v3.y, v3.z);

						// triangle 2
						if (textured)
							glTexCoord2f(1, 0);
						glVertex3f(v2.x, v2.y, v2.z);

						if (textured)
							glTexCoord2f(0, 1);
						glVertex3f(v3.x, v3.y, v3.z);

						if (textured)
							glTexCoord2f(1, 1);
						glVertex3f(v4.x, v4.y, v4.z);

						terrainCap.add(new Location(x, y1, z));
					}
				}
			}
			glEnd();
		}
		glEndList();

		new SkyFactory();

		while (!Display.isCloseRequested()){

			time = getTime();
			delta = time - lastTime;
			lastTime = time;

			if (ingameMenu){
				if (Mouse.isGrabbed())
					Mouse.setGrabbed(false);
				
				// close menu
				if (isKeyDown(KEY_ESCAPE)){
					if (System.currentTimeMillis() - lastPress > 200){
						ingameMenu = false;
						lastPress = System.currentTimeMillis();
						if (!Mouse.isGrabbed())
							Mouse.setGrabbed(true);
						
						continue;
					}
				}
				
				dx = getDX();
				dy = getDY() * -1;


				glMatrixMode(GL_PROJECTION);
				glPushMatrix();
				glLoadIdentity();
				glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1, 1 );
				glMatrixMode(GL_MODELVIEW);
				glPushMatrix();
				glLoadIdentity();
				glDisable(GL_LIGHTING);
				if (wireframe)
					glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
				glDisable(GL_DEPTH_TEST);
				glDisable(GL_CULL_FACE);
				glBegin(GL_QUADS);
				
				// block world from view
				/*glColor3f(0f, 0f, 0f);
				glVertex3f(0, 0, 0f);
				glVertex3f(Display.getWidth(), 0, 0f);
				glVertex3f(Display.getWidth(), Display.getHeight(), 0f);
				glVertex3f(0, Display.getHeight(), 0f);*/
				
				glColor3f(.6f, .6f, .6f);
				int resumeBtnPos = 200;
				glVertex3f((Display.getWidth() / 2) - (buttonWidth / 2), resumeBtnPos, 1f);
				glVertex3f((Display.getWidth() / 2) + (buttonWidth / 2), resumeBtnPos, 1f);
				glVertex3f((Display.getWidth() / 2) + (buttonWidth / 2), resumeBtnPos + buttonHeight, 1f);
				glVertex3f((Display.getWidth() / 2) - (buttonWidth / 2), resumeBtnPos + buttonHeight, 1f);
				glEnd();
				String resumeBtnText = "Resume Game";
				drawString((Display.getWidth() / 2) - (font.getWidth(resumeBtnText) / 2),
						resumeBtnPos + ((buttonHeight - font.getHeight(resumeBtnText)) / 2),
						resumeBtnText, false);

				glEnable(GL_DEPTH_TEST);
				if (wireframe)
					glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
				glEnable(GL_LIGHTING);
				glMatrixMode(GL_PROJECTION);
				glPopMatrix();
				glMatrixMode(GL_MODELVIEW);
				glPopMatrix();
				
			}
			
			else {

				boolean movedByGamepad = false;

				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

				if (gamepad){
					if (!joystick.pollController())
						gamepad = false;
				}

				if (gamepad){
					float leftStickX = (joystick.getX_LeftJoystick_Percentage() - 50) / 50f;
					float leftStickY = (joystick.getY_LeftJoystick_Percentage() - 50) / 50f;
					float rightStickX = (joystick.getX_RightJoystick_Percentage() - 50) / 50f;
					float rightStickY = (joystick.getY_RightJoystick_Percentage() - 50) / 50f;

					if (leftStickX > .04 || leftStickX < -.04 || leftStickY > .04 || leftStickY < -.04){
						boolean backward = false;
						if (leftStickY > 0)
							backward = true;
						int angle = Math.round(leftStickX * 90);
						float larger = Math.abs(leftStickX);
						if (Math.abs(leftStickY) > Math.abs(leftStickX))
							larger = Math.abs(leftStickY);
						if (movementSpeed * delta / 1000 < 50) // just in case
							camera.moveCustom(angle, movementSpeed * larger * delta / 1000, backward);
						movedByGamepad = true;
					}
					else
						camera.freezeXAndZ();

					if (rightStickX > .04 || rightStickX < -.04 || rightStickY > .04 || rightStickY < -.04){
						camera.setYaw(camera.getYaw() + rightStickX * joystickPovSpeed * delta / 1000);
						camera.setPitch(camera.getPitch() + rightStickY * joystickPovSpeed * delta / 1000);
					}

					float a = joystick.getComponentValue(Identifier.Button._0);
					if (a > 0 && !jumping && !falling)
						jumping = true;

					float start = joystick.getComponentValue(Identifier.Button._7);
					if (start > 0)
						break;

					float select = joystick.getComponentValue(Identifier.Button._6);
					if (select > 0){
						if (System.currentTimeMillis() - lastPress > 200){
							if (debug)
								debug = false;
							else
								debug = true;
							lastPress = System.currentTimeMillis();
						}
					}

					float x = joystick.getComponentValue(Identifier.Button._2);
					if (x > 0){
						if (System.currentTimeMillis() - lastPress > 200){
							if (wireframe)
								wireframe = false;
							else
								wireframe = true;
							lastPress = System.currentTimeMillis();
						}
					}
				}

				if (wireframe)
					glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
				else
					glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

				// skybox
				if (wireframe)
					glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
				glPushMatrix();
				glLoadIdentity();
				glRotatef(camera.getPitch() - 13, 1, 0, 0);
				glRotatef(camera.getYaw(), 0, 1, 0);
				glRotatef(5, 0, 0, 1);
				//glTranslatef(0f, -0.15f, 0f);
				glCallList(SkyFactory.getHandle());
				glPopMatrix();
				if (wireframe)
					glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

				// terrain
				grassTexture.bind();
				glEnable(GL_LIGHT1);
				glCallList(terrainHandle);
				glDisable(GL_LIGHT1);

				// models
				glTranslatef(250, 37, 250);
				glRotatef(bunnyFrame, 0f, 1f, 0f);
				bunnyFrame += 1;
				bunnyTexture.bind();
				glUseProgram(shaderProgram);
				glUniform1f(diffuseModifierUniform, 10f);
				glEnable(GL_LIGHT0);
				glEnable(GL_CULL_FACE);
				glCallList(bunnyHandle);
				glDisable(GL_CULL_FACE);
				glDisable(GL_LIGHT0);
				glUseProgram(0);

				dx = getDX();
				dy = getDY() * -1;

				updateFps();
				if (debug){
					drawString(10, 10, "fps: " + currentFps, true);
					drawString(10, 45, "x: " + camera.getX(), true);
					drawString(10, 80, "y: " + camera.getY(), true);
					drawString(10, 115, "z: " + camera.getZ(), true);
					drawString(10, 150, "pitch: " + camera.getPitch(), true);
					drawString(10, 185, "yaw: " + camera.getYaw(), true);
					drawString(10, 220, "gamepad: " + gamepad, true);
					int mb = 1024 * 1024;
					Runtime runtime = Runtime.getRuntime();
					drawString(10, 255, runtime.maxMemory() / mb + "mb allocated memory: " +
							(runtime.maxMemory() - runtime.freeMemory()) / mb + "mb used, " +
							runtime.freeMemory() / mb + "mb free", true);
				}

				camera.setYaw(camera.getYaw() + dx * mouseSensitivity);
				camera.setPitch(camera.getPitch() + dy * mouseSensitivity);

				if (!movedByGamepad){
					if (isKeyDown(KEY_W))
						forward = true;
					else if (forward){
						camera.walkForward(0);
						forward = false;
					}

					if (isKeyDown(KEY_S))
						backward = true;
					else if (backward){
						camera.walkBackward(0);
						backward = false;
					}

					if (isKeyDown(KEY_A))
						left = true;
					else if (left){
						camera.strafeLeft(0);
						left = false;
					}

					if (isKeyDown(KEY_D))
						right = true;
					else if (right){
						camera.strafeRight(0);
						right = false;
					}
				}

				if (isKeyDown(KEY_SPACE)){
					if (!jumping && !falling){
						jumping = true;
						jumpFrame = 0;
					}
				}

				if (isKeyDown(KEY_F)){
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

				if (isKeyDown(KEY_F3)){
					if (System.currentTimeMillis() - lastPress > 200){
						if (debug)
							debug = false;
						else
							debug = true;
						lastPress = System.currentTimeMillis();
					}
				}
				
				// open menu
				if (isKeyDown(KEY_ESCAPE)){
					if (System.currentTimeMillis() - lastPress > 200){
						ingameMenu = true;
						lastPress = System.currentTimeMillis();
						
						continue;
					}
				}

				if (isButtonDown(1)){
					if (System.currentTimeMillis() - lastPress > 200){
						if (isGrabbed())
							setGrabbed(false);
						else if (!isGrabbed())
							setGrabbed(true);
						lastPress = System.currentTimeMillis();
					}
				}

				/*if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				camera.flyDown(movementSpeed * delta / 100f);*/

				//if (Keyboard.isKeyDown(Keyboard.KEY_L))
				lightPosition = new Vector3f(-camera.position.x, -camera.position.y, -camera.position.z);

				for (Location l : terrainCap){
					if (l.xZEquals(new Location((int)camera.position.x * -1, (int)camera.position.y * -1, (int)camera.position.z * -1))){
						if (camera.position.y + playerHeight + l.getY() <= 0 && camera.position.y + playerHeight + l.getY() >= -1){
							falling = false;
							break;
						}
						else if (camera.position.y + playerHeight + l.getY() > 0){
							falling = false;
							float x2 = 0;
							float z2 = 0;
							for (Location lo : terrainCap){
								if (lo.getX() == l.getX() + 1 && lo.getZ() == l.getZ())
									x2 = lo.getY();
								else if (lo.getX() == l.getX() && lo.getZ() == l.getZ() + 1)
									z2 = lo.getY();
								
								if (x2 != 0 && z2 != 0)
									break;
							}
							float xPercent = camera.position.x + l.getX();
							float zPercent = camera.position.z + l.getZ();
							float xY = (l.getY() * (1 - xPercent)) + (x2 * xPercent);
							float zY = (l.getY() * (1 - zPercent)) + (z2 * zPercent);
							camera.position.setY(-((xY + zY) / 2) - playerHeight);
							System.out.println(xPercent + ", " + zPercent + ", " + xY + ", " + zY + ", " + ((xY + zY) / 2));
							break;
						}
						else {
							falling = true;
							break;
						}
					}
				}
				if (falling && !jumping){
					camera.flyDown((fallFrame / fallIncrease * gravity) * delta / 100f);
					if (fallFrame < fallIncrease)
						fallFrame += 1;
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
					fallFrame = 0f;
					ground = false;
					if (jumpFrame < jumpDistance){
						camera.flyUp((jumpSpeed - (jumpFrame / jumpDistance)) * delta / 100f);
						jumpFrame += delta / 100;
					}
					else {
						jumping = false;
						falling = true;
						jumpFrame = 0;
					}
				}
				else {
					fallFrame = 0f;
					camera.velocity.setY(0);
					ground = true;
				}

				if (!movedByGamepad){
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
				}

				glLoadIdentity();

				camera.lookThrough();

				glLight(GL_LIGHT1, GL_POSITION, asFloatBuffer(new float[]{lightPosition.x, lightPosition.y, lightPosition.z, 1f}));

			}

			Display.sync(60);

			Display.update();
		}
		glDeleteProgram(shaderProgram);
		Display.destroy();
	}

	private static FloatBuffer asFloatBuffer(float[] f){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(f.length);
		buffer.put(f);
		buffer.flip();
		buffer.order();
		return buffer;
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
		float size = 28F;
		Font awtFont = new Font("Verdana", Font.BOLD, (int)size);
		font = new UnicodeFont(awtFont.deriveFont(0, size));
		font.addAsciiGlyphs();
		ColorEffect e = new ColorEffect();
		e.setColor(Color.WHITE);
		font.getEffects().add(e);
		backFont = new UnicodeFont(awtFont.deriveFont(0, size));
		backFont.addAsciiGlyphs();
		ColorEffect e2 = new ColorEffect();
		e.setColor(Color.BLACK);
		backFont.getEffects().add(e2);
		try {
			font.loadGlyphs();
			backFont.loadGlyphs();
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}

	public void drawString(int x, int y, String str, boolean shadow){
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1, 1 );
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
		glDisable(GL_LIGHTING);
		if (wireframe)
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		if (shadow)
			backFont.drawString(x - 3, y - 3, str);
		font.drawString(x, y, str);
		if (wireframe)
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glEnable(GL_LIGHTING);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
	}

	public BufferedImage scaleImage(BufferedImage img, int width, int height){
		BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)newImg.getGraphics();
		g.scale(((double)width / (double)img.getWidth()), ((double)height / (double)img.getHeight()));
		g.drawImage(img, 0, 0, null);
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

	public Vector3f getNormal(Vector3f p1, Vector3f p2, Vector3f p3){
		Vector3f v = new Vector3f();

		Vector3f calU = new Vector3f(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
		Vector3f calV = new Vector3f(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);

		v.setX(calU.getY() * calV.getZ() - calU.getZ() * calV.getY());
		v.setY(calU.getZ() * calV.getX() - calU.getX() * calV.getZ());
		v.setZ(calU.getX() * calV.getY() - calU.getY() * calV.getX());

		return (Vector3f)v.normalise();
	}
}
