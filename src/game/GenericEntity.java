package game;

import math.Vector2f;
import math.Vector3f;

public class GenericEntity extends Entity {
	
	public GenericEntity(float x, float y, float r) {
		position = new Vector2f(x, y);
		depth = 0f;
		rot = r;
		size.x = -0.0001f;
		size.y = -0.0001f;
	}
	
	public GenericEntity(Vector2f v, float r) {
		this(v.x, v.y, r);
	}
	
	public void update() {}
	
	public void render() {}
}
