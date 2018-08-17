package game;

import graphics.Shader;
import math.Vector2f;
import math.Vector3f;

public class PlayerMobFiller extends PlayerMob {
	public PlayerMobFiller() {
		isFiller = true;
		size = new Vector2f();
		position = new Vector2f();
		depth = 0f;
		rot = 0f;
		textureString = "res/black.png";
		shader = Shader.ROOM;
		setMesh();
	}
	public void getInput() {}
	public void update() {}
	public void render() {}
}
