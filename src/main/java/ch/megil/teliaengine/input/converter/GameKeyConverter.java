package ch.megil.teliaengine.input.converter;

import static org.lwjgl.glfw.GLFW.*;

import java.util.HashMap;
import java.util.Map;

import ch.megil.teliaengine.input.VirtualController;

public class GameKeyConverter implements KeyConverter {
	private Map<Integer, VirtualController> keyboad;
	private Map<Integer, VirtualController> gamepadButtons;
	private Map<AxisInput, VirtualController> gamepadAxes;
	
	public GameKeyConverter() {
		keyboad = new HashMap<>();
		keyboad.put(GLFW_KEY_D, VirtualController.WALK_RIGHT);
		keyboad.put(GLFW_KEY_RIGHT, VirtualController.WALK_RIGHT);
		keyboad.put(GLFW_KEY_A, VirtualController.WALK_LEFT);
		keyboad.put(GLFW_KEY_LEFT, VirtualController.WALK_LEFT);
		keyboad.put(GLFW_KEY_W, VirtualController.JUMP);
		keyboad.put(GLFW_KEY_UP, VirtualController.JUMP);
		
		gamepadButtons = new HashMap<>();
		gamepadButtons.put(GLFW_GAMEPAD_BUTTON_A, VirtualController.JUMP);
		
		gamepadAxes = new HashMap<>();
		gamepadAxes.put(new AxisInput(GLFW_GAMEPAD_AXIS_LEFT_X, -1f, -0.85f), VirtualController.WALK_LEFT);
		gamepadAxes.put(new AxisInput(GLFW_GAMEPAD_AXIS_LEFT_X, 0.85f, 1f), VirtualController.WALK_RIGHT);
	}

	@Override
	public Map<Integer, VirtualController> getKeyboad() {
		return keyboad;
	}

	@Override
	public Map<Integer, VirtualController> getGamepadButtons() {
		return gamepadButtons;
	}

	@Override
	public Map<AxisInput, VirtualController> getGamepadAxes() {
		return gamepadAxes;
	}
}
