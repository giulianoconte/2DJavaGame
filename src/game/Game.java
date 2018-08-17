package game;

import graphics.Shader;
import input.Input;
import math.Perspective;
import game.Level;
import engine.time.Time;
import engine.time.Timer;
import static utils.OutputUtils.*;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryUtil.*;


public class Game {
	
	private static boolean running = true;
	
	private long window;
	public static final int WINDOW_WIDTH = 1280;
	public static final int WINDOW_HEIGHT = 720;
	
	private static GLFWErrorCallback errorCallback;	//need this according to lwjgl 3 tutorial
	private static Input input = new Input();	//need this, this is merely to keep a strong reference (to prevent garbage collection) and for setup
	
	private static Game game;
	private Level level;
	private static boolean showFPS = true;
	
	public void run() {
		init();
		
		long lastTime = Time.current();
		double delta = 0.0;
		double ns = 1000000000.0 / 60.0;
		int updates = 0;
		int frames = 0;
		int secTimer = Timer.createDelaySeconds(1.0);
		
		
		while (running) {
			long now = Time.current();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1.0) {
				update();
				updates++;
				delta--;
			}
			render();
			frames++;
			if (Timer.isOver(secTimer)) {
				Timer.reset(secTimer);
				if (showFPS) p(updates + " ups, " + frames + " fps");
				updates = 0;
				frames = 0;
			}
			if (glfwWindowShouldClose(window)) {
				running = false;
			}
		}
		cleanUp();
		System.exit(0);
	}
	
	private void update() {
		glfwPollEvents();
		level.update();
		if (level.isRoundOver()) {
			level = new Level();
			level.init();
		}
	}
	
	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		level.render();
		int error = glGetError();
		if (error != GL_NO_ERROR) p("" + error);
		glfwSwapBuffers(window);
	}
	
	public void init() {
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		initWindow();
		initInput();
		initShaders();
		
		level = new Level();
		level.init();
	}
	
	private void initWindow() {
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
		window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "SPOOK", NULL, NULL);
		
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(window, (vidmode.width() - WINDOW_WIDTH) / 2, (vidmode.height() - WINDOW_HEIGHT) / 2);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
		
		GL.createCapabilities(true);
		glEnable(GL_DEPTH_TEST);
		glActiveTexture(GL_TEXTURE1);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		p("OpenGL: " + glGetString(GL_VERSION));
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	}
	
	@SuppressWarnings("static-access")
	private void initInput() {
		glfwSetKeyCallback(window, input.key);				//must use instance of Input for these calls to protect the callback system from garbage collection
		glfwSetMouseButtonCallback(window, input.mouse);
		glfwSetCursorPosCallback(window, input.cursorPos);
		Input.init();
	}
	
	private void initShaders() {
		Shader.loadAll();
		Shader.initAll();
	}
	
	private void cleanUp() {
		cleanUpInput();
		glfwDestroyWindow(window);
		glfwTerminate();
//		errorCallback.release();	//check to see if release() was taken out of callbacks
	}

	@SuppressWarnings("static-access")
	private void cleanUpInput() {
//		input.key.release();		//check to see if release() was taken out of callbacks
//		input.mouse.release();
//		input.cursorPos.release();
	}
	
	public static void endGame() {
		running = false;
	}
	
	public static Game getGame() {
		if (game == null) return new Game();
		else			  return game;
	}
	
	public static Level getLevel() {
		try {
			if (game.level == null) throw new Exception();
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("TRIED TO getLevel() WHEN LEVEL WAS NULL");
			endGame();
		}
		return game.level;
	}
	
	public static void start() {
		game = getGame();
		game.run();
	}
	
	
	
	
	
	
	
	/////////////////////////////////////////////////////////
	//				DEBUGGING							   //
	/////////////////////////////////////////////////////////
	
	private static boolean debug = false;
	
	public static void db(String s) {
		if (debug)	p("\t\t\t\t\t" + s);
	}
	
}
