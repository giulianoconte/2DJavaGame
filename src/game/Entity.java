package game;

import static utils.OutputUtils.*;

import java.util.ArrayList;
import java.util.Random;

import engine.Physics;
import engine.navigation.WayPoint;
import graphics.Shader;
import graphics.Texture;
import graphics.VertexArray;
import math.Matrix4f;
import math.Perspective;
import math.Vector2f;
import math.Vector3f;
import math.Linear;

/**
 * Entity is anything that could/will be drawn to the window.
 */
public class Entity {
	protected boolean exists = true;
	protected boolean initialized = false;
	
	protected boolean scrollWrap = true;
	
	protected VertexArray mesh;
	protected Texture texture;
	protected String textureString = "res/black.png";
	protected Shader shader = Shader.PLAYER;
	public static int alpha = 1;
	
	protected float[] vertices; 	//vertices
	protected byte[] indices; 	//indices -- vertices of the two triangles that make up the mesh
	protected float[] tcs;		//texture coordinates
	
	protected Level level = Game.getGame().getLevel();
	protected PlayerMob player = level.entities.player();
	protected Vector2f size = new Vector2f(1.0f, 1.0f);
	
	protected Vector2f target = new Vector2f();
	protected Vector2f acceleration = new Vector2f();
	protected Vector2f velocity = new Vector2f();
	protected Vector2f position = new Vector2f(); //x,y,z
	protected float maxForce = 0.002f; //slowdown and speedup rate
	protected float maxSpeed = 0.02f;  //max speed
	protected Vector2f delta = new Vector2f(); //dx,dy,dz
	protected Vector2f deltaOld = new Vector2f(); //old dx,dy,dz used for scrolling to follow the player
	protected float angle = 0f; //angle it is moving, in degrees
	protected float mass = 2f;
	protected int hp = 3;
	protected int damage = 1;
	
	protected float stopMult = 1f;
	protected float seekMult = 1f;
	protected float arriveMult = 1f;
	protected float alignMult = 1f;
	protected float separateMult = 1f;
	protected float cohereMult = 1f;
	
	protected float rot = 0f;   //rotation, in degrees
	protected float depth = Perspective.far; //perspective.far is bugged, actually is closest to camera you can get . . .
	
	protected static boolean lighting = false;
	protected boolean render = true;
	protected Room currentRoom;
	protected boolean inPlayerRoom = false;
	//offset for scrolling, moves in opposite direction of player
	public static Vector2f scrollOffset = new Vector2f();
	
	
	protected Random r = new Random();
	protected float noise = 0f;
	
	public Entity() {}
	
	//sets up mesh and texture for drawing
	protected void setMesh() {
		vertices = new float[] {
		//			x			y		z
			-size.x / 2.0f, -size.y / 2.0f, depth,	//lower left vertex
			-size.x / 2.0f,  size.y / 2.0f, depth,	//upper left vertex
			 size.x / 2.0f,  size.y / 2.0f, depth,	//upper right vertex
			 size.x / 2.0f, -size.y / 2.0f, depth,	//lower right vertex
		};
		
		indices = new byte[] {	//these represent 2 triangles for the rectangular mesh
			0, 1, 2, 					//triangle 1: lower left, upper left, upper right
			2, 3, 0						//triangle 2: upper right, lower right, lower left
		};
		
		tcs = new float[] {		//texture coordinates
			0, 1, 						//lower left
			0, 0,						//upper left
			1, 0,						//upper right
			1, 1						//lower right
		};
		
		mesh = new VertexArray(vertices, indices, tcs);		//creates mesh with vertices, indices, and texture coordinates
		texture = new Texture(textureString);				//path from project directory to texture
	}
	
	protected void setTexture(String ts) {
		texture = new Texture(ts);
	}
	
	public boolean getExists() {
		return exists;
	}
	
	public void remove() {
		exists = false;
	}
	
	public Vector2f getPos() {
		return position;
	}
	
	public Vector2f getSize() {
		return size;
	}
	
	public float getRot() {
		return rot;
	}
	
	public void setRot(float r) {
		rot = r;
	}
	
	public boolean getRender() {
		return render;
	}
	
