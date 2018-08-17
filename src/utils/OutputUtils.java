package utils;

public class OutputUtils {
	
	/////////////////////////////////////////////////////////
	//				OUTPUT								   //
	/////////////////////////////////////////////////////////
	
	private static String output = "";
	
	public static void p(Object o) {
		p(o.toString());
	}
	
	public static void p(String s) {
		System.out.println("" + s);
	}
	
	public static void p(int s) {
		p("" + s);
	}
	
	public static void p(double s) {
		p("" + s);
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
	
	public static boolean debug = false;
	
	public static void setDebug(boolean b) {
		debug = b;
	}
	
	public static void db(String s) {
		if (debug) p("\t\t\t\t\t\t\t\t" + s);
	}

}
