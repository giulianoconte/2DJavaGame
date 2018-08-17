package input;

import org.lwjgl.glfw.GLFWCursorPosCallback;

public class CursorPos extends GLFWCursorPosCallback {
	
	public void invoke(long window, double xpos, double ypos) {
		Input.setCursorPos(xpos, ypos);
	}

}
