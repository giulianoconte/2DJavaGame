package game;

import static org.lwjgl.glfw.GLFW.*;
import static utils.OutputUtils.p;

import java.util.ArrayList;

import static math.Direction.*;

import engine.Physics;
import engine.time.Timer;
import graphics.Shader;
import input.Input;
import math.Matrix4f;
import math.Perspective;
import math.Vector2f;
import math.Vector3f;

public class PlayerMob extends Entity {
	
	public static int reflect;
	protected boolean isFiller;
	public boolean shoot = false;
	public int shootDelay;
	
	public PlayerMob() {}
	
	public PlayerMob(float x, float y) {
		isFiller = false;
		size = new Vector2f(1.0f, 1.0f);
		position = new Vector2f(x, y);
		depth = 10f;
		rot = 0f;
		maxSpeed = 0.04f;
		maxForce = 0.008f;
		textureString = "res/white.png";
		shader = Shader.ROOM;
		shootDelay = Timer.createDelaySeconds(0.2f);
		setMesh();
		init();
	}
	
	public boolean getIsFiller() {
		return isFiller;
	}
	
	public void init() {
		for (ArrayList<Entity> list : level.entities.getAll()) {
			for (Entity e : list) {
				e.initScroll();
			}
		}
		this.initScroll();
		initialized = true;
	}
	
	public void getInput() {
		target.set(position);
//		p("target: " + target);
		if (Input.down(GLFW_KEY_W)) target.y += maxSpeed;
		if (Input.down(GLFW_KEY_S)) target.y -= maxSpeed;
		if (Input.down(GLFW_KEY_A)) target.x -= maxSpeed;
		if (Input.down(GLFW_KEY_D)) target.x += maxSpeed;
		if (Input.press(GLFW_KEY_MINUS)) {
			maxSpeed *= 0.4f;
			maxForce *= 0.4f;
		}
		if (Input.press(GLFW_KEY_EQUAL)) {
			maxSpeed *= 2.5f;
			maxForce *= 2.5f;
		}
//		p("target: " + target);
		
//		Zombie zombie = level.entities.getMobs().get(0);
//		target.set(Input.getXY().sub(scrollOffset));
		
		//mouse tracking code
		rot = getAngle(Vector2f.add(position, delta), Input.getXY().sub(scrollOffset));
//		if (Input.down(GLFW_MOUSE_BUTTON_1)) {
//			if (Timer.isOver(shootDelay)) {
//				shoot = true;
//				Timer.reset(shootDelay);
//			}
//		}
		
		if (Input.press(GLFW_KEY_Q)) remove();
//		if (Physics.collision(Input.getXY().sub(scrollOffset), this)) {
//			p("MOUSE IS ON PLAYER");
//		}
//		angle = getAngle(position, Perspective.toProjXY(Input.getXY()).sub(scrollOffset.to2D()));
	}
	
	public void update() {
		player = level.entities.player();
		getInput();
		seek(target);
		steer();
		if (shoot) {
			spawn(new Bullet(position, rot));
			shoot = false;
		}
		for (Room r : level.entities.rooms) {
			if (Physics.collision(position, r)) {
				currentRoom = r;
				inPlayerRoom = true;
			}
		}
	}
	
	public void render() {
//		super.render();
		texture.bind();
		shader.enable();
		shader.setUniform2f("player", position.x + scrollOffset.x, position.y + scrollOffset.y);
		shader.setUniformMat4f("vw_matrix", Matrix4f.translate(Vector2f.add(position, scrollOffset).to3D(depth)).multiply(Matrix4f.rotate(rot)));
		if (render) shader.setUniform1i("currentRoom", 1);
		else shader.setUniform1i("currentRoom", 0);	
		if (lighting) shader.setUniform1i("lighting", 1);
		else shader.setUniform1i("lighting", 0);
//		mesh.render();
		shader.disable();
		texture.unbind();
		
//		texture.bind();
//		shader.enable();
//		shader.setUniform1i("reflect", reflect);
//		shader.setUniformMat4f("vw_matrix", Matrix4f.translate(Vector2f.add(position, scrollOffset).to3D(depth)).multiply(Matrix4f.rotate(rot)));
//		mesh.render();
//		shader.disable();
//		texture.unbind();
	}
}