	public void setRender(boolean r) {
		render = r;
	}
	//updates position and resets deltas
	public void update() {
		position.x += delta.x;
		position.y += delta.y;
		deltaOld.x = delta.x;
		deltaOld.y = delta.y;
		delta.x = 0;
		delta.y = 0;
		scroll();
	}
	
	public void collide(Entity e) {
//		Vector2f p1 = Vector2f.times(velocity, mass);
		Vector2f p2 = Vector2f.times(e.velocity, e.mass);
//		p("p2 before: " + p2);
//		p("division: " + (1f/mass));
		p2.times(1f/mass);
//		p("p2 after: " + p2);
		velocity.add(p2);
		hp -= e.damage;
	}
	
	public void move() {
		position.add(velocity);
		deltaOld = velocity;
		if (scrollWrap) {
			Vector2f diff = Vector2f.sub(position, player.position);
			if (Math.abs(diff.x) > Perspective.right + 0.5f) 	position.x -= diff.x * 1.999f;
			if (Math.abs(diff.y) > Perspective.top + 0.5f) 	position.y -= diff.y * 1.999f;
		}
	}
	
	public void steer() {
		velocity.add(acceleration);
//		p("velocity: " + velocity);
		move();
//		delta.reset();
		acceleration.reset();
		scroll();
	}
	
	public void stop() {
		Vector2f steer = Vector2f.scale(velocity, -1);
		steer.limit(maxForce);
		steer.times(stopMult);
		applyForce(steer);
	}
	
	public void wander() {
		noise += (r.nextFloat() - 0.5f) * 2 * 20f; // * strength
		Vector2f goal = Vector2f.add(position, Vector2f.scale(velocity, 10f));
		goal.add(Vector2f.scale(Vector2f.rotate(velocity, noise), 0.1f)); // scale rate
		seek(goal);
	}
	
	public void seek(Vector2f goal) {
		Vector2f desired = Vector2f.sub(goal, position);
		desired.setMag(maxSpeed);
		
		Vector2f steer = desired.sub(velocity);
		steer.limit(maxForce);
		steer.times(seekMult);
		applyForce(steer);
	}
	
	public void arrive(Vector2f goal, float radius) {
		Vector2f desired = Vector2f.sub(goal, position);
		float distance = desired.length();
		if (distance < radius) desired.setMag(Linear.map(distance, 0, radius, 0, maxSpeed));
		else desired.setMag(maxSpeed);
		
		Vector2f steer = desired.sub(velocity);
		steer.limit(maxForce);
		steer.times(arriveMult);
		applyForce(steer);
	}
	
	public Vector2f align(ArrayList<? extends Entity> neighbors) {
		if (neighbors.size() <= 0) return new Vector2f(); //no influence from neighbors because no neighbors
		Vector2f desired = new Vector2f(); //neighborhood velocity
		int numNeighbors = 0;
		for (Entity neighbor : neighbors) {
			desired.add(neighbor.velocity);
			numNeighbors++;
		}
		desired.divide(numNeighbors);
		desired.setMag(maxSpeed);
		
		Vector2f steer = desired.sub(velocity);
		steer.limit(maxForce);
		steer.times(alignMult);
		applyForce(steer);
		return steer;
	}
	
	public Vector2f separate(ArrayList<? extends Entity> neighbors) {
		if (neighbors.size() <= 0) return new Vector2f(); //no influence from neighbors because no neighbors
		Vector2f desired = new Vector2f();
		Vector2f difference = new Vector2f();
		float distance;
		int numNeighbors = 0;
		for (Entity neighbor : neighbors) {
			difference = Vector2f.sub(position, neighbor.position);
			distance = difference.length();
			difference.times(1/(float)Math.pow(distance, 2));
			desired.add(difference);
			numNeighbors++;
		}
		desired.divide(numNeighbors);
		desired.setMag(maxSpeed);
		
		Vector2f steer = desired.sub(velocity);
		steer.limit(maxForce);
		steer.times(separateMult);
		applyForce(steer);
		return steer;
	}
	
