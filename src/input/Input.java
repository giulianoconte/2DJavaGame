package input;

import math.Perspective;
import math.Vector2f;

public class Input {
	
	public static Key key = new Key();
	public static Mouse mouse = new Mouse();
	public static CursorPos cursorPos = new CursorPos();
	
	/*
	 * input array, includes keys and mouse buttons:
	 * 		1 means button is pressed, 
	 * 		2 means button is down (held down or pressed), 
	 * 		3 means button is released or up
	 */
	private static byte[] keys = new byte[65536];
	//x,y values for cursor position
	private static float xPos, yPos;

	public static boolean press(int key) {
		boolean res = keys[key] == 1;
		if (keys[key] == 1) keys[key] = 2;
		return res;
	}
	
	public static boolean down(int key) {
		boolean res = keys[key] == 1 || keys[key] == 2;
		if (keys[key] == 1) keys[key] = 2;
		return res;
	}
	
	public static boolean release(int key) {
		return keys[key] == 3;
	}
	
	public static Vector2f getXY() {
		return Perspective.toProjXY(new Vector2f(xPos, yPos));
	}
	
	public static float getX() {
		return Perspective.toProjX(xPos);
	}
	
	public static float getY() {
		return Perspective.toProjY(yPos);
	}

	//Not to be implemented by anything outside of the input package
	public static void set(int key, byte b) {
		if (key < 0) return;
		keys[key] = b;
	}
	//Not to be implemented by anything outside of the input package
	public static void setCursorPos(double x, double y)  {
		xPos = (float)x;
		yPos = (float)y;
	}
	
	public static void init() {
		for (int i = 0; i < keys.length; i++) {
			keys[i] = 3;
		}
	}
}
