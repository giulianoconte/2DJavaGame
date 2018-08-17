package game;

import static utils.OutputUtils.*;

import engine.time.Timer;
import graphics.Shader;
import math.Matrix4f;
import math.Perspective;
import math.Vector2f;

public class RedDot extends Entity {
	
	protected int deathDelay;

	public RedDot(Vector2f pos) {
		position.set(pos);
		size.set(0.05f*1f, 0.05f*1f);
		depth = 20.0f;
		textureString = "res/red.png";
		shader = Shader.ROOM;
		deathDelay = Timer.createDelaySeconds(0.001f);
//		deathDelay = Timer.createDelaySeconds(50000f);
		setMesh();
		init();
	}
	
	public RedDot(Vector2f pos, boolean big) {
		position.set(pos);
		size.set(0.05f*3f, 0.05f*3f);
		depth = 20.0f;
		textureString = "res/red.png";
		shader = Shader.ROOM;
		deathDelay = Timer.createDelaySeconds(0.001f);
//		deathDelay = Timer.createDelaySeconds(50000);
		setMesh();
		init();
	}
	
	public RedDot(Vector2f pos, int i) {
		position.set(pos);
		size.set(0.05f*7f, 0.05f*7f);
		depth = 20.0f;
		textureString = "res/redring.png";
		shader = Shader.ROOM;
		deathDelay = Timer.createDelaySeconds(0.001f);
//		deathDelay = Timer.createDelaySeconds(50000);
		setMesh();
		init();
	}
	
	public void update() {
		if (Timer.isOver(deathDelay)) {
			remove();
		}
	}
	
	public void render() {
		if (render) {
			texture.bind();
			shader.enable();
			shader.setUniform2f("player", player.position.x + scrollOffset.x, player.position.y + scrollOffset.y);
			shader.setUniformMat4f("vw_matrix", Matrix4f.translate(Vector2f.add(position, scrollOffset)).multiply(Matrix4f.rotate(rot)));
			shader.setUniform1i("currentRoom", 1);
			shader.setUniform1i("lighthing", 0);
			
			mesh.render();
			
			shader.disable();
			texture.unbind();
		}
	}
	
}
