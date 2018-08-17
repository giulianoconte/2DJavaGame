package engine.navigation;

import java.util.ArrayList;

public class WayPointPath {
	private ArrayList<WayPoint> path;
	private int it; //an iterator implementation
	private int size;
	private int endIndex;
	private boolean isLoop;
	
	public float radius;
	
	public WayPointPath(ArrayList<WayPoint> path_, boolean isLoop_) {
		path = path_;
		it = -1;
		size = path.size();
		updateEndIndex();
		isLoop = isLoop_;
		radius = 1f;
	}
	
	public WayPointPath(ArrayList<WayPoint> path_) {
		this(path_, false);
	}
	
	public WayPointPath(boolean isLoop_) {
		this(new ArrayList<WayPoint>(), isLoop_);
	}
	
	public WayPointPath() {
		this(new ArrayList<WayPoint>(), false);
	}
	
	private void incrementSize() {
		size++;
		updateEndIndex();
	}
	private void decrementSize() {
		size--;
		updateEndIndex();
	}
	private void updateEndIndex() {
		endIndex = size - 1;
	}
	//shouldn't use this unless debugging
	public ArrayList<WayPoint> getPath() {
		return path;
	}
	public void resetIterator() {
		it = -1;
	}
	public int getIterator() {
		return it;
	}
	public int size() {
		return size;
	}
	public void setLoop(boolean b) {
		isLoop = b;
	}
	public boolean getLoop() {
		return isLoop;
	}
	
	public boolean exists() {
		return (size > 0);
	}
	public boolean isStarted() {
		return (it > -1);
	}
	public boolean hasNext() {
		return (it < endIndex);
	}
	public boolean isEnd() {
		return isLoop ? false : it == endIndex;
	}
	public boolean isFinished() {
		return isLoop ? false : it > endIndex;
	}
	public WayPoint current() {
		return path.get(it);
	}
	public WayPoint next() {
		if (!hasNext() && isLoop) return path.get(it = 0);
		else return path.get(++it); //will, throw NullPointerException if the list has been exhausted; need to check for this with hasNext() before calling next()!
	}
	public void increment() {
		if (!hasNext() && isLoop) it = 0;
		else ++it;
	}
	public void remove() {
		path.remove(it);
		decrementSize();
	}
	public int hasLeft() {
		if (isLoop) return path.size();
		else return path.size()-1 - it;
	}
	public WayPoint getAhead(int index) {
		if (!hasNext() && isLoop) return path.get((it+index) % path.size());
		else return path.get(it+index); //will, throw NullPointerException if the list has been exhausted; need to check for this with hasNext() before calling next()!
	}
	public void add(WayPoint wp) {
		path.add(wp);
		if (it >= size) it++;
		incrementSize();
	}
	public void insert(int index, WayPoint wp) {
		path.add(index, wp);
		if (it >= index) it++;
		incrementSize();
	}
	
	public String toString() {
		String res = "";
		for (WayPoint wp : path) {
			res += wp.toString();
		}
		return res;
	}
}