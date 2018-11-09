package ch.megil.teliaengine.game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VectorTest {
	
	@Test
	public void testVector() {
		var vec = new Vector(5, 10);
		
		assertEquals(vec.getX(), 5, 0);
		assertEquals(vec.getY(), 10, 0);
	}
	
	@Test
	public void testNegateVector() {
		var vec = new Vector(5, 10);
	}
}
