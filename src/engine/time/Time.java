package engine.time;

import static utils.OutputUtils.*;

public class Time {
	
	private static long lastTime;
	private static long currentTime;
	private static double difference;
	
	public static long current() {
		return System.nanoTime();
	}
	
	public static double inSeconds(long timeInNanos) {
		return ((double)(timeInNanos))/1000000000;
	}
	
	public static long inNanos(double timeInSeconds) {
		return (long)(timeInSeconds*1000000000);
	}
	
	public static void start() {
		lastTime = current();
	}
	
	public static double measure() {
		currentTime = current();
		difference = inSeconds(currentTime - lastTime);
		return difference;
	}
	
	public static double measureSet() {
		measure();
		start();
		return difference;
	}

}
