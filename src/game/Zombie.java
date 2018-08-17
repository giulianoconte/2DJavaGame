package game;

import static math.Direction.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static utils.OutputUtils.*;

import java.util.ArrayList;

import engine.Physics;
import engine.navigation.Navigator;
import engine.navigation.WayPointPath;
import engine.time.Time;
import engine.time.Timer;
import engine.navigation.WayPoint;
import graphics.Shader;
import input.Input;
import math.Matrix4f;
import math.Perspective;
import math.Vector2f;
import math.Vector3f;

public class Zombie extends Entity {
	private boolean flag = true;
	public enum State {IDLE, CHASE, GO, FLOCK,}
	protected State state;
	protected float alertRadius;
	protected WayPointPath path;
	protected float pathRadius;
	protected int pathUpdateDelay;
	protected ArrayList<Zombie> alignNeighbors;
	protected ArrayList<Zombie> separateNeighbors;
	protected ArrayList<Zombie> cohereNeighbors;
	protected float alignRadius;
	protected float separateRadius;
	protected float cohereRadius;
	
	public Zombie() {}
	
	public Zombie(Vector2f v) {
		this(v.x, v.y);
	}
	
	public Zombie(float x, float y) {
		hp = 5;
		
		angle = 0f;
		size = new Vector2f(1.0f*0.2f, 0.75f*0.2f);
		position = new Vector2f(x, y);
		depth = 0.4f;
		rot = 90f;
		maxSpeed = 0.02f*3;
//		maxForce = 0.005f*1f;
		maxForce = maxSpeed/20.0f*1f;
		textureString = "res/darkgrey.png";
		shader = Shader.MOB;
		render = true;

		state = State.FLOCK;
		alertRadius = 4f*0.0001f;
		
		path = new WayPointPath();
//		path = new WayPointPath(true);
//		path.add(new WayPoint(10.0f, 10.0f));
//		path.add(new WayPoint(10.0f, -10.0f));
//		path.add(new WayPoint(-10.0f, -10.0f));
//		path.add(new WayPoint(-10.0f, 10.0f));
//		path.add(new WayPoint(0.0f, 5.0f));
//		path = Navigator.getPathNow(position, new Vector2f(0f, 0f));
		for (WayPoint wp : path.getPath()) {
//			level.spawn(new RedDot(wp, true));
		}
		pathRadius = 0.8f*0.25f;
		pathUpdateDelay = Timer.createDelaySeconds(1f/60);
		
		alignRadius = 1f*0.4f;
		separateRadius = 1f*0.35f;
		cohereRadius = 1f*0.3f;
		seekMult = 1f;
		alignMult = 0.8f;
		separateMult = 0.9f;
		cohereMult = 0.7f;
		velocity = Vector2f.random().setMag(maxSpeed);
		angle = Vector2f.getAngle(velocity);
		
		setMesh();
		init();
	}
	
	public void updateState() {
		switch (state) {
			case IDLE:
				if (Vector2f.length(Vector2f.sub(player.position, this.position)) < alertRadius) {
					state = State.CHASE;
				} else if (!path.isFinished() && path.exists()) {
					if (path.isStarted()) target = path.current();
					else target = path.next();
					state = State.GO;
				}
				break;
			case CHASE:
				if (Vector2f.length(Vector2f.sub(player.position, this.position)) >= alertRadius) {
					state = State.IDLE;
				}
				break;
			case GO:
				if (Vector2f.length(Vector2f.sub(player.position, this.position)) < alertRadius) {
					state = State.CHASE;
				} else if (path.isFinished()) {
					state = State.IDLE;
				}
				break;
			case FLOCK:
				break;
			default:
				break;
		}
	}
	
