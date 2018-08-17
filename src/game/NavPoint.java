package game;

import engine.time.Timer;
import graphics.Shader;
import math.Matrix4f;
import math.Vector2f;

public class NavPoint extends Entity {
	
	protected int deathDelay;

	public NavPoint(Vector2f pos) {
		position.set(pos);
		target.set(position);
		size.set(0.05f, 0.05f);
		size.set(0.95f, 0.95f);
		depth = 0.0f;
		textureString = "res/purple.png";
		shader = Shader.ROOM;
		render = false;
		deathDelay = Timer.createDelaySeconds(5000);
		setMesh();
		init();
	}
	
	public NavPoint(float x_, float y_) {
		this(new Vector2f(x_, y_));
	}
	
	public void update() {
		if (Timer.isOver(deathDelay)) {
			remove();
		}
	}
	
	public void render() {
		texture.bind();
		shader.enable();
		shader.setUniform2f("player", player.position.x + scrollOffset.x, player.position.y + scrollOffset.y);
		shader.setUniformMat4f("vw_matrix", Matrix4f.translate(Vector2f.add(position, scrollOffset)).multiply(Matrix4f.rotate(rot)));
		shader.setUniform1i("currentRoom", 1);
		shader.setUniform1i("lighthing", 1);
		
		if (render) mesh.render();
		
		shader.disable();
		texture.unbind();
	}

}
