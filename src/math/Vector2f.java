package math;

import static math.Direction.*;
import static utils.OutputUtils.*;

import java.util.Random;

/*
 * 2D Vector composed of 2 floats
 * 
 * All math is done with degree inputs and outputs
 * Vectors named "u" are typically vectors to be worked on by the method
 * Vectors named "v" are typically vectors that are used to change "u" or this instance of Vector2f.
 * Same goes for uv, uy, vx, vy which represent Vector2f's in the form of two floats
 */
public class Vector2f {
	
	public static final Vector2f zero = new Vector2f(0f, 0f);
	private static Random r = new Random();
	
	public float x, y;
	
	public Vector2f() {
		x = 0f;
		y = 0f;
	}
	
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2f(Vector2f v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	public Vector2f(float degrees) {
		x = cos(degrees);
		y = sin(degrees);
	}
	
	public static Vector2f random() {
		return new Vector2f(r.nextFloat() * 360f);
	}
	
	public Vector2f reset() {
		x = 0f;
		y = 0f;
		return new Vector2f(this);
	}
	
	public Vector2f set(Vector2f v) {
		x = v.x;
		y = v.y;
		return new Vector2f(this);
	}
	
	public Vector2f set(float vx, float vy) {
		x = vx;
		y = vy;
		return new Vector2f(this);
	}
	
	public Vector2f setNegative(Vector2f v) {
		x = -v.x;
		y = -v.y;
		return new Vector2f(this);
	}
	
	public Vector2f setNegative(float vx, float vy) {
		x = -vx;
		y = -vy;
		return new Vector2f(this);
	}
	
	public static Vector2f add(Vector2f u, Vector2f v) {
		return new Vector2f(u.x + v.x, u.y + v.y);
	}
	
	public Vector2f add(Vector2f v) {
		x += v.x;
		y += v.y;
		return new Vector2f(this);
	}
	
	public Vector2f add(float vx, float vy) {
		x += vx;
		y += vy;
		return new Vector2f(this);
	}
	
	public static Vector2f sub(Vector2f u, Vector2f v) {
		return new Vector2f(u.x - v.x, u.y - v.y);
	}
	
	public Vector2f sub(Vector2f v) {
		x -= v.x;
		y -= v.y;
		return new Vector2f(this);
	}
	
	public Vector2f sub(float vx, float vy) {
		x -= vx;
		y -= vy;
		return new Vector2f(this);
	}

	//returns dot product of vector u and vector v
	public static float dot(Vector2f u, Vector2f v) {
		return (u.x * v.x) + (u.y * v.y);
	}
	//returns dot product of this vector and vector v
	public float dot(Vector2f v) {
		return (x * v.x) + (y * v.y);
	}
	
	public static Vector2f times(Vector2f u, float a) {
		return new Vector2f(u.x * a, u.y * a);
	}
	
	public Vector2f times(float a) {
		x *= a;
		y *= a;
		return new Vector2f(x * a + y * a);
	}
	
	public static Vector2f divide(Vector2f u, float a) {
		return new Vector2f(u.x / a, u.y / a);
	}
	
	public Vector2f divide(float a) {
		x /= a;
		y /= a;
		return new Vector2f(x / a, y / a);
	}
	
	public static Vector2f rotate(Vector2f u, float degrees) {
		return new Vector2f(cos(degrees)*u.x - sin(degrees)*u.y, 
							sin(degrees)*u.x + cos(degrees)*u.y);
	}
	
	public Vector2f rotate(float degrees) {
		float xT = cos(degrees)*x - sin(degrees)*y;
		float yT = sin(degrees)*x + cos(degrees)*y;
		x = xT;
		y = yT;
		return new Vector2f(this);
	}
	
	public static float getAngle(Vector2f u, Vector2f v) {
		float degrees = degrees((float)Math.atan2(v.y - u.y, v.x - u.x));
		return simplify(degrees);
	}
	
	public static float getAngle(Vector2f u) {
		return getAngle(zero, u);
	}
	
	public static float length(Vector2f u) {
		return (float)Math.sqrt(u.x*u.x + u.y*u.y);
	}
	
	public float length() {
		return length(this);
	}
	
	public static float norml1(Vector2f u) {
		return (float)(u.x + u.y);
	}
	
	public float norml1() {
		return norml1(this);
	}
	
	public static float norml2(Vector2f u) {
		return length(u);
	}
	
	public float norml2() {
		return norml2(this);
	}
	
	public static float normlinf(Vector2f u) {
		return u.x >= u.y ? u.x : u.y; //returns max(u.x, u.y)
	}
	
	public float normlinf() {
		return normlinf(this);
	}
	
	public static float normlp(Vector2f u, float p) {
		return (float)(Math.sqrt((Math.pow(u.x, p) + Math.pow(u.y, p))));
	}
	
	public float normlp(float p) {
		return normlp(this, p);
	}
	
	public static float norml2squared(Vector2f u) {
		return u.x*u.x + u.y*u.y;
	}
	
	public float norml2squared() {
		return norml2squared(this);
	}
	
	public static float distance(Vector2f u, Vector2f v) {
		return sub(u, v).length();
	}
	
	public float distance(Vector2f v) {
		return sub(this, v).length();
	}
	
	public static float distancel1(Vector2f u, Vector2f v) {
		return sub(u, v).norml1();
	}
	
	public static float distancel2(Vector2f u, Vector2f v) {
		return sub(u, v).norml2();
	}
	
	public static float distancelinf(Vector2f u, Vector2f v) {
		return sub(u, v).normlinf();
	}
	
	public static float distancel2squared(Vector2f u, Vector2f v) {
		return sub(u, v).norml2squared();
	}
	
	public static Vector2f normalize(Vector2f u) {
		float length = u.length();
		if (length == 0f) 	return new Vector2f(0f, 0f);
		else 				return new Vector2f(u.x/length, u.y/length);
	}
	
	public Vector2f normalize() {
		float length = this.length();
		if (length == 0f) {
			x = 0f;
			y = 0f;
		}
		else {
			x /= length;
			y /= length;
		}
		return new Vector2f(this);
	}
	
	public static Vector2f scale(Vector2f u, float s) {
		return new Vector2f(u.x*s, u.y*s);
	}
	
	public Vector2f scale(float s) {
		x *= s;
		y *= s;
		return new Vector2f(this);
	}

	public static Vector2f setMag(Vector2f u, float m) {
		Vector2f normalized = normalize(u);
		return new Vector2f(normalized.x*m, normalized.y*m);
	}
	
	public Vector2f setMag(float m) {
		Vector2f normalized = normalize(this);
		x = normalized.x*m;
		y = normalized.y*m;
		return new Vector2f(this);
	}
	
	public static Vector2f limit(Vector2f u, float l) {
		if (u.length() > l) return new Vector2f(u.x, u.y).setMag(l);
		else 				return new Vector2f(u.x, u.y);
	}
	
	public Vector2f limit(float l) {
		if (length() > l) 	return this.setMag(l);
		else 				return this;
	}
	
	public Vector3f to3D(float z) {
		return new Vector3f(this.x, this.y, z);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	
	
	
	

//	//returns dot product of this vector and vector v
//	public float dot(Vector2f v) {
//		return (x * v.x) + (y * v.y);
//	}
//	//returns addition of this vector and vector v
//	public Vector2f add(float vx, float vy) {
//		return new Vector2f(x + vx, y + vy);
//	}
//	//returns addition of this vector and vector v
//	public Vector2f add(Vector3f v) {
//		return new Vector2f(x + v.x, y + v.y);
//	}
//	//returns addition of this vector and vector v
//	public void add(Vector2f v) {
//		x += v.x;
//		y += v.y;
//	}
//	//returns difference between this vector and vector v
//	public Vector2f sub(Vector2f v) {
//		return new Vector2f(x - v.x, y - v.y);
//	}
//	//returns difference between this vector and vector v
//	public Vector2f sub(float vx, float vy) {
//		return new Vector2f(x - vx, y - vy);
//	}
//	//returns difference between this vector and vector v
//	public Vector2f sub(Vector3f v) {
//		return new Vector2f(x - v.x, y - v.y);
//	}
//	
//	public Vector2f rotate(float degrees) {
//		return new Vector2f(cos(degrees)*x - sin(degrees)*y, sin(degrees)*x + cos(degrees)*y);
//	}
//	
//	public float length() {
//		return (float)(Math.pow((Math.pow(x, 2) + Math.pow(y, 2)), 1.0/2));
//	}
//	
//	public Vector2f normalize() {
//		float length = this.length();
//		if (length == 0f) 	return new Vector2f(0f, 0f);
//		else 				return new Vector2f(x/length, y/length);
//	}
//	
//	public Vector2f scale(float s) {
//		return new Vector2f(x*s, y*s);
//	}
//	
//	public Vector2f setMag(float m) {
//		Vector2f normalized = this.normalize();
//		return new Vector2f(normalized.x*m, normalized.y*m);
//	}
//	
//	public Vector2f orthogonal() {
//		return new Vector2f(y, -x);
//	}
//	
//	public Vector3f to3D() {
//		return new Vector3f(x, y, 0);
//	}
//	
//	public String toString() {
//		return "(" + x + ", " + y + ")";
//	}
}
