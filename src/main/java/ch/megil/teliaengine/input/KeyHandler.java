package ch.megil.teliaengine.input;

import java.util.EnumSet;
import java.util.Set;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyHandler {
	private EnumSet<VirtualController> pressed;
	private EnumSet<VirtualController> repeat;
	private EnumSet<VirtualController> released;
	
	public KeyHandler() {
		pressed = EnumSet.noneOf(VirtualController.class);
		repeat = EnumSet.noneOf(VirtualController.class);
		released = EnumSet.noneOf(VirtualController.class);
	}
	
	public Set<VirtualController> getPressed() {
		synchronized (this) {
			var ret = pressed.clone();
			repeat.addAll(pressed);
			pressed.clear();
			return ret;
		}
	}
	
	public Set<VirtualController> getReleased() {
		synchronized (this) {
			var ret = released.clone();
			released.clear();
			return ret;
		}
	}
	
	public void press(KeyEvent e) {
		var key = convert(e.getCode());
		synchronized (this) {
			if (!repeat.contains(key)) {
				pressed.add(key);
			}
		}
	}
	
	public void release(KeyEvent e) {
		var key = convert(e.getCode());
		synchronized (this) {
			repeat.remove(key);
			released.add(key);
		}
	}
	
	private VirtualController convert(KeyCode code) {
		var vCode = VirtualController.NONE;
		switch (code) {
			case D:
			case RIGHT:
				vCode = VirtualController.WALK_RIGHT;
				break;
			case A:
			case LEFT:
				vCode = VirtualController.WALK_LEFT;
				break;
			default:
				break;
		}
		return vCode;
	}
}
