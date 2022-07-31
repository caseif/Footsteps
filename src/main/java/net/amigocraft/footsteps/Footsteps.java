package net.amigocraft.footsteps;

import static net.amigocraft.footsteps.util.GluEmulation.gluLookAt;
import static net.amigocraft.footsteps.util.MiscUtil.*;
import static net.amigocraft.footsteps.util.RenderUtil.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.amigocraft.footsteps.util.ObjLoader;
import net.amigocraft.footsteps.util.SetupDisplay;
import net.amigocraft.footsteps.util.ShaderLoader;
import net.amigocraft.footsteps.util.Vector3f;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * @author The Unknown Team
 * 
 * All Rights Reserved to The Unknown Team
 *
 */
public class Footsteps {

	public static double OPENGL_VERSION;

	public static Camera camera = new Camera(250, 50, 150);
	public float dx = 0.0f;
	public float dy = 0.0f;
	public static float delta = 0;
	public long lastTime = getTime();
	public static long time = getTime();
	public static Vector3f lightPosition = new Vector3f(-500f, -100f, 500f);
	public static long lastPress = (int)System.currentTimeMillis();

	float mouseSensitivity = 0.025f;
	static float movementSpeed = 20.0f;
	static float joystickPovSpeed = 100.0f;
	public static float jumpFrame = 0;
	public float jumpSpeedFrame = 0;
	public float jumpFreezeFrame = 0;
	public float fallFrame = 0f;
	public static int bunnyFrame = 0;

	public static boolean jumping = false;
	public static boolean falling = true;
	public static boolean left = false;
	public static boolean right = false;
	public static boolean forward = false;
	public static boolean backward = false;
	public boolean ground = false;
	public static boolean ingameMenu = false;
	public static boolean moved = false;

	public static boolean gamepad = false;
	public boolean smoothing = false;
	public static boolean debug = false;
	public boolean fullscreen = false;

	public final int playerHeight = 10;
	public final float gravity = 5f;
	public final float jumpSpeed = 3f;
	public final float jumpDistance = 2f;
	public final float fallIncrease = 15f;
	public final int playerFootstepDelay = 500;
	public static float lastFps = 0f;
	public float currentTime = 0f;
	public static int currentFps = 0;
	public static float terrainBrightness = 0.45f; // brightness is directly proportional to value

	public static boolean wireframe = false;
	private boolean colorize = false;
	private boolean textured = true;

	public int buttonWidth = 350;
	public int buttonHeight = 50;

	public static Model bunnyModel, wtModel, armModel;

	public static int terrainHandle;
	public static int bunnyHandle;
	public static int wtHandle;
	public static int armHandle;

	private static final String VERTEX_SHADER = "/shaders/shader.vs";
	private static final String FRAGMENT_SHADER = "/shaders/shader.fs";

	public static int shaderProgram;
	public static int diffuseModifierUniform;

	List<Location> terrainCap = new ArrayList<Location>();
	public static List<CollisionBox> cBoxes = new ArrayList<CollisionBox>();

	public float[] skyColor = new float[]{0f, 0.7f, 0.9f, 1.1f};

	public static Texture grassTexture;

	public Texture bunnyTexture;

	public List<Sound> grassSounds = new ArrayList<Sound>();

	public float lastGrassSound = time;

	public static void main(String[] args){
		new Footsteps();
	}

