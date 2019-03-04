package ch.megil.teliaengine.input;

import static org.junit.Assert.assertEquals;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import ch.megil.teliaengine.input.converter.KeyConverter;

public class KeyHandlerTest {
	private int keyRight;
	private int keyLeft;
	private int keyNone;
	
	private KeyConverter converter;

	@Before
	public void setUp() throws Exception {
		keyRight = GLFW_KEY_RIGHT;
		keyLeft = GLFW_KEY_LEFT;
		keyNone = GLFW_KEY_ENTER;
		
		var keyMap = new HashMap<Integer, VirtualController>();
		keyMap.put(keyRight, VirtualController.WALK_RIGHT);
		keyMap.put(keyLeft, VirtualController.WALK_LEFT);
		
		converter = mock(KeyConverter.class);
		when(converter.getKeyboad()).thenReturn(keyMap);
	}

	@Test
	public void testPressCycle() {
		var handler = new InputHandler(converter);
		
		handler.registerKeyAction(keyLeft, GLFW_PRESS);

		var strokes = handler.getInputs();
		assertEquals(EnumSet.of(VirtualController.WALK_LEFT), strokes.get(0));
		assertEquals(EnumSet.noneOf(VirtualController.class), strokes.get(1));
		
		handler.registerKeyAction(keyLeft, GLFW_PRESS);

		strokes = handler.getInputs();
		assertEquals(EnumSet.noneOf(VirtualController.class), strokes.get(0));
		assertEquals(EnumSet.noneOf(VirtualController.class), strokes.get(1));
		
		handler.registerKeyAction(keyLeft, GLFW_RELEASE);

		strokes = handler.getInputs();
		assertEquals(EnumSet.noneOf(VirtualController.class), strokes.get(0));
		assertEquals(EnumSet.of(VirtualController.WALK_LEFT), strokes.get(1));
	}
	
	@Test
	public void testMultipleGetRequests() {
		var handler = new InputHandler(converter);
		
		handler.registerKeyAction(keyLeft, GLFW_PRESS);

		assertEquals(EnumSet.of(VirtualController.WALK_LEFT), handler.getInputs().get(0));
		assertEquals(EnumSet.noneOf(VirtualController.class), handler.getInputs().get(0));
		
		handler.registerKeyAction(keyLeft, GLFW_RELEASE);
		handler.registerKeyAction(keyLeft, GLFW_PRESS);
		handler.registerKeyAction(keyRight, GLFW_PRESS);

		assertEquals(EnumSet.of(VirtualController.WALK_LEFT, VirtualController.WALK_RIGHT), handler.getInputs().get(0));
		assertEquals(EnumSet.noneOf(VirtualController.class), handler.getInputs().get(0));
	}

	@Test
	public void testPressReleaseBeforeGet() {
		var handler = new InputHandler(converter);
		
		handler.registerKeyAction(keyLeft, GLFW_PRESS);
		handler.registerKeyAction(keyLeft, GLFW_RELEASE);

		var strokes = handler.getInputs();
		assertEquals(EnumSet.of(VirtualController.WALK_LEFT), strokes.get(0));
		assertEquals(EnumSet.of(VirtualController.WALK_LEFT), strokes.get(1));
		
		handler.registerKeyAction(keyLeft, GLFW_PRESS);
		
		strokes = handler.getInputs();
		assertEquals(EnumSet.of(VirtualController.WALK_LEFT), strokes.get(0));
	}
	
	@Test
	public void testNonExistantKeyPress() {
		var handler = new InputHandler(converter);
		
		handler.registerKeyAction(keyNone, GLFW_PRESS);
		handler.registerKeyAction(keyNone, GLFW_RELEASE);
		handler.registerKeyAction(keyNone, GLFW_PRESS);
		handler.registerKeyAction(keyNone, GLFW_RELEASE);
		
		var strokes = handler.getInputs();
		assertEquals(EnumSet.of(VirtualController.NONE), strokes.get(0));
		assertEquals(EnumSet.of(VirtualController.NONE), strokes.get(1));
	}
}
