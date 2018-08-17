package engine;

import static utils.OutputUtils.*;

import game.Entity;
import game.Game;
import game.GenericEntity;
import game.Level;
import game.PlayerMob;
import game.RedDot;
import game.Zombie;
import math.Vector2f;

public class Physics {
	
	public static boolean collision(Entity a, Entity b) {
		//get centers and rotations of rectangles
		float ax = a.getPos().x;
		float ay = a.getPos().y;
		float arot = a.getRot();
		float bx = b.getPos().x;
		float by = b.getPos().y;
		float brot = b.getRot();
		//get min and max x and y values of a
		float ax0 = -a.getSize().x / 2.0f;
		float ax1 =  a.getSize().x / 2.0f;
		float ay0 = -a.getSize().y / 2.0f;
		float ay1 =  a.getSize().y / 2.0f;
		//get min and max x and y values of b
		float bx0 = -b.getSize().x / 2.0f;
		float bx1 =  b.getSize().x / 2.0f;
		float by0 = -b.getSize().y / 2.0f;
		float by1 =  b.getSize().y / 2.0f;
		//find vertices of rectangle on entity coordinates
		Vector2f aur = new Vector2f(ax1, ay1); //upper right
		Vector2f aul = new Vector2f(ax0, ay1); //upper left
		Vector2f all = new Vector2f(ax0, ay0); //lower left
		Vector2f alr = new Vector2f(ax1, ay0); //lower right
		//find vertices of rectangle on entity coordinates
		Vector2f bur = new Vector2f(bx1, by1); //upper right
		Vector2f bul = new Vector2f(bx0, by1); //upper left
		Vector2f bll = new Vector2f(bx0, by0); //lower left
		Vector2f blr = new Vector2f(bx1, by0); //lower right
		//rotate vertices to correct rotation coordinates
		if (b instanceof PlayerMob) {
//			p(brot);
		}
//		aur = Vector2f.rotate(aur, arot); bur = Vector2f.rotate(bur,  brot);
//		aul = Vector2f.rotate(aul, arot); bul = Vector2f.rotate(bul,  brot);
//		all = Vector2f.rotate(all, arot); bll = Vector2f.rotate(bll,  brot);
//		alr = Vector2f.rotate(alr, arot); blr = Vector2f.rotate(blr,  brot);
		aur.rotate(arot); bur.rotate(brot);
		aul.rotate(arot); bul.rotate(brot);
		all.rotate(arot); bll.rotate(brot);
		alr.rotate(arot); blr.rotate(brot);
		//translate vertices to world coordinates
		aur.add(ax, ay); bur.add(bx, by);
		aul.add(ax, ay); bul.add(bx, by);
		all.add(ax, ay); bll.add(bx, by);
		alr.add(ax, ay); blr.add(bx, by);

//		bul = new Vector2f(bur);
//		p("bur: " + bur);
//		p("bul: " + bul);
//		bur = Vector2f.rotate(bur, brot);
//		bul.rotate(brot);
//		p("bur after: " + bur);
//		p("bul after: " + bul);
		
		//find the four axes which represent the normals of the two rectangles
		Vector2f axes[] = new Vector2f[4];
		axes[0] = Vector2f.sub(aur, aul);
		axes[1] = Vector2f.sub(aur, alr);
		axes[2] = Vector2f.sub(bur, bul);
		axes[3] = Vector2f.sub(bur, blr);
		
		Vector2f[] recta = {aur, aul, all, alr};
		Vector2f[] rectb = {bur, bul, bll, blr};
		
//		if (a instanceof Zombie) {
//			for (Vector2f v : recta) {
//				Game.getGame().getLevel().spawn(new RedDot(v));
//			}
//			for (Vector2f v : rectb) {
//				Game.getGame().getLevel().spawn(new RedDot(v));
//			}
//		}
		
		Vector2f[] proja = new Vector2f[axes.length]; //vectors representing min and max scalars for the projected points of each rectangle to each axis
		Vector2f[] projb = new Vector2f[axes.length]; //proj[i].x is the min scalar, proj[i].y is the max scalar. this is only used for sorting and detecting overlaps -- see <https://www.gamedev.net/resources/_/technical/game-programming/2d-rotated-rectangle-collision-r2604>
		for (int i = 0; i < axes.length; i++) {
			proja[i] = projectRect(recta, axes[i]);
			projb[i] = projectRect(rectb, axes[i]);
			if (!(projb[i].x <= proja[i].y && projb[i].y >= proja[i].x)) return false; //at least one scalar doesn't overlap -> no collision
		}
		return true; //all projections overlap -> collision
	}
	
	public static boolean collision(Vector2f p, Entity a) {
		Entity e = new GenericEntity(p, 0.0f);
		return collision(a, e);
	}
	/*
	 * Helper for collision()
	 * @param rect
	 * 		must be 4 points in clockwise or counter-clockwise order
	 * projection of p onto axis = ((p.x * axis.x + p.y * axis.y) / (axis.x^2 + axis.y^2) * axis.x, (p.x * axis.x + p.y * axis.y) / (axis.x^2 + axis.y^2) * axis.y)
	 * or simply (scale*axis.x, scale*axis.y) -- see <https://www.gamedev.net/resources/_/technical/game-programming/2d-rotated-rectangle-collision-r2604>
	 */
	private static Vector2f projectRect(Vector2f[] rect, Vector2f axis) {
		float projScale;
		Vector2f[] proj = new Vector2f[rect.length];
		for (int i = 0; i < rect.length; i++) {
			projScale = (rect[i].x * axis.x + rect[i].y * axis.y) / 
					((float)Math.pow(axis.x,  2) + (float)Math.pow(axis.y, 2));
			proj[i] = new Vector2f(projScale*axis.x, projScale*axis.y);
		}
		int minIndex = 0;
		int maxIndex = 0;
		for (int i = 0; i < proj.length; i++) {
			if (axis.dot(proj[i]) < axis.dot(proj[minIndex])) minIndex = i;
			if (axis.dot(proj[i]) > axis.dot(proj[maxIndex])) maxIndex = i;
		}
		return new Vector2f(axis.dot(proj[minIndex]), axis.dot(proj[maxIndex]));
	}
	
	public static boolean pointOnSegment(Vector2f p, Vector2f a, Vector2f b) {
		if (p.x < (float)Math.min(a.x, b.x) || p.x > (float)Math.max(a.x, b.x) ||
			p.y < (float)Math.min(a.y, b.y) || p.y > (float)Math.max(a.y, b.y)) {
			return false;
		} else {
			return true;
		}
	}
}
