package engine.navigation;

import static utils.OutputUtils.*;

import math.Vector2f;
import utils.FileUtils;
import engine.Physics;
import engine.time.Timer;
import game.Entity;
import game.Game;
import game.NavPoint;
import game.RedDot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
//import java.nio.file.Path;
import java.nio.file.Paths;

//TODO: fix djikstra's algorithm
//extra		implement string pulling or funneling
//extra		triangulation of polygons

public class Navigator {
	private static Graph<WayPoint> navmesh;
	private static int updateDelay;
//	private static float spacing;
	private static int sx;
	private static int sy;
	private static HashMap<Integer, WayPointPath> paths;
	private static Queue<Integer> pathQueue; //queue of the keys of the paths to be updated, probably 10-15 paths per cycle
	
//	private static ArrayList<NavPoint> navpoints;
	private static HashMap<WayPoint, NavPoint> navpoints;
	
	
	public static void init() {
		updateDelay = Timer.createDelaySeconds(1f/30);
		float radius = 5f;
		float mult = 1f;
		radius *= mult;
		navmesh = constructGridGraph(-radius, -radius, radius, radius, 1.0f);
		sx = 0;
		sy = 0;
		paths = new HashMap<Integer, WayPointPath>();
		pathQueue = new LinkedList<Integer>();
//		p(navmesh);
		Random r = new Random();
		for (int i = 0; i < navmesh.getVertexList().size(); i++) {
			if (r.nextFloat() < 0.0f) {
				WayPoint wp = navmesh.getVertexList(i);
				wp.isObstructed = true;
			}
		}
//		int runTime = Timer.create();
//		p(getPathNow(new Vector2f(radius, radius), new Vector2f(-radius, -radius)));
//		p("getPathNow runtime: " + Timer.getSeconds(runTime) + " seconds.");
	}
	
	public static void update() {
		if (Timer.isOver(updateDelay)) {
			Timer.reset(updateDelay);
			checkObstructions(navmesh);
		}
	}
	
	public static Graph<WayPoint> constructGridGraph(float x1, float y1, float x2, float y2, float distanceBetween) {
		Graph<WayPoint> g = new Graph<WayPoint>(false);
		WayPoint p1 = new WayPoint(0, 0);
		WayPoint p2 = new WayPoint(0, 0);
		for (float y = y1; y <= y2; y += distanceBetween) {
			sy++;
			for (float x = x1; x <= x2; x += distanceBetween) {
				p1 = new WayPoint(x, y);
				g.add(p1, new ArrayList<Edge<WayPoint>>());
				sx++;
			}
		}
		sx /= sy;
		
		for (int j = 0; j <= sy-1; j++) {
			for (int i = 0; i <= sx-1; i++) {
				p1 = g.getVertexList(i + j*sx);
				//horizontal edges
				if (i < sx-1) {
					p2 = g.getVertexList((i+1) + j*sx);
					g.addEdge(p1, p2, Vector2f.distance(p1, p2));
				}
				//vertical edges
				if (j < sy-1) {
					p2 = g.getVertexList(i + (j+1)*sx);
					g.addEdge(p1, p2, Vector2f.distance(p1, p2));
				}
				//topleft-bottomright edges
				if (i < sx-1 && j < sy-1) {
					p2 = g.getVertexList((i+1) + (j+1)*sx);
					g.addEdge(p1, p2, Vector2f.distance(p1, p2));
				}
				//bottomleft-topright edges
				if (i < sx-1 && j >= 1) {
					p2 = g.getVertexList((i+1) + (j-1)*sx);
					g.addEdge(p1, p2, Vector2f.distance(p1, p2));
				}
			}
		}
		//create navpoint objects for displaying this graph
		navpoints = new HashMap<WayPoint, NavPoint>();
		NavPoint np;
		for (WayPoint wp : g.getVertexList()) {
			np = new NavPoint(wp);
			navpoints.put(wp, np);
			Game.getGame().getLevel().spawn(np);
		}
		
		return g;
	}
	
	public static WayPointPath getPathNow(Vector2f start, Vector2f target) {
		return new WayPointPath(aStar(getNearest(start), getNearest(target), navmesh), false);
	}
	
	//guarentees to update path this cycle
	public static WayPointPath getPathNow(int key, Vector2f start, Vector2f target) {
		return new WayPointPath();
	}
	
	//adds this path to a queue to be updated at ASAP, might be a few cycles later
	public static WayPointPath getPath(int key, WayPoint start, WayPoint target) {
		return new WayPointPath();
	}
	
	//have to guarentee that pos is within half a spacing of the grid
	private static WayPoint getNearest(Vector2f pos) {
		WayPoint closest = null;
		float closestDistance = Float.POSITIVE_INFINITY;
		float distance;
		for (WayPoint wp : navmesh.getVertexList()) {
			if (wp.isObstructed) continue;
			distance = Vector2f.distance(pos, wp);
			if (distance < closestDistance) {
				closest = wp;
				closestDistance = distance;
			}
		}
		return closest;
	}
	
