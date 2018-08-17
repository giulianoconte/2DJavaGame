package game;

import static utils.OutputUtils.*;
import static math.Direction.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import engine.Physics;
import engine.navigation.Navigator;
import engine.time.Timer;
import input.Input;
import math.Perspective;
import math.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Level {
	
	public EntityList entities;
	private boolean stepMode = false;
	private boolean reset = false;
	private boolean hasSpawned = false;
	private int reflect;
	private int reflectTimer;
	
	private Random r = new Random();
	
	public Level() {}
	
	public void init() {
		entities = new EntityList();
		entities.init();
		Navigator.init();
		spawn(new Room(-Room.backgroundSize.x, Room.backgroundSize.y, r.nextInt(4)*0f));
		spawn(new Room(0, Room.backgroundSize.y, r.nextInt(4)*0f));
		spawn(new Room(Room.backgroundSize.x, Room.backgroundSize.y, r.nextInt(4)*0f));
		spawn(new Room(-Room.backgroundSize.x, 0, r.nextInt(4)*0f));
		spawn(new Room(0f, 0f, r.nextInt(4)*0f));
		spawn(new Room(Room.backgroundSize.x, 0, r.nextInt(4)*0f));
		spawn(new Room(-Room.backgroundSize.x, -Room.backgroundSize.y, r.nextInt(4)*0f));
		spawn(new Room(0, -Room.backgroundSize.y, r.nextInt(4)*0f));
		spawn(new Room(Room.backgroundSize.x, -Room.backgroundSize.y, r.nextInt(4)*0f));
//		spawn(new Room(-Room.backgroundSize.x, Room.backgroundSize.y, r.nextInt(4)*90f));
//		spawn(new Room(0, Room.backgroundSize.y, r.nextInt(4)*90f));
//		spawn(new Room(Room.backgroundSize.x, Room.backgroundSize.y, r.nextInt(4)*90f));
//		spawn(new Room(-Room.backgroundSize.x, 0, r.nextInt(4)*90f));
//		spawn(new Room(0f, 0f, r.nextInt(4)*90f));
//		spawn(new Room(Room.backgroundSize.x, 0, r.nextInt(4)*90f));
//		spawn(new Room(-Room.backgroundSize.x, -Room.backgroundSize.y, r.nextInt(4)*90f));
//		spawn(new Room(0, -Room.backgroundSize.y, r.nextInt(4)*90f));
//		spawn(new Room(Room.backgroundSize.x, -Room.backgroundSize.y, r.nextInt(4)*90f));
		
		reflect = 0;
		reflectTimer = Timer.createDelaySeconds(0.05f);
		
		spawn(new PlayerMob(0f, 0f));
		for (int i = 0; i < 0; i++) {
			spawn(new Zombie((r.nextFloat() - 0.5f) * 2f * 5f, (r.nextFloat() - 0.5f) * 2f * 5f));
//			spawn(new Zombie(0f, 0f));
		}
//		spawn(new PlayerMob((r.nextFloat() - 0.5f) * Room.backgroundSize.x, (r.nextFloat() - 0.5f) * Room.backgroundSize.y));
//		spawn(new Wall(1f, 1f));
//		spawn(new Wall(1f, 0f));
		for (int i = 0; i < 1; i++) {
//			spawn(new PathAgent(-10f, 0.49f));
//			spawn(new PathAgent(-10f, (r.nextFloat() - 0.5f) * 2));
			spawn(new PathAgent((r.nextFloat() - 0.5f) * 2f * 5f, (r.nextFloat() - 0.5f) * 2f * 5f));
		}
		for (int i = 0; i < 50; i++) {
			int x = (int)((r.nextFloat() - 0.5f) * 2f * 5f);
			int y = (int)((r.nextFloat() - 0.5f) * 2f * 5f);
			spawn(new Wall(x, y));
		}
		entities.printInfo();
		
	}
	
	public void spawn(Entity e) {
		entities.add(e);
	}
	
	public void displayDebugMarker(Vector2f pos) {
		displayDebugMarker(pos, 0);
	}
	
	public void displayDebugMarker(Vector2f pos, int type) {
		entities.displayDebugMarker(pos, type);
	}
	
	private Vector2f target = new Vector2f(-99f, -99f);
	
	public void getInput() {
		if (Input.down(GLFW_KEY_ESCAPE)) Game.endGame();
		
		if (Input.press(GLFW_MOUSE_BUTTON_1)) {
			for (Entity e : entities.getMobs()) {
				if (e instanceof PathAgent) {
					PathAgent pa = (PathAgent) e;
					if (!pa.selected) {
						if (Physics.collision(Input.getXY(), e)) {
							pa.selected = true;
						}
					} else {
						pa.path = Navigator.getPathNow(pa.position, Input.getXY());
						target = Input.getXY();
					}
				}
			}
		}
		displayDebugMarker(target, 2);
		if (Input.press(GLFW_MOUSE_BUTTON_2)) {
			boolean wallFlag = false;
			for (Wall w : entities.getWalls()) {
				if (Physics.collision(Input.getXY(), w)) {
					w.remove();
					wallFlag = true;
					break;
				}
			}
			if (!wallFlag) spawn(new Wall(Input.getXY()));
		}
		
		
		if (Input.press(GLFW_KEY_N)) reset = true;
		if (Input.press(GLFW_KEY_PERIOD)) stepMode = !stepMode;
		if (Input.press(GLFW_KEY_K)) {
			if (entities.player().isFiller) spawn(new PlayerMob((r.nextFloat() - 0.5f) * Room.backgroundSize.x, (r.nextFloat() - 0.5f) * Room.backgroundSize.y));
			else 							entities.player().remove();
		}
		if (Input.press(GLFW_KEY_L)) Entity.lighting = !Entity.lighting;
		if (Input.press(GLFW_KEY_F)) Room.alpha = (Room.alpha + 1) % 2;
		if (Input.down(GLFW_KEY_T) && Timer.isOver(reflectTimer)) {
			entities.player().shader.setUniform1i("reflect", reflect);
			reflect = (reflect + 1) % 2;
			PlayerMob.reflect = reflect;
			Timer.reset(reflectTimer);
		}
		if (Input.press(GLFW_KEY_I)) entities.printInfo();
	}
	
	public void update() {
		getInput();
		if (!stepMode) {
			entities.updateAll();
			Navigator.update();
		} else {
			if (Input.down(GLFW_KEY_LEFT_SHIFT) && Input.press(GLFW_KEY_SPACE)) {
				for (int i = 0; i < 10; i++) {
					entities.updateAll();
					Navigator.update();
				}
			} else if (Input.press(GLFW_KEY_SPACE)) {
				entities.updateAll();
				Navigator.update();
			}
		}
	}
	
	public void render() {
		entities.renderAll();
	}
	
	public boolean isRoundOver() {
		return reset;
	}
}
