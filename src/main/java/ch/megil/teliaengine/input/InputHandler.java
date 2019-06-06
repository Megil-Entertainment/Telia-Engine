package ch.megil.teliaengine.input;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetGamepadState;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.glfw.GLFWGamepadState;

import ch.megil.teliaengine.input.converter.KeyConverter;

public class InputHandler {
	private EnumSet<VirtualController> startInput;
	private EnumSet<VirtualController> endInput;
	private EnumSet<VirtualController> keyboardPressed;
	private EnumSet<VirtualController> gamepadPressed;
	
	private KeyConverter keyConverter;
	
	public InputHandler(KeyConverter keyConverter) {
		this.keyConverter = keyConverter;
		
		startInput = EnumSet.noneOf(VirtualController.class);
		endInput = EnumSet.noneOf(VirtualController.class);
		keyboardPressed = EnumSet.noneOf(VirtualController.class);
		gamepadPressed = EnumSet.noneOf(VirtualController.class);
	}
	
	/**
	 * @return 2 Sets of VirtualController inputs since last call,
	 * 				<b>0</b> are started inputs and <b>1</b> are ended inputs
	 */
	public List<Set<VirtualController>> getInputs() {
		var ret = new ArrayList<Set<VirtualController>>(2);
		
		synchronized (this) {
			ret.add(startInput.clone());
			startInput.clear();
			ret.add(endInput.clone());
			endInput.clear();
		}
		
		return ret;
	}
	
	private void startInput(VirtualController input, EnumSet<VirtualController> pressAdd) {
		synchronized (this) {
			if (!keyboardPressed.contains(input) && !gamepadPressed.contains(input)) {
				startInput.add(input);
				pressAdd.add(input);
			}
		}
	}
	
	private void endInput(VirtualController input, EnumSet<VirtualController> pressCheck) {
		synchronized (this) {
			if (pressCheck.contains(input)) {
				endInput.add(input);
				pressCheck.remove(input);
			}
		}
	}
	
	private void updateGamepadAxes(FloatBuffer axesState) {
		var axesMapping = keyConverter.getGamepadAxes();
		
		for (var axis : axesMapping.entrySet()) {
			if (axis.getKey().isInThreshold(axesState.get(axis.getKey().getGlfwGamepadAxis()))) {
				startInput(axis.getValue(), gamepadPressed);
			} else {
				endInput(axis.getValue(), gamepadPressed);
			}
		}
	}
	
	private void updateGamepadButtons(ByteBuffer buttonsState) {
		var buttonsMapping = keyConverter.getGamepadButtons();
		
		for (var button : buttonsMapping.entrySet()) {
			if (buttonsState.get(button.getKey()) == GLFW_PRESS) {
				startInput(button.getValue(), gamepadPressed);
			} else {
				endInput(button.getValue(), gamepadPressed);
			}
		}
	}
	
	public void updateGamepad() {
		var state = GLFWGamepadState.calloc();
		try {
			if (glfwGetGamepadState(GLFW_JOYSTICK_1, state)) {
				updateGamepadAxes(state.axes());
				updateGamepadButtons(state.buttons());
			}
		} finally {
			state.free();
		}
	}
	
	public void registerKeyAction(int glfwKey, int action) {
		var key = keyConverter.getKeyboard().getOrDefault(glfwKey, VirtualController.NONE);
		if (action == GLFW_PRESS) {
			startInput(key, keyboardPressed);
		} else if (action == GLFW_RELEASE) {
			endInput(key, keyboardPressed);
		}
	}
}
