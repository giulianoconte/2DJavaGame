package math;

import game.Game;

public class Perspective {
	
	public static final float aspectRatio = (float)Game.WINDOW_WIDTH / (float)Game.WINDOW_HEIGHT;
	
	public static Vector3f centerFront = new Vector3f(0f, 0f, 0.5f);
	public static float left 	= -10.0f;
	public static float right 	=  10.0f;
//	public static float bottom 	= -10.0f * 9.0f / 16.0f;
//	public static float top 	=  10.0f * 9.0f / 16.0f;
	public static float bottom 	= -10.0f / aspectRatio;
	public static float top 	=  10.0f / aspectRatio;
//	public static float bottom 	= -10.0f * (Game.WINDOW_HEIGHT / Game.WINDOW_WIDTH);
//	public static float top 	=  10.0f * (Game.WINDOW_HEIGHT / Game.WINDOW_WIDTH);
	public static float near 	= -100.0f;	//these two probably are 
	public static float far 	=  100.0f;	//bugged and/or switched
	
	//conversion from projection to pixel info
//	private static float xFactor = 64;	//xFactor * (-10.0f, 10.0f) + xOffset = (0, 1280)
//	private static float xOffset = 640; 
//	private static float yFactor = -64;	//yFactor * (-10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f) + yOffset = (720, 0)
//	private static float yOffset = 360;
	private static float xFactor = 64;	//xFactor * (-10.0f, 10.0f) + xOffset = (0, 1280)
	private static float xOffset = Game.WINDOW_WIDTH / 2.0f; 
	private static float yFactor = -64;	//yFactor * (-10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f) + yOffset = (720, 0)
	private static float yOffset = xOffset / aspectRatio;
	
	private static int wPix = Game.WINDOW_WIDTH;	//window width in pixels
	private static int hPix = Game.WINDOW_HEIGHT;	//window height in pixels
	private static float wPro = right - left;		//window width in projection matrix
	private static float hPro = top - bottom;		//window height in projection matrix
	
	public static int toPixelX(float pro) {
		return (int)(xFactor * pro + xOffset);
	}
	
	public static int toPixelY(float pro) {
		return (int)(yFactor * pro + yOffset);
	}
	
	public static float toProjX(int pix) {
		return (float)(pix - xOffset) / xFactor;
	}
	
	public static float toProjX(float pix) {
		return (pix - xOffset) / xFactor;
	}
	
	public static float toProjY(int pix) {
		return (float)(pix - yOffset) / yFactor;
	}
	
	public static float toProjY(float pix) {
		return (pix - yOffset) / yFactor;
	}
	
	public static Vector2f toProjXY(Vector2f p) {
		return new Vector2f(toProjX(p.x), toProjY(p.y));
	}
	
	public static Vector2f toProjXY(int x, int y) {
		return new Vector2f(toProjX(x), toProjY(y));
	}
	
	public static Vector2f toProjXY(float x, float y) {
		return new Vector2f(toProjX(x), toProjY(y));
	}
}
