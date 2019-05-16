package ch.megil.teliaengine.game;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ch.megil.teliaengine.physics.Vector;

public class VectorTest {
	
	@Test
	public void testVector() {
		var vec = new Vector(5, 10);
		
		assertEquals(5, vec.getX(), 0);
		assertEquals(10, vec.getY(), 0);
		
	}
	
	@Test
	public void testNegateVector() {
		var vec = new Vector(5, 10);
		
		assertEquals(5, vec.getX(), 0);
		assertEquals(10, vec.getY(), 0);
		vec = vec.negate();
		assertEquals(vec.getX(), -5, 0);
		assertEquals(vec.getY(), -10, 0);
	}
	
	@Test
	public void testAddVector() {
		var vec = new Vector(5, 10);
		var vec2 = new Vector(3, 15);
		
		assertEquals(5, vec.getX(), 0);
		assertEquals(10, vec.getY(), 0);
		assertEquals(3, vec2.getX(), 0);
		assertEquals(15, vec2.getY(), 0);
		vec = vec.add(vec2);
		assertEquals(8, vec.getX(), 0);
		assertEquals(25, vec.getY(), 0);
	}
	
	@Test
	public void testSplitToComponentSizeOne() {
		var vecX = new Vector(28, 13);
		var vecY = new Vector(13, 28);
		List<Vector> vectorListX = new ArrayList<>();
		List<Vector> vectorListY = new ArrayList<>();
		
		vectorListX = vecX.splitToComponentSizeOne();
		vectorListY = vecY.splitToComponentSizeOne();
		
		for(Vector v: vectorListX) {
			assertEquals(1, v.getX(), 0);
			assertEquals(0.46428571428, v.getY(), 0.000001);
		}
		
		for(Vector v: vectorListY) {
			assertEquals(0.46428571428, v.getX(), 0.000001);
			assertEquals(1, v.getY(), 0);
		}
	}
	
	@Test
	public void testRoundVector() {
		var vec = new Vector(12.99, 8.49);
		
		assertEquals(12.99, vec.getX(), 0);
		assertEquals(8.49, vec.getY(), 0);
		vec = vec.round();
		assertEquals(13, vec.getX(), 0);
		assertEquals(8, vec.getY(), 0);
	}
	
	@Test
	public void testXandYVector() {
		var vecX = new Vector(5, 10);
		var vecY = new Vector(5, 10);
		
		assertEquals(5, vecX.getX(), 0);
		assertEquals(10, vecX.getY(), 0);
		assertEquals(5, vecY.getX(), 0);
		assertEquals(10, vecY.getY(), 0);
		vecX = vecX.xVector();
		vecY = vecY.yVector();
		assertEquals(5, vecX.getX(), 0);
		assertEquals(0, vecX.getY(), 0);
		assertEquals(0, vecY.getX(), 0);
		assertEquals(10, vecY.getY(), 0);
	}
}