	public void update() {
		if (Timer.isOver(pathUpdateDelay)) {
			Timer.reset(pathUpdateDelay);
//			path = Navigator.getPathNow(Vector2f.scale(Vector2f.random(), 5f), Vector2f.scale(Vector2f.random(), 5f));
		}
		float[] tcsNew = new float[] {		//texture coordinates
				0, 0.5f, 						//lower left
				0, 0,						//upper left
				0.5f, 0,						//upper right
				0.5f, 0.5f						//lower right
			};
//			if (Input.down(GLFW_KEY_O) && flag) {
//				mesh.resetMesh(vertices, indices, tcsNew);
//				flag = !flag;
//			} else if (Input.down(GLFW_KEY_O) && !flag) {
//				mesh.resetMesh(vertices, indices, tcs);
//				flag = !flag;
//			}
		player = level.entities.player();
		
		updateState();
		switch (state) {
			case IDLE:
				target = position;
				seek(target);
				if (velocity.length() != 0.0f) rot = Vector2f.getAngle(velocity);
				break;
			case CHASE:
				target = player.position;
				rot = getAngle(position, target);
				alignNeighbors = new ArrayList<Zombie>();
				separateNeighbors = new ArrayList<Zombie>();
				cohereNeighbors = new ArrayList<Zombie>();
				for (Zombie z : level.entities.getMobs()) {
					float distance = Vector2f.distance(position, z.position);
					if (distance > 0 && distance <= alignRadius) 		alignNeighbors.add(z);
					if (distance > 0 && distance <= separateRadius) 	separateNeighbors.add(z);
					if (distance > 0 && distance <= cohereRadius)		cohereNeighbors.add(z);
				}
//				rot = Vector2f.getAngle(velocity);
				separate(separateNeighbors);
				arrive(target, 2f);
				break;
			case GO:
				alignNeighbors = new ArrayList<Zombie>();
				separateNeighbors = new ArrayList<Zombie>();
				cohereNeighbors = new ArrayList<Zombie>();
				for (Zombie z : level.entities.getMobs()) {
					float distance = Vector2f.distance(position, z.position);
					if (distance > 0 && distance <= alignRadius) 		alignNeighbors.add(z);
					if (distance > 0 && distance <= separateRadius) 	separateNeighbors.add(z);
					if (distance > 0 && distance <= cohereRadius)		cohereNeighbors.add(z);
				}
				if (Vector2f.distance(position, target) <= pathRadius) {
					if (path.isEnd()) path.increment(); //path iterator will be past endpoint, and path will be "finished"
					else target = path.next();
				}
				rot = Vector2f.getAngle(velocity);
//				separate(separateNeighbors);
				seek(target);
				break;
			case FLOCK:
				flock();
//				wander();
				rot = Vector2f.getAngle(velocity);
				break;
			default:
				break;
		}
		steer();
		angle = Vector2f.getAngle(velocity);
		for (Room r : level.entities.rooms) {
			if (Physics.collision(position, r)) {
				currentRoom = r;
				if (currentRoom.equals(player.currentRoom)) {
					inPlayerRoom = true;
				} else {
					inPlayerRoom = false;
				}
			}
		}
		if (hp <= 0) {
			remove();
		}
	}
	
	protected void flock() {
		alignNeighbors = new ArrayList<Zombie>();
		separateNeighbors = new ArrayList<Zombie>();
		cohereNeighbors = new ArrayList<Zombie>();
		for (Zombie z : level.entities.getMobs()) {
			float distance = Vector2f.distance(position, z.position);
			if (distance > 0 && distance <= alignRadius) 		alignNeighbors.add(z);
			if (distance > 0 && distance <= separateRadius) 	separateNeighbors.add(z);
			if (distance > 0 && distance <= cohereRadius)		cohereNeighbors.add(z);
		}
		align(alignNeighbors);
		separate(separateNeighbors);
		cohere(cohereNeighbors);
//		seek(Input.getXY());
	}
	
	public void render() {
//		super.render();
		if (	position.x + scrollOffset.x >  Perspective.top ||
				position.x + scrollOffset.x < -Perspective.top ||
				position.y + scrollOffset.y >  Perspective.top ||
				position.y + scrollOffset.y < -Perspective.top) {
			render = false;
		} else {
			render = true;
		}
		texture.bind();
		shader.enable();
		shader.setUniform2f("player", player.position.x + scrollOffset.x, player.position.y + scrollOffset.y);
		shader.setUniformMat4f("vw_matrix", Matrix4f.translate(Vector2f.add(position, scrollOffset)).multiply(Matrix4f.rotate(rot)));
		if (inPlayerRoom) shader.setUniform1i("currentRoom", 1);
		else shader.setUniform1i("currentRoom", 0);	
		if (lighting) shader.setUniform1i("lighting", 1);
		else shader.setUniform1i("lighting", 0);
		shader.setUniform1i("alpha", alpha);
		Vector2f mouse = Input.getXY();
		shader.setUniform2f("mouse", mouse.x, mouse.y);
		mesh.render();
		shader.disable();
		texture.unbind();
	}
	
}
