package ch.megil.teliaengine.input.converter;

import java.util.Map;

import ch.megil.teliaengine.input.VirtualController;

public interface KeyConverter {
	public Map<Integer, VirtualController> getKeyboard();
	public Map<Integer, VirtualController> getGamepadButtons();
	public Map<AxisInput, VirtualController> getGamepadAxes();
}
