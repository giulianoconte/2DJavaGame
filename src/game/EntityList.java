package game;

import static utils.OutputUtils.*;

import java.util.ArrayList;
import java.util.Iterator;

import math.Vector2f;

public class EntityList {
	private ArrayList<ArrayList<Entity>> all;
	public ArrayList<PlayerMob> player; //index 0 is a PlayerMobFiller entity, index 1 will be filled with a PlayerMob entity when it is created
	public ArrayList<Room> rooms;
	public ArrayList<Wall> walls;
	public ArrayList<Zombie> mobs;
	public ArrayList<Bullet> bullets;
	public ArrayList<RedDot> redDots;
	public ArrayList<NavPoint> navPoints;
	public ArrayList<DebugMarker> debugMarkers;
	public ArrayList<Entity> removedEntities;
	
	private Iterator<DebugMarker> it;
	
	public EntityList() {}
	
	public void init() {
		all = new ArrayList<ArrayList<Entity>>();
		//these are added in order of update-ordering (player updates first, then rooms second, etc)
		add(player = new ArrayList<PlayerMob>());
		add(rooms = new ArrayList<Room>());
		add(walls = new ArrayList<Wall>());
		add(mobs = new ArrayList<Zombie>());
		add(bullets = new ArrayList<Bullet>());
		add(redDots = new ArrayList<RedDot>());
		add(navPoints = new ArrayList<NavPoint>());
		add(debugMarkers = new ArrayList<DebugMarker>());
		removedEntities = new ArrayList<Entity>();
		
		player.add(new PlayerMobFiller());
		for (int i = 0; i < 200; i++) {
			debugMarkers.add(new DebugMarker());
		}
		it = debugMarkers.iterator(); //TODO: figure out iterator display stuff
	}
	
	public void updateAll() {
		for (ArrayList<Entity> list : all) 
			updateList(list);
	}
	
	//updates a given list. the "<? extends Entity>" syntax is called an "upper-bounded wildcard" that you can use for ArrayLists of multiple types that extend the same object
	public void updateList(ArrayList<? extends Entity> list) {
		for (Entity e : list) {
			if (e.getExists()) 	e.update();
			else				removedEntities.add(e);
		}
		for (Entity e : removedEntities) {
			list.remove(e);
		}
		removedEntities = new ArrayList<Entity>();
	}
	
	public void renderAll() {
		for (ArrayList<Entity> list : all) 
			renderList(list);
	}
	
	private void renderList(ArrayList<? extends Entity> list) {
		for (Entity e : list) 
			e.render();
	}
	
	private void add(ArrayList<? extends Entity> list) {
		all.add((ArrayList<Entity>) list);
	}
	
	public void add(Entity e) {
		try {
			if (e == null) throw new NullPointerException();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			System.err.println("Tried to add a null entity!");
		}
		//none of the instanceof checks should be against any superclass
		if (e instanceof Room) 						rooms.add((Room) e);
		else if (e instanceof Wall)					walls.add((Wall) e);
		else if (e instanceof Zombie) 				mobs.add((Zombie) e);
		else if (e instanceof Bullet) 				bullets.add((Bullet) e);
		else if (e instanceof PlayerMob)			player.add((PlayerMob) e);
		else if (e instanceof RedDot)				redDots.add((RedDot) e);
		else if (e instanceof NavPoint)				navPoints.add((NavPoint) e);
		else if (e instanceof DebugMarker)			debugMarkers.add((DebugMarker) e);
	}
	
	public ArrayList<ArrayList<Entity>> getAll() {
		return all;
	}
	
	public ArrayList<Wall> getWalls() {
		return walls;
	}
	
	public ArrayList<Zombie> getMobs() {
		return mobs;
	}
	
	public PlayerMob player() {
		try {
			if (player.size() >= 3) 		throw new Exception();
			else if (player.size() == 2) 	return player.get(1);
			else if (player.size() == 1) 	return player.get(0);
			else 							return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("PlayerMob list is greater than or equal to 3! Returning the second PlayerMob in the list -- it should be a non-filler player.");
			return player.get(1);
		}
	}
	
	public void displayDebugMarker(Vector2f pos) {
		displayDebugMarker(pos, 0);
	}
	
	public void displayDebugMarker(Vector2f pos, int type) {
		if (!it.hasNext()) {
			it = debugMarkers.iterator();
		}
		DebugMarker dm = it.next();
		dm.teleport(pos);
		dm.setRender(true);
		dm.changeType(type);
	}
	
	public void printInfo() {
		p("----------------");
		p("ALL size: " + all.size());
		int allTotal = 0;
		for (ArrayList<Entity> list : all) {
			allTotal += list.size();
		}
		p("ALL COMBINED size: " + allTotal);
		p("ROOMS size: " + rooms.size());
		p("WALLS size: " + walls.size());
		p("MOBS size: " + mobs.size());
		p("BULLETS size: " + bullets.size());
		p("PLAYER size: " + player.size());
		p("REDDOT size: " + redDots.size());
		p("NAVPOINT size: " + navPoints.size());
		p("DEBUGMARKER size: " + debugMarkers.size());
		p("REMOVED size: " + removedEntities.size());
		p("----------------");
	}
}
