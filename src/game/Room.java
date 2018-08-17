package game;

import static utils.OutputUtils.p;

import static org.lwjgl.glfw.GLFW.*;
import engine.time.Timer;
import graphics.Shader;
import input.Input;
import math.Matrix4f;
import math.Perspective;
import math.Vector2f;
import math.Vector3f;

public class Room extends Entity {
	private boolean flag = true;
	public static Vector2f backgroundSize = new Vector2f(Perspective.top * 2, Perspective.top * 2);
	
	public Room(float x, float y, float r) {
		size = new Vector2f(Perspective.top * 2, Perspective.top * 2);
		position = new Vector2f(x, y);
		depth = -10f;
		rot = r;
		textureString = "res/black.png";
//		textureString = "res/cube.jpg";
		shader = Shader.ROOM;
		setMesh();
		init(); //also sets initialized to true
	}
	
	public void update() {
//		float[] tcsNew = new float[] {		//texture coordinates
//			0, 0.5f, 						//lower left
//			0, 0,						//upper left
//			0.5f, 0,						//upper right
//			0.5f, 0.5f						//lower right
//		};
//		if (Input.down(GLFW_KEY_O) && flag) {
//			mesh.rebindTextureCoordinates(tcsNew);
//			flag = !flag;
//		} else if (Input.down(GLFW_KEY_O) && !flag) {
//			mesh.rebindTextureCoordinates(tcs);
//			flag = !flag;
//		}
//		mesh.resetMesh(vertices, indices, tcsNew);
//		mesh.rebindTextureCoordinates(tcsNew);
		player = level.entities.player();
//		super.steer();
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
		if (render) shader.setUniform1i("currentRoom", 1);
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




