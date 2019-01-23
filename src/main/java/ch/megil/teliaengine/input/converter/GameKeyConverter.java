package ch.megil.teliaengine.input.converter;

import java.util.HashMap;
import java.util.Map;

import ch.megil.teliaengine.input.VirtualController;

public class GameKeyConverter implements KeyConverter {
	private Map<Integer, VirtualController> keyboad;
	private Map<Integer, VirtualController> gamepadButtons;
	private Map<Integer, AxisInput> gamepadAxes;
	
	public GameKeyConverter() {
		keyboad = new HashMap<>();
		
		gamepadButtons = new HashMap<>();
		
		gamepadAxes = new HashMap<>();
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
	public Map<Integer, AxisInput> getGamepadAxes() {
		return gamepadAxes;
	}
}
