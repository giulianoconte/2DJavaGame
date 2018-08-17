package math;

public class Direction {
	public static float cos(float degrees) {
		return (float)Math.cos(radians(degrees));
	}
	
	public static float sin(float degrees) {
		return (float)Math.sin(radians(degrees));
	}
	
	public static float getAngle(Vector2f p1, Vector2f p2) {
		float degrees = degrees((float)Math.atan2(p2.y - p1.y, p2.x - p1.x));
		return simplify(degrees);
	}
	
	public static float getAngle(Vector3f p1, Vector3f p2) {
		return getAngle(new Vector2f(p1.x, p1.y), new Vector2f(p2.x, p2.y));
	}
	
	public static float getAngle(Vector3f p1, Vector2f p2) {
		return getAngle(new Vector2f(p1.x, p1.y), p2);
	}
	
	public static float getAngle(Vector2f p1, Vector3f p2) {
		return getAngle(p1, new Vector2f(p2.x, p2.y));
	}
	
	public static float simplify(float degrees) {
		while (degrees < 0) degrees += (float) 360.0;
		while (degrees >= 360.0) degrees -= (float) 360.0;
		return degrees;
	}
	
	public static float degrees(float radians) {
		return (float)Math.toDegrees(radians);
	}
	
	public static float radians(float degrees) {
		return (float)Math.toRadians(degrees);
	}
}
