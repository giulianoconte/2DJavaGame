package game;

import static utils.OutputUtils.*;
import static org.lwjgl.glfw.GLFW.*;
import static math.Direction.getAngle;

import java.util.ArrayList;

import engine.Physics;
import engine.navigation.Navigator;
import engine.navigation.WayPoint;
import engine.navigation.WayPointPath;
import engine.time.Timer;
import game.Zombie.State;
import graphics.Shader;
import input.Input;
import math.Matrix4f;
import math.Perspective;
import math.Vector2f;

public class PathAgent extends Zombie {
	
	protected boolean selected;
	
	protected WayPoint wp0;
	protected WayPoint wp1;
	protected WayPoint wp2;
	
	public PathAgent(Vector2f v) {
		this(v.x, v.y);
	}
	
	public PathAgent(float x, float y) {
		super(x, y);
		selected = false;
		size = new Vector2f(1.0f*0.5f, 0.75f*0.5f);
		textureString = "res/red.png";
		maxSpeed = 0.07f;
		maxForce = maxSpeed/10f*2f;
		angle = 0f;
		velocity = new Vector2f();
		separateRadius = 1f;
		seekMult = 1f;
		separateMult = 0.8f;
		
		state = State.IDLE;
		
		path = new WayPointPath();
//		path.add(new WayPoint(-10f, 0f));
//		path.add(new WayPoint(0f, 0f));
//		path.add(new WayPoint(-5f, -5f));
//		path.add(new WayPoint(5f, -3f));
//		path.add(new WayPoint(2f, 4f));
//		path.add(new WayPoint(10f, 0f));
//		path.increment();
		
		init();
		setMesh();
	}
	
	public void updateState() {
		switch (state) {
			case IDLE:
				if (!path.isFinished() && path.exists()) {
					if (path.isStarted()) target = path.current();
					else target = path.next();
					state = State.GO;
				}
				break;
			case GO:
				if (path.isFinished()) {
					state = State.IDLE;
				}
				break;
			default:
				break;
		}
	}
	
	public void update() {
		player = level.entities.player();
		
		updateState();
		switch (state) {
			case IDLE:
				stop();
				
				if (velocity.length() != 0.0f) rot = Vector2f.getAngle(velocity);
				break;
			case GO:
				separateNeighbors = new ArrayList<Zombie>();
				for (Zombie z : level.entities.getMobs()) {
					float distance = Vector2f.distance(position, z.position);
					if (distance > 0 && distance <= separateRadius) separateNeighbors.add(z);
				}
//				separate(separateNeighbors);
				pathFollowWayPoint();
				break;
			default:
				break;
		}
//		avoidWalls();
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
	}
	
	protected void pathFollowSteering() {
		if (path.hasLeft() >= 0) wp0 = path.getAhead(0);
		if (path.hasLeft() >= 1) wp1 = path.getAhead(1);
		else wp1 = null;
		if (path.hasLeft() >= 2) wp2 = path.getAhead(2);
		else wp2 = null;
		pathFollow(wp0, wp1, wp2);
		if (path.hasLeft() >= 1 && Vector2f.distance(position, wp1) < path.radius) path.increment();
		if (path.isEnd()) path.increment();
		if (velocity.length() != 0.0f) rot = Vector2f.getAngle(velocity);
	}
	
	protected void pathFollowWayPoint() {
		if (Vector2f.distance(position, target) <= pathRadius) {
			if (path.isEnd()) {
				path.increment(); //path iterator will be past endpoint, and path will be "finished"
			} else {
				target = path.next();
			}
		}
		rot = Vector2f.getAngle(velocity);
		seek(target);
		level.displayDebugMarker(target, 1);
	}
	
	protected void avoidWalls() {
		ArrayList<Wall> wallNeighbors = new ArrayList<Wall>();
		for (Wall w : level.entities.getWalls()) {
			float distance = Vector2f.distance(position, w.position);
			if (distance > 0 && distance <= separateRadius) 		wallNeighbors.add(w);
		}
		separate(wallNeighbors);
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
