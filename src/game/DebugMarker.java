package game;

import engine.time.Timer;
import graphics.Shader;
import math.Matrix4f;
import math.Vector2f;

public class DebugMarker extends Entity {
	
	private static final Vector2f size0 = new Vector2f(0.05f*1f, 0.05f*1f);
	private static final String textureString0 = "res/red.png";
	private static final Vector2f size1 = new Vector2f(0.05f*3f, 0.05f*3f);
	private static final String textureString1 = "res/red.png";
	private static final Vector2f size2 = new Vector2f(0.05f*7f, 0.05f*7f);
	private static final String textureString2 = "res/redring.png";

	public DebugMarker() {
		this(new Vector2f(), 0);
	}
	
	public DebugMarker(Vector2f pos) {
		this(pos, 0);
	}
	
	public DebugMarker(Vector2f pos, int type) {
		position.set(pos);
		switch (type) {
			case 0:
				size.set(size0);
				textureString = textureString0;
				break;
			case 1:
				size.set(size1);
				textureString = textureString1;
				break;
			case 2:
				size.set(size2);
				textureString = textureString2;
				break;
		}
		depth = 20.0f;
		shader = Shader.ROOM;
		setMesh();
		init();
	}
	
	public void update() {}
	
	public void changeType(int type) {
		boolean a;
		boolean b;
		switch (type) {
			case 0:
				a = size.x == size0.x && size.y == size0.y;
				b = textureString.equals(textureString0);
				if (!a) size = size0;
				if (!b) textureString = textureString0;
				if (!a || !b) setMesh();
				break;
			case 1:
				a = size.x == size1.x && size.y == size1.y;
				b = textureString.equals(textureString1);
				if (!a) size = size1;
				if (!b) textureString = textureString1;
				if (!a || !b) setMesh();
				break;
			case 2:
				a = size.x == size2.x && size.y == size2.y;
				b = textureString.equals(textureString2);
				if (!a) size = size2;
				if (!b) textureString = textureString2;
				if (!a || !b) setMesh();
				break;
			default:
				System.err.println("DEBUGMARKER TYPE " + type + " NOT VALID");
				break;
		}
	}
	
	public void teleport(Vector2f pos) {
		position.set(pos);
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
			
			render = false;
		}
	}
}