	public Vector2f cohere(ArrayList<? extends Entity> neighbors) {
		if (neighbors.size() <= 0) return new Vector2f(); //no influence from neighbors because no neighbors
		Vector2f desired = new Vector2f(); //neighborhood velocity
		int numNeighbors = 0;
		for (Entity neighbor : neighbors) {
			desired.add(neighbor.position);
			numNeighbors++;
		}
		desired.divide(numNeighbors);
		desired.sub(position); //vector from current position to center of group
		desired.setMag(maxSpeed);
		
		Vector2f steer = desired.sub(velocity);
		steer.limit(maxForce);
		steer.times(cohereMult);
		applyForce(steer);
		return steer;
	}
	
	public Vector2f predictFutureLocation(int timeFromNow) {
		Vector2f future;
		if (velocity.length() == 0.0f) {
			future = Vector2f.setMag(new Vector2f(angle), 0.01f); 
		} else {
			future = Vector2f.add(Vector2f.scale(velocity, timeFromNow), Vector2f.setMag(new Vector2f(angle), 0.01f));
		}
		return Vector2f.add(position, future);
	}
	
	public void pathFollow(Vector2f start, Vector2f end) {
		Vector2f futurePosition = predictFutureLocation(4);
		Vector2f future = Vector2f.sub(futurePosition, start);
		Vector2f normal = Vector2f.sub(end, start);
		normal.normalize();
		normal.scale(Vector2f.dot(future, normal));
		Vector2f normalPosition = Vector2f.add(start, normal);
		
		Vector2f dir = Vector2f.sub(end, start);
		dir.normalize();
		dir.scale(3);
		Vector2f target = Vector2f.add(normalPosition, dir);
		
		float distance = Vector2f.distance(normalPosition, futurePosition);
		if (distance > 2.5f) { //path radius
			seek(target);
		} else {
			seek(futurePosition);
		}
	}
	//TODO: reduce amount of vector2fs used in path follow algorithms, can probably remove normal1, normal2 etc
	public void pathFollow(Vector2f wp1, Vector2f wp2, Vector2f wp3) {
		if (wp1 == null || wp2 == null) return;
		
		Vector2f futurePosition = predictFutureLocation(4);
		Vector2f future1 = Vector2f.sub(futurePosition, wp1);
		Vector2f normal1 = Vector2f.sub(wp2, wp1);
		normal1.normalize();
		normal1.scale(Vector2f.dot(future1, normal1));
		Vector2f normalPosition1 = Vector2f.add(wp1, normal1);
		
		Vector2f future2;
		Vector2f normal2;
		Vector2f normalPosition2 = new Vector2f();
		if (!Physics.pointOnSegment(normalPosition1, wp1, wp2)) normalPosition1 = wp2;
		level.displayDebugMarker(normalPosition1, 2);
		if (wp3 != null) {
			future2 = Vector2f.sub(futurePosition, wp2);
			normal2 = Vector2f.sub(wp3, wp2);
			normal2.normalize();
			normal2.scale(Vector2f.dot(future2, normal2));
			normalPosition2 = Vector2f.add(wp2, normal2);
			if (!Physics.pointOnSegment(normalPosition2, wp2, wp3)) normalPosition2 = wp3;
//			level.displayDebugMarker(normalPosition2, 2); //fix rendering line here... it slows down significantly when on the last two points
		}
		Vector2f a;
		Vector2f b;
		Vector2f normal;
		int index;
		if (Vector2f.distancel2squared(futurePosition, normalPosition1) < Vector2f.distancel2squared(futurePosition, normalPosition2) || wp3 == null) {
			normal = normalPosition1;
			a = wp1;
			b = wp2;
			index = 0;
		} else {
			normal = normalPosition2;
			a = wp2;
			b = wp3;
			index = 1;
		}
		level.displayDebugMarker(normal, 1);
		Vector2f dir = Vector2f.sub(b, a);
		dir.normalize();
		dir.scale(0.5f);
		Vector2f target = Vector2f.add(normal, dir);
		if (!Physics.pointOnSegment(target, a, b) && index == 0 && wp3 != null) {
			dir = Vector2f.sub(wp3, wp2);
			dir.normalize();
			dir.scale(0.5f);
			target = Vector2f.add(normal, dir);
		}
		level.displayDebugMarker(target, 0);
		
		float distance = Vector2f.distance(normal, futurePosition);
		if (distance > 0.5f) 	seek(target); //path radius
		else 					seek(futurePosition);
	}
	
