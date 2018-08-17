package math;

public class Line2D {
	public Vector2f min;
	public Vector2f max;
	
	public Line2D(Vector2f min, Vector2f max) {
		this.min = new Vector2f(min.x, min.y);
		this.max = new Vector2f(max.x, max.y);
	}
	
	public Line2D(float xMin, float yMin, float xMax, float yMax) {
		this.min = new Vector2f(xMin, yMin);
		this.max = new Vector2f(yMin, yMax);
	}
	
	public String toString() {
		return "{" + min + ", " + max + "}";
	}
}
