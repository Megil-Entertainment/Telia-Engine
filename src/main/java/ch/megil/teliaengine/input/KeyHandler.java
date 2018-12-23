package ch.megil.teliaengine.input;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class KeyHandler {
	private EnumSet<VirtualController> pressed;
	private EnumSet<VirtualController> repeat;
	private EnumSet<VirtualController> released;
	
	public KeyHandler() {
		pressed = EnumSet.noneOf(VirtualController.class);
		repeat = EnumSet.noneOf(VirtualController.class);
		released = EnumSet.noneOf(VirtualController.class);
	}
	
	/**
	 * @return 2 Sets of VirtualController inputs since last call,
	 * 				<b>0</b> are pressed and <b>1</b> are released
	 */
	public List<Set<VirtualController>> getKeyStrokes() {
		var ret = new ArrayList<Set<VirtualController>>(2);
		
		synchronized (this) {
			ret.add(getPressed());
			ret.add(getReleased());
		}
		
		return ret;
	}
	
	private Set<VirtualController> getPressed() {
		var ret = pressed.clone();
		repeat.addAll(pressed);
		repeat.removeAll(released);
		pressed.clear();
		return ret;
	}
	
	private Set<VirtualController> getReleased() {
		var ret = released.clone();
		released.clear();
		return ret;
	}
	
	public void press(int glfwKey) {
		var key = convert(glfwKey);
		synchronized (this) {
			if (!repeat.contains(key)) {
				pressed.add(key);
			}
		}
	}
	
	public void release(int glfwKey) {
		var key = convert(glfwKey);
		synchronized (this) {
			repeat.remove(key);
			released.add(key);
		}
	}
	
	private VirtualController convert(int glfwKey) {
		var vCode = VirtualController.NONE;
		switch (glfwKey) {
			case GLFW_KEY_D:
			case GLFW_KEY_RIGHT:
				vCode = VirtualController.WALK_RIGHT;
				break;
			case GLFW_KEY_A:
			case GLFW_KEY_LEFT:
				vCode = VirtualController.WALK_LEFT;
				break;
			case GLFW_KEY_W:
			case GLFW_KEY_UP:
				vCode = VirtualController.JUMP;
				break;
			default:
				break;
		}
		return vCode;
	}
}
