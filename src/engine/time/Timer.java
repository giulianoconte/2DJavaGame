package engine.time;

import java.util.HashMap;
import java.util.Map;

/**
 * Timer is a class which handles Timer and Delay related functions.
 * 
 * 
 * Example Delay usage code:
 * 
 * //Delay initiation, 3 seconds from here the delay will be "over"
 * int grenade = Timer.createDelaySeconds(3f);
 * 
 * //in update() method, if it has been 3 seconds since the delay was created, explode
 * if (Timer.isOver(grenade)) {
 *   explode();
 * }
 * 
 * Another Delay example:
 * 
 * //Delay initiation, 1 second long
 * int secRepeater = Timer.createDelaySeconds(1f);
 * 
 * //in update() method, after 1 second print message and reset -- message repeats every second
 * if (Timer.isOver(secRepeater)) {
 *   Timer.reset(secRepeater);
 *   System.out.println("1 second passed!");
 * }
 * 
 * 
 * Example Timer usage code:
 * 
 * //Timer initiation
 * int playTime = Timer.create();
 * 
 * //in gameOver() method, if play time is 30 seconds or greater, score += 20
 * if (Timer.hasBeenSeconds(playTime, 30f) {
 *   score += 20;
 * } 
 * //else if play time is 20 seconds to 30 seconds, score += 40
 * else if (Timer.hasBeenSeconds(playTime, 20f) {
 *   score += 40;
 * }
 * //else if play time is 10 seconds to 20 seconds, score += 80
 * else if (Timer.hasBeenSeconds(playTime, 10f) {
 *   score += 80;
 * }
 */
public class Timer {
	
	private static int index = 0;
	private static Map<Integer, Long> timers = new HashMap<Integer, Long>();
	private static Map<Integer, Long> delays = new HashMap<Integer, Long>();
	
	private Timer() {}	//private constructor because Timer shouldn't be instantiated
	
	//returns time in nanoseconds since timer started
	public static long get(int key) {
		return current() - timers.get(key);
	}
	//returns time in seconds since timer started
	public static double getSeconds(int key) {
		return inSeconds(get(key));
	}
	
	//returns true iff it has been timeInNanos nanoseconds since timer started
	public static boolean hasBeen(int key, long timeInNanos) {
		return current() - timers.get(key) >= timeInNanos;
	}
	//returns true iff it has been timeInSeconds seconds since timer started
	public static boolean hasBeenSeconds(int key, double timeInSeconds) {
		return hasBeen(key, inNanos(timeInSeconds));
	}
	//returns true iff it has been the amount of time specified at delay creation since timer started
	public static boolean isOver(int key) {
		if (delays.get(key) == null) System.err.println("Tried to get info of a DELAY that hasn't been initialized!");
		return hasBeen(key, delays.get(key));
	}
	
	/*
	 * Returns integer key to new timer instance, this key must be passed
	 * into all other public methods to get the correct timer info
	 */
	public static int create() {
		if (index >= Integer.MAX_VALUE - 1) index = 0;
		timers.put(index, current());
		return index++;
	}
	
	public static int createDelay(long timeInNanos) {
		if (index >= Integer.MAX_VALUE - 1) index = 0;
		timers.put(index, current());
		delays.put(index, timeInNanos);
		return index++;
	}
	
	public static int createDelaySeconds(double timeInSeconds) {
		if (index >= Integer.MAX_VALUE - 1) index = 0;
		timers.put(index, current());
		delays.put(index, inNanos(timeInSeconds));
		return index++;
	}
	
	//returns time in nanoseconds since timer started
	public static long reset(int key) {
		long cur = current();
		long res = cur - timers.get(key);
		timers.put(key, cur);
		return res;
	}
	//returns time in seconds since timer started
	public static double resetSeconds(int key) {
		return inSeconds(reset(key));
	}
	
	//returns time in nanoseconds since timer started
	public static long end(int key) {
		long res = current() - timers.get(key);
		timers.put(key, null); //timers.remove(key); might be better, removes it from the map instead of setting it to null, although might have to do this for the delays list as well
		return res;
	}
	//returns time in seconds since timer started
	public static double endSeconds(int key) {
		return inSeconds(end(key));
	}
	
	private static long current() {
		return System.nanoTime();
	}
	
	private static double inSeconds(long timeInNanos) {
		return ((double)(timeInNanos))/1000000000;
	}
	
	private static long inNanos(double timeInSeconds) {
		return (long)(timeInSeconds*1000000000);
	}
	
	
	
	
	
	
	
	
	/////////////////////////////////////////////////////////
	//				OUTPUT								   //
	/////////////////////////////////////////////////////////
	
	private static String output = "";
	
	public static void p(String s) {
		System.out.println("" + s);
	}
	
	public static void p() {
		p("");
	}
	
	public static void pCon(String s) {
		System.out.print("" + s);
	}
	
	public static void addOutput(String s) {
		s = s.concat("\n");
		output = output.concat(s);
	}
	
	public static void addOutputCon(String s) {
		output = output.concat(s);
	}
	
	public static double round(double x, int decimalPlaces) {
		int multiple = 1;
		for (int i = 0; i < decimalPlaces; i++) multiple *= 10;
		return (double)Math.round(x * multiple) / multiple;
	}
	

	/////////////////////////////////////////////////////////
	//				DEBUGGING							   //
	/////////////////////////////////////////////////////////
	
	private static boolean debug = false;
	
	public static void db(String s) {
		if (debug)	p("\t\t\t\t\t" + s);
	}
}