	public Footsteps() {
		var windowOpt = SetupDisplay.setupDisplay();
		if (windowOpt.isEmpty()) {
			throw new RuntimeException("Failed to create window");
		}

		var window = windowOpt.getAsLong();

		OPENGL_VERSION = Double.parseDouble(glGetString(GL_VERSION).substring(0, 3));

		if (OPENGL_VERSION >= 2.0){
			shaderProgram = ShaderLoader.loadShaderPair(VERTEX_SHADER, FRAGMENT_SHADER);
			diffuseModifierUniform = glGetUniformLocation(shaderProgram, "diffuseLightIntensity");
		}

		if (GamepadHandler.joystick.isControllerConnected()){
			System.out.println("Gamepad \"" + GamepadHandler.joystick.getControllerName() + "\" found");
			gamepad = true;
		}

		// textures
		try {
			grassTexture = TextureLoader.getTexture("PNG", this.getClass().getResourceAsStream("/images/grass.png"));
			//bunnyTexture = TextureLoader.getTexture("PNG", this.getClass().getResourceAsStream("/images/rabbitfur.png"));
		}
		catch (Exception ex){
			ex.printStackTrace();
		}

		// sounds
		//try {AL.create();}
		//catch (LWJGLException ex){ex.printStackTrace();}
		/*Sound.initialize();
		grassSounds.add(new Sound("grass1", "/sounds/grass1.ogg"));
		grassSounds.add(new Sound("grass2", "/sounds/grass2.ogg"));
		grassSounds.add(new Sound("grass3", "/sounds/grass3.ogg"));
		grassSounds.add(new Sound("grass4", "/sounds/grass4.ogg"));
		grassSounds.add(new Sound("grass5", "/sounds/grass5.ogg"));
		grassSounds.add(new Sound("grass6", "/sounds/grass6.ogg"));*/

		setUpFont();

		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

		bunnyHandle = glGenLists(1);
		glNewList(bunnyHandle, GL_COMPILE);
		{
			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
			glBegin(GL_TRIANGLES);
			glMaterialf(GL_FRONT, GL_SHININESS, 10f);
			glMaterialf(GL_BACK, GL_SHININESS, 10f);
			glColor3f(0.45f, 0.35f, 0.1f);
			try {
				bunnyModel = ObjLoader.loadModel("/models/bunny.obj");
				drawModel(bunnyModel);
			}
			catch (IOException ex){
				ex.printStackTrace();
			}
			glEnd();

			glDisable(GL_CULL_FACE);
		}
		glEndList();

		wtHandle = glGenLists(1);
		glNewList(wtHandle, GL_COMPILE);
		{
			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
			glBegin(GL_TRIANGLES);
			glMaterialf(GL_FRONT, GL_SHININESS, 10f);
			glMaterialf(GL_BACK, GL_SHININESS, 10f);
			glColor3f(0.45f, 0.35f, 0.1f);
			try {
				wtModel = ObjLoader.loadModel("/models/Walkie_Talkie.obj");
				drawModel(wtModel);
			}
			catch (IOException ex){
				ex.printStackTrace();
			}
			glEnd();

			glDisable(GL_CULL_FACE);
		}
		glEndList();

		armHandle = glGenLists(1);
		glNewList(armHandle, GL_COMPILE);
		{
			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
			glBegin(GL_QUADS);
			glMaterialf(GL_FRONT, GL_SHININESS, 10f);
			glMaterialf(GL_BACK, GL_SHININESS, 10f);
			glColor3f(0.45f, 0.35f, 0.1f);
			try {
				armModel = ObjLoader.loadModel("/models/fps_arms.obj");
				drawModel(armModel);
			}
			catch (IOException ex){
				ex.printStackTrace();
			}
			glEnd();

			glDisable(GL_CULL_FACE);
		}
		glEndList();

		terrainHandle = glGenLists(1);
		glNewList(terrainHandle, GL_COMPILE);
		{
			glDisable(GL_CULL_FACE);
			glEnable(GL_DEPTH_TEST);
			BufferedImage hm = null;
			BufferedImage hmRef = null;
			try {
				hm = ImageIO.read(this.getClass().getResourceAsStream("/images/hm.png"));
				hmRef = ImageIO.read(this.getClass().getResourceAsStream("/images/hmColor.png"));
			}
			catch (IOException e){
				e.printStackTrace();
			}
			glMaterialf(GL_FRONT, GL_SHININESS, 10f);
			glColor3f(0.85f, 1f, 0.85f);
			for (float x = 1; x < hm.getWidth(null); x++){
				glBegin(GL_TRIANGLES);
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
				glEnd();
			}
		}
		glEndList();

		new SkyFactory();

		double lastMouseX = 0;
		double lastMouseY = 0;

		glfwSwapInterval(1);

		while (!glfwWindowShouldClose(window)) {
			glfwPollEvents();

			time = getTime();
			delta = time - lastTime;
			lastTime = time;

			var cursorXBuf = BufferUtils.createDoubleBuffer(1);
			var cursorYBuf = BufferUtils.createDoubleBuffer(1);
			glfwGetCursorPos(window, cursorXBuf, cursorYBuf);
			var cursorX = cursorXBuf.get();
			var cursorY = cursorYBuf.get();
			dx = (float) (cursorX - lastMouseX);
			dy = (float) (cursorY - lastMouseY);
			lastMouseX = cursorX;
			lastMouseY = cursorY;

			if (ingameMenu) {
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);

				// close menu
				if (glfwGetKey(window, GLFW_KEY_ESCAPE) != 0) {
					if (System.currentTimeMillis() - lastPress > 200){
						ingameMenu = false;
						lastPress = System.currentTimeMillis();
						var mouseMode = glfwGetInputMode(window, GLFW_CURSOR);
						if (mouseMode != GLFW_CURSOR_DISABLED)
							glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

						continue;
					}
				}

				var winWidthBuf = BufferUtils.createIntBuffer(1);
				var winHeightBuf = BufferUtils.createIntBuffer(1);
				glfwGetWindowSize(window, winWidthBuf, winHeightBuf);
				var winWidth = winWidthBuf.get();
				var winHeight = winHeightBuf.get();

				glMatrixMode(GL_PROJECTION);
				glPushMatrix();
				glLoadIdentity();
				glOrtho(0, winWidth, winHeight, 0, -1, 1 );
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
				glVertex3f((winWidth / 2) - (buttonWidth / 2), resumeBtnPos, 1f);
				glVertex3f((winWidth / 2) + (buttonWidth / 2), resumeBtnPos, 1f);
				glVertex3f((winWidth / 2) + (buttonWidth / 2), resumeBtnPos + buttonHeight, 1f);
				glVertex3f((winWidth / 2) - (buttonWidth / 2), resumeBtnPos + buttonHeight, 1f);
				glEnd();
				String resumeBtnText = "Resume Game";
				drawString(window, (winWidth / 2) - (font.getWidth(resumeBtnText) / 2),
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
				glClearColor(0f, 0f, 0f, 1f);
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

				if (wireframe)
					glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
				else
					glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

				moved = false;
				GamepadHandler.handleGamepad();
				KeyHandler.handleKeys(window);

				renderWorld(window);

				updateFps();
				if (debug){
					drawString(window, 10, 30, "fps: " + currentFps, true);
					drawString(window, 10, 65, "x: " + camera.getX(), true);
					drawString(window, 10, 100, "y: " + camera.getY(), true);
					drawString(window, 10, 135, "z: " + camera.getZ(), true);
					drawString(window, 10, 170, "pitch: " + camera.getPitch(), true);
					drawString(window, 10, 205, "yaw: " + camera.getYaw(), true);
					drawString(window, 10, 240, "gamepad: " + gamepad, true);
					int mb = 1024 * 1024;
					Runtime runtime = Runtime.getRuntime();
					drawString(window, 10, 275, runtime.maxMemory() / mb + "mb allocated memory: " +
							(runtime.maxMemory() - runtime.freeMemory()) / mb + "mb used, " +
							runtime.freeMemory() / mb + "mb free", true);
				}

				if (glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED) {
					camera.setYaw(camera.getYaw() + dx * mouseSensitivity / (delta / 60));
					camera.setPitch(camera.getPitch() + dy * mouseSensitivity / (delta / 60));
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

				if (!moved){
					if (forward && left){
						camera.forwardLeft(movementSpeed * delta / 1000);
						moved = true;
					}
					else if (forward && right){
						camera.forwardRight(movementSpeed * delta / 1000);
						moved = true;
					}
					else if (backward && left){
						camera.backwardLeft(movementSpeed * delta / 1000);
						moved = true;
					}
					else if (backward && right){
						camera.backwardRight(movementSpeed * delta / 1000);
						moved = true;
					}
					else if (left){
						camera.strafeLeft(movementSpeed * delta / 1000);
						moved = true;
					}
					else if (right){
						camera.strafeRight(movementSpeed * delta / 1000);
						moved = true;
					}
					else if (forward){
						camera.walkForward(movementSpeed * delta / 1000);
						moved = true;
					}
					else if (backward){
						camera.walkBackward(movementSpeed * delta / 1000);
						moved = true;
					}

					if (moved){
						if (time - lastGrassSound > playerFootstepDelay){
							/*Random rand = new Random();
							int soundToPlay = rand.nextInt(grassSounds.size() - 1);
							grassSounds.get(soundToPlay).play();
							lastGrassSound = time;*/
						}
					}
				}

				glLoadIdentity();

				camera.lookThrough();

			}

			glfwSwapBuffers(window);
		}
		/*for (Sound s : grassSounds){
			s.dispose();
		}*/
		//AL.destroy();
		if (OPENGL_VERSION >= 2.0){
			glDeleteProgram(shaderProgram);
		}

		glfwDestroyWindow(window);
	}
}
