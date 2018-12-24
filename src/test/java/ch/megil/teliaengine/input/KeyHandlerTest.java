package ch.megil.teliaengine.input;

import static org.junit.Assert.assertEquals;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;

public class KeyHandlerTest {
	private int keyRight;
	private int keyLeft;

	@Before
	public void setUp() throws Exception {
		keyRight = GLFW_KEY_RIGHT;
		keyLeft = GLFW_KEY_LEFT;
	}

	@Test
	public void testPressCycle() {
		var handler = new KeyHandler();
		
		handler.press(keyLeft);

		var strokes = handler.getKeyStrokes();
		assertEquals(EnumSet.of(VirtualController.WALK_LEFT), strokes.get(0));
		assertEquals(EnumSet.noneOf(VirtualController.class), strokes.get(1));
		
		handler.press(keyLeft);

		strokes = handler.getKeyStrokes();
		assertEquals(EnumSet.noneOf(VirtualController.class), strokes.get(0));
		assertEquals(EnumSet.noneOf(VirtualController.class), strokes.get(1));
		
		handler.release(keyLeft);

		strokes = handler.getKeyStrokes();
		assertEquals(EnumSet.noneOf(VirtualController.class), strokes.get(0));
		assertEquals(EnumSet.of(VirtualController.WALK_LEFT), strokes.get(1));
	}
	
	@Test
	public void testMultipleGetRequests() {
		var handler = new KeyHandler();
		
		handler.press(keyLeft);

		assertEquals(EnumSet.of(VirtualController.WALK_LEFT), handler.getKeyStrokes().get(0));
		assertEquals(EnumSet.noneOf(VirtualController.class), handler.getKeyStrokes().get(0));
		
		handler.release(keyLeft);
		handler.press(keyLeft);
		handler.press(keyRight);

		assertEquals(EnumSet.of(VirtualController.WALK_LEFT, VirtualController.WALK_RIGHT), handler.getKeyStrokes().get(0));
		assertEquals(EnumSet.noneOf(VirtualController.class), handler.getKeyStrokes().get(0));
	}

	@Test
	public void testPressReleaseBeforeGet() {
		var handler = new KeyHandler();
		
		handler.press(keyLeft);
		handler.release(keyLeft);

		var strokes = handler.getKeyStrokes();
		assertEquals(EnumSet.of(VirtualController.WALK_LEFT), strokes.get(0));
		assertEquals(EnumSet.of(VirtualController.WALK_LEFT), strokes.get(1));
		
		handler.press(keyLeft);
		
		strokes = handler.getKeyStrokes();
		assertEquals(EnumSet.of(VirtualController.WALK_LEFT), strokes.get(0));
	}

}
