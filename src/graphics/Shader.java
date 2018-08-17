package graphics;

import utils.ShaderUtils;
import math.Perspective;

import static org.lwjgl.opengl.GL20.*;
import static utils.OutputUtils.*;

import java.util.HashMap;
import java.util.Map;

import math.Matrix4f;
import math.Vector3f;

public class Shader {
	
	public static final int VERTEX_ATTRIB = 0;
	public static final int TCOORD_ATTRIB = 1;
	
	public static Shader ROOM, MOB, PLAYER;
	
	private boolean enabled = false;
	
	private final int ID;
	private Map<String, Integer> locationCache = new HashMap<String, Integer>();
	
	public Shader(String vertex, String fragment) {
		ID = ShaderUtils.load(vertex,  fragment);
		setDebug(true);
	}
	
	public static void loadAll() {
		ROOM = new Shader("shaders/room.vert", "shaders/room2.frag");
		MOB = new Shader("shaders/room.vert", "shaders/mob.frag");
		PLAYER = new Shader("shaders/player.vert", "shaders/player.frag");
	}
	
	public static void initAll() {
		//set camera: projection matrix is camera view
		Matrix4f pr_matrix = Matrix4f.orthographic(Perspective.left, Perspective.right, Perspective.bottom, Perspective.top, Perspective.near, Perspective.far);
		//pass camera (pr_matrix) into vertex shader and set texture rendering to true for each shader
		ROOM.setUniformMat4f("pr_matrix", pr_matrix);
		ROOM.setUniform1i("tex", 1);
		
		MOB.setUniformMat4f("pr_matrix", pr_matrix);
		MOB.setUniform1i("tex", 1);
		
		PLAYER.setUniformMat4f("pr_matrix", pr_matrix);
		PLAYER.setUniform1i("tex", 1);
		
	}
	
	public int getUniform(String name) {
		setDebug(false);
		if (locationCache.containsKey(name)) {
			return locationCache.get(name);
		}
		int result = glGetUniformLocation(ID, name);
		try {
			if (result == -1) {
				if (debug) System.err.println("Could not find uniform variable '" + name + "'!");
				throw new Exception();
			} else {
				locationCache.put(name,  result);
			}
		} catch (Exception e) {
			if (debug) e.printStackTrace();
		}
		return result;
	}
	
	public void setUniform1i(String name, int value) {
		if (!enabled) enable();
		glUniform1i(getUniform(name), value);
	}
	
	public void setUniform1f(String name, float value) {
		if (!enabled) enable();
		glUniform1f(getUniform(name), value);
	}
	
	public void setUniform2f(String name, float x, float y) {
		if (!enabled) enable();
		glUniform2f(getUniform(name), x, y);
	}
	
	public void setUniform3f(String name, Vector3f vector) {
		if (!enabled) enable();
		glUniform3f(getUniform(name), vector.x, vector.y, vector.z);
	}
	
	public void setUniformMat4f(String name, Matrix4f matrix) {
		if (!enabled) enable();
		glUniformMatrix4fv(getUniform(name), false, matrix.toFloatBuffer());
	}
	
	public void enable() {
		glUseProgram(ID);
		enabled = true;
	}
	
	public void disable() {
		glUseProgram(0);
		enabled = false;
	}
}
