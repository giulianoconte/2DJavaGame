package math;

public class Linear {
	
	//maps x in range(a, b) to y in range(c, d)
	public static float map(float x, float a, float b, float c, float d) {
		try {
			if (a == b) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.err.println("Tried to map " + x + " but " + a + " == " + b + "; division by 0!");
			e.printStackTrace();
			b = a + Float.MIN_VALUE;
		}
		float y = (x-a)/(b-a) * (d-c) + c;
		return y;
	}
}
