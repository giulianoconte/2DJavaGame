package game;

import static math.Direction.*;
import static utils.OutputUtils.p;

import engine.Physics;
import engine.time.Timer;
import graphics.Shader;
import input.Input;
import math.Matrix4f;
import math.Perspective;
import math.Vector2f;
import math.Vector3f;

public class Bullet extends Entity {
	
	private boolean homing = false;
	private float turnSpeed = 5;
	protected int deathDelay;
	
	public Bullet(float x, float y, float a) {
		this(new Vector2f(x, y), a);
	}
	
	public Bullet(Vector2f pos, float a) {
		maxSpeed = 1f;
		maxForce = 1f;
		angle = a;
		mass = 0.1f*1;
		rot = a;
		size = new Vector2f(1f, 0.04f);
//		size = new Vector2f(0.6f, 0.04f);
		position = new Vector2f(pos.x, pos.y);
		depth = 0.2f;
		target = position;
		target = Vector2f.add(position, new Vector2f(angle));
//		target = level.entities.player().position.add(Vector3f.zero);
		textureString = "res/gunmetal.png";
		shader = Shader.ROOM;
		render = true;
		deathDelay = Timer.createDelaySeconds(0.1f);
		
		velocity = new Vector2f(angle).setMag(maxSpeed);
		
		setMesh();
		init();
	}
	
	public void update() {
		player = level.entities.player();
//		target.add(new Vector2f(angle));
//		seek();
//		steer();
		move();
		
//		if (homing) {
//			float targetAngle = getAngle(position, Input.getXY().sub(scrollOffset.to2D()));
//			float totalRotation = targetAngle - angle;
//			while (totalRotation < -180) totalRotation += 360;
//			while (totalRotation > 180)  totalRotation -= 360;
//			if (totalRotation < 0.0) {
//				angle -= turnSpeed;
//			} else if (totalRotation > 0.0) {
//				angle += turnSpeed;
//			}
//			rot = angle;
//		}
//		delta.x = cos(angle) * speed;
//		delta.y = sin(angle) * speed;
//		super.update();
		if (Timer.isOver(deathDelay)) remove();
		for (Entity e : level.entities.getMobs()) {
			if (Physics.collision(this, e)) {
				e.collide(this);
				remove();
			}
		}
		if (position.x > Perspective.right * 10 ||
			position.x < Perspective.left  * 10 ||
			position.y > Perspective.right * 10 ||
			position.y < Perspective.left  * 10) remove();
	}
	
	public void render() {
//		super.render();
		texture.bind();
		shader.enable();
		shader.setUniform2f("player", player.position.x + scrollOffset.x, player.position.y + scrollOffset.y);
		shader.setUniformMat4f("vw_matrix", Matrix4f.translate(Vector2f.add(position, scrollOffset)).multiply(Matrix4f.rotate(rot)));
		if (render) shader.setUniform1i("currentRoom", 1);
		else shader.setUniform1i("currentRoom", 0);	
		if (lighting) shader.setUniform1i("lighting", 1);
		else shader.setUniform1i("lighting", 0);
		mesh.render();
		shader.disable();
		texture.unbind();
	}
}




