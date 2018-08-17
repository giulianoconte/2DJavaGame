package engine.navigation;
import math.Vector2f;

public class WayPoint extends Vector2f {
	public boolean isObstructed = false;
	public WayPoint(float x_, float y_) {
		super(x_, y_);
	}
	public WayPoint(Vector2f v) {
		this(v.x, v.y);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
