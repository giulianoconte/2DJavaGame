package math;

import static math.Direction.*;

public class Vector3f {
	
	public static Vector3f zero = new Vector3f(0f, 0f, 0f);
	
	public float x, y, z;
	
	public Vector3f() {
		x = 0f;
		y = 0f;
		z = 0f;
	}
	
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3f(float degrees) {
		x = cos(degrees);
		y = sin(degrees);
		z = 0;
	}
	
	public Vector3f set(Vector3f v) {
		Vector3f u = new Vector3f(v.x, v.y, z);
		this.x = u.x;
		this.y = u.y;
		return u;
	}
	
	public Vector3f reset() {
		x = 0f; 
		y = 0f; 
		z = 0f;
		return new Vector3f(0f, 0f, 0f);
	}
	//returns addition of this vector and vector v
	public Vector3f add(Vector3f v) {
		return new Vector3f(x + v.x, y + v.y, z);
	}
	//returns difference between this vector and vector v
	public Vector3f sub(Vector3f v) {
		return new Vector3f(x - v.x, y - v.y, z);
	}
	//returns addition of this vector and vector v
	public Vector3f add(Vector2f v) {
		return new Vector3f(x + v.x, y + v.y, z);
	}
	//returns difference between this vector and vector v
	public Vector3f sub(Vector2f v) {
		return new Vector3f(x - v.x, y - v.y, z);
	}
	
	public Vector3f rotate(float degrees) {
		return new Vector3f(cos(degrees)*x - sin(degrees)*y, sin(degrees)*x + cos(degrees)*y, z);
	}
	//returns 2D length of x,y values
	public float length() {
		return (float)(Math.pow((Math.pow(x, 2) + Math.pow(y, 2)), 1.0/2));
	}
	
	public Vector3f normalize() {
		float length = this.length();
		if (length == 0f) 	return new Vector3f(0f, 0f, z);
		else 				return new Vector3f(x/length, y/length, z);
	}
	
	public Vector3f scale(float s) {
		return new Vector3f(x*s, y*s, z);
	}
	
	public Vector3f setMag(float m) {
		Vector3f normalized = this.normalize();
		return new Vector3f(normalized.x*m, normalized.y*m, z);
	}
	
	public Vector3f limit(float l) {
		if (length() > l) 	return new Vector3f(x, y, z).setMag(l);
		else 				return new Vector3f(x, y, z);
	}
	
	public Vector2f to2D() {
		return new Vector2f(x, y);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