	public void pathFollow(ArrayList<WayPoint> wps) {
		Vector2f futurePosition = predictFutureLocation(4);
		Vector2f future;
		Vector2f closestNormal = new Vector2f(99999f, 99999f);
		float closestNormalLength = Float.POSITIVE_INFINITY;
		float normalLength;
		int index = 0;
		Vector2f wp1 = wps.get(0);
		Vector2f wp2 = wps.get(1);
		Vector2f normal = new Vector2f();;
		Vector2f normalPosition = new Vector2f();
		for (int i = 0; i < wps.size() - 1; i++) {
			future = Vector2f.sub(futurePosition, wps.get(i));
			normal = Vector2f.sub(wps.get(i+1), wps.get(i));
			normal.normalize();
			normal.scale(Vector2f.dot(future, normal));
			normalPosition = Vector2f.add(wps.get(i), normal);
			if (!Physics.pointOnSegment(normalPosition, wps.get(i), wps.get(i+1))) {
				normalPosition = wps.get(i+1);
			}
			level.spawn(new RedDot(normalPosition, 1));
			normalLength = Vector2f.distancel2squared(futurePosition, normalPosition);
			if (normalLength < closestNormalLength) {
				closestNormal = normalPosition;
				closestNormalLength = normalLength;
				index = i;
				wp1 = wps.get(i);
				wp2 = wps.get(i+1);
			}
		}
//		level.spawn(new RedDot(futurePosition));
		level.spawn(new RedDot(closestNormal, true));
		Vector2f dir = Vector2f.sub(wp2, wp1);
		dir.normalize();
		dir.scale(0.5f);
		Vector2f target = Vector2f.add(closestNormal, dir);
		if (!Physics.pointOnSegment(target, wp1, wp2)) {
			if (index < wps.size() - 2) {
				dir = Vector2f.sub(wps.get(index+2), wps.get(index+1));
				dir.normalize();
				dir.scale(0.5f);
				target = Vector2f.add(closestNormal, dir);
			}
		}
		level.spawn(new RedDot(target, true));
		
		float distance = Vector2f.distance(closestNormal, futurePosition);
		if (distance > 0.5f) {
			seek(target);
		} else {
			seek(futurePosition);
		}
	}
	
	public void applyForce(Vector2f force) {
		acceleration.add(force);
	}
	
	protected void init() {
		initScroll();
		player = level.entities.player();
		initialized = true;
	}
	
	public void initScroll(Vector2f playerPos) {
//		scrollOffset = Vector2f.sub(Vector2f.zero, level.entities.player().position);
		scrollOffset.setNegative(level.entities.player().position);
	}
	
	public void initScroll() {
//		scrollOffset = Vector2f.zero.sub(level.entities.player().position);
//		scrollOffset = Vector2f.sub(Vector2f.zero, level.entities.player().position);
		scrollOffset.setNegative(level.entities.player().position);
	}
	
	public void scroll() {
//		scrollOffset = Vector2f.sub(Vector2f.zero, level.entities.player().position);
		scrollOffset.setNegative(player.position);
	}
	
	public void render() {
//		if (shader == Shader.ROOM) {
//			texture.bind();
//			shader.enable();
//			shader.setUniform2f("player", player.position.x + scrollOffset.x, player.position.y + scrollOffset.y);
//			shader.setUniformMat4f("vw_matrix", Matrix4f.translate(position.add(scrollOffset)).multiply(Matrix4f.rotate(rot)));
//			if (render) shader.setUniform1i("currentRoom", 1);
//			else shader.setUniform1i("currentRoom", 0);	
//			if (lighting) shader.setUniform1i("lighting", 1);
//			else shader.setUniform1i("lighting", 0);
//			mesh.render();
//			shader.disable();
//			texture.unbind();
//			texture.bind();
//		}
	}
	
	public void spawn(Entity e) {
		level.spawn(e);
	}
}

