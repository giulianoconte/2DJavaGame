package input;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class Mouse extends GLFWMouseButtonCallback {
	
	public void invoke(long window, int button, int action, int mods) {
		byte b = 3;						//1 means button is pressed, 2 means button is down (held down or pressed), 3 means button is released or up
		if (action == GLFW_PRESS) b = 1;
		if (action == GLFW_REPEAT) b = 2;
		Input.set(button, b);
	}

}