	//start and target must be exact instances of vertices on the given graph, not approximate locations
	public static ArrayList<WayPoint> aStar(WayPoint start, WayPoint target, Graph<WayPoint> g) {
		HashMap<WayPoint, WayPoint> parentMap = new HashMap<WayPoint, WayPoint>();
		HashMap<WayPoint, Float> gMap = new HashMap<WayPoint, Float>();
		HashMap<WayPoint, Float> hMap = new HashMap<WayPoint, Float>();
		HashMap<WayPoint, Float> fMap = new HashMap<WayPoint, Float>();
		//openSet is sorted based on lowest f-value.
		TreeSet<WayPoint> openSet = new TreeSet<WayPoint>(new Comparator<WayPoint>() {
			public int compare(WayPoint p1, WayPoint p2) {
				if (p1.equals(p2)) return 0;
				float difference = fMap.get(p1) - fMap.get(p2);
				if (difference < 0) return -1;
				else if (difference > 0) return 1;
				/*
				 * (else difference == 0)
				 * This shouldn't return 0 despite having equal priority because TreeSet.contains(), TreeSet.add(), and TreeSet.remove() use compare() to check if an 
				 * element is already in the list. So remove(A) would return true if A wasn't in the list but there was B which had equivalent value to A, which is 
				 * not what we want. We only want it to return true if that instance of A is in the list.
				 */
				else return -1; //prioritizes newer elements
			}
		});
		HashSet<WayPoint> closedSet = new HashSet<WayPoint>();
		for (WayPoint v : g.getVertexList()) {
			parentMap.put(v, null);
			hMap.put(v, Vector2f.distance(target, v)); //heuristic is l2 norm
			if (v.equals(start)) {
				gMap.put(v, 0f);
				fMap.put(v, gMap.get(v) + hMap.get(v)); //will simply be h + 0 for start
			} else {
				gMap.put(v, Float.POSITIVE_INFINITY);
				fMap.put(v, Float.POSITIVE_INFINITY);
			}
		}
		
		ArrayList<String> fileOutput = new ArrayList<String>();
		int iteration = 0;
		
		//the A* algorithm; before this is initialization
		WayPoint current;
		Float tentative_g;
		openSet.add(start);
		while (openSet.size() > 0) {
			current = openSet.pollFirst(); //retrieves and removes first element of openSet
			fileOutput.add("---------------- current node: " + current + " ----------------");
			if (current.equals(target)) {
				FileUtils.writeLog(Navigator.class, fileOutput);
				return path(current, parentMap);
			}
			closedSet.add(current);
			for (WayPoint neighbor : g.getAdjacentVertices(current)) {
				if (neighbor.isObstructed) continue; //ignore obstacles
				if (closedSet.contains(neighbor)) continue; //ignore closedSet neighbors
				//distance to from start to neighbor
				tentative_g = gMap.get(current) + (float)g.getDistanceBetween(current, neighbor);
				//"discover" a new WayPoint
				if (!openSet.contains(neighbor)) openSet.add(neighbor);
				//this is not a better path, move on
				else if (tentative_g >= gMap.get(neighbor)) continue;
				//this is the best path so far; update parent and g/f values
				openSet.remove(neighbor); //we must remove, update neighbor's f value, and reinsert in that order to keep openSet properly sorted
				parentMap.put(neighbor, current);
				gMap.put(neighbor, tentative_g);
				fMap.put(neighbor, gMap.get(neighbor) + hMap.get(neighbor));
				openSet.add(neighbor);
				{
					fileOutput.add("neighbor node: " + neighbor);
					fileOutput.add("open: " + openSet);
					fileOutput.add("closed: " + closedSet);
					fileOutput.add("gmap: " + gMap);
					fileOutput.add("hmap: " + hMap);
					fileOutput.add("fmap: " + fMap);
					fileOutput.add("parentmap: " + parentMap);
					fileOutput.add("");
				}
			}
		}
		return new ArrayList<WayPoint>();
	}
	
	public static ArrayList<WayPoint> path(WayPoint end, HashMap<WayPoint, WayPoint> parentMap) {
		if (parentMap.get(end) == null) {
			ArrayList<WayPoint> start = new ArrayList<WayPoint>();
			start.add(end);
			return start;
		} else {
			ArrayList<WayPoint> path = path(parentMap.get(end), parentMap);
			path.add(end);
			return path;
		}
	}
	
	private static void checkObstructions(Graph<WayPoint> g) {
		WayPoint wp;
		boolean obstructed = false;
		for (int i = 0; i < g.getVertexList().size(); i++) {
			wp = g.getVertexList(i);
			obstructed = false;
			for (Entity e : Game.getGame().getLevel().entities.getWalls()) {
				if (Physics.collision(wp, e)) {
					obstructed = true;
					break;
				}
			}
			if (Physics.collision(wp, Game.getGame().getLevel().entities.player())) {
//				obstructed = true;
			}
			wp.isObstructed = obstructed;
			navpoints.get(wp).setRender(!obstructed);
		}
	}
	
	private static WayPoint get(int i, int j, Graph<WayPoint> g) {
		return g.getVertexList(i + j*sx);
	}
	
	public static WayPoint get(int i, int j) {
		return navmesh.getVertexList(i + j*sx);
	}
}
