package ch.megil.teliaengine.physics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VectorTest {
	
	@Test
	public void testVector() {
		var vec = new Vector(5, 10);
		
		assertEquals(5, vec.getX(), 0);
		assertEquals(10, vec.getY(), 0);	
	}
	
	@Test
	public void testNegate() {
		var vec = new Vector(5, 10);
		
		var res = vec.negate();
		assertEquals(res.getX(), -5, 0);
		assertEquals(res.getY(), -10, 0);
	}
	
	@Test
	public void testAdd() {
		var vec = new Vector(5, 10);
		var vec2 = new Vector(3, 15);
		
		var res = vec.add(vec2);
		assertEquals(8, res.getX(), 0);
		assertEquals(25, res.getY(), 0);
	}
	
	@Test
	public void testSubtract() {
		var vec = new Vector(5, 10);
		var vec2 = new Vector(3, 15);
		
		var res = vec.subtract(vec2);
		assertEquals(2, res.getX(), 0);
		assertEquals(-5, res.getY(), 0);
	}
	
	@Test
	public void testDot() {
		var vec = new Vector(5, 10);
		var vec2 = new Vector(3, 15);
		var vec3 = new Vector(-10, 5);
		
		var res = vec.dot(vec2);
		assertEquals(165, res, 0);
		
		res = vec2.dot(vec);
		assertEquals(165, res, 0);

		res = vec.dot(vec);
		assertEquals(125, res, 0);
		
		res = vec.dot(vec3);
		assertEquals(0, res, 0);
	}
	
	@Test
	public void testPerpendicularDot() {
		var vec = new Vector(5, 10);
		var vec2 = new Vector(3, 15);
		var vec3 = new Vector(-10, 5);
		
		var res = vec.perpendicularDot(vec2);
		assertEquals(45, res, 0);
		
		res = vec2.perpendicularDot(vec);
		assertEquals(-45, res, 0);

		res = vec.perpendicularDot(vec);
		assertEquals(0, res, 0);
		
		res = vec.perpendicularDot(vec3);
		assertEquals(125, res, 0);
	}
	
	@Test
	public void testMultiplyByComponent() {
		var vec = new Vector(5, 10);
		var vec2 = new Vector(3, -15);
		
		var res = vec.multiplyByComponent(vec2);
		assertEquals(15, res.getX(), 0);
		assertEquals(-150, res.getY(), 0);
	}
	
	@Test
	public void testSquareLength() {
		var vec = new Vector(5, 10);
		var vec2 = new Vector(3, -15);
		
		var res = vec.squareLength();
		assertEquals(125, res, 0);
		
		res = vec2.squareLength();
		assertEquals(234, res, 0);
	}
	
	@Test
	public void testSquareDistanceToLineSegment() {
		var lineEndA = new Vector(-3, 1);
		var lineEndB = new Vector(4, 4);
		
		var point0 = new Vector(-4, -2);
		var point1 = new Vector(-1, 5);
		var point2 = new Vector(5, 6);
		
		var res = point0.squareDistanceToLineSegment(lineEndA, lineEndB);
		assertEquals(10, res, 0);
		
		res = point1.squareDistanceToLineSegment(lineEndA, lineEndB);
		assertEquals(8.344827586, res, 0.000000001);
		
		res = point2.squareDistanceToLineSegment(lineEndA, lineEndB);
		assertEquals(5, res, 0);
	}
	
	@Test
	public void testClamp() {
		var min = new Vector(-5, -2);
		var max = new Vector(4, 4);
		
		var point0 = new Vector(-6, -4);
		var point1 = new Vector(-1, 1);
		var point2 = new Vector(1, -4);
		var point3 = new Vector(5, 3);
		var point4 = new Vector(4, 7);
		
		var res = point0.clamp(min, max);
		assertEquals(-5, res.getX(), 0);
		assertEquals(-2, res.getY(), 0);
		
		res = point1.clamp(min, max);
		assertEquals(-1, res.getX(), 0);
		assertEquals(1, res.getY(), 0);
		
		res = point2.clamp(min, max);
		assertEquals(1, res.getX(), 0);
		assertEquals(-2, res.getY(), 0);
		
		res = point3.clamp(min, max);
		assertEquals(4, res.getX(), 0);
		assertEquals(3, res.getY(), 0);
		
		res = point4.clamp(min, max);
		assertEquals(4, res.getX(), 0);
		assertEquals(4, res.getY(), 0);
	}
	
	@Test
	public void testXandYVector() {
		var vecX = new Vector(5, 10);
		var vecY = new Vector(5, 10);
		
		var res = vecX.xVector();
		assertEquals(5, res.getX(), 0);
		assertEquals(0, res.getY(), 0);
		
		res = vecY.yVector();
		assertEquals(0, res.getX(), 0);
		assertEquals(10, res.getY(), 0);
	}
	
	@Test
	public void testSplitToComponentSizeOne() {
		var vecX = new Vector(28, 13);
		var vecY = new Vector(13, 28);
		var vec0 = new Vector(0, 0);
		
		var res = vecX.splitToComponentSizeOne();
		assertFalse(res.isEmpty());
		for(var v : res) {
			assertEquals(1, v.getX(), 0);
			assertEquals(0.46428571428, v.getY(), 0.000001);
		}
		
		res = vecY.splitToComponentSizeOne();
		assertFalse(res.isEmpty());
		for(var v : res) {
			assertEquals(0.46428571428, v.getX(), 0.000001);
			assertEquals(1, v.getY(), 0);
		}
		
		res = vec0.splitToComponentSizeOne();
		assertEquals(1, res.size());
		assertEquals(0, res.get(0).getX(), 0);
		assertEquals(0, res.get(0).getY(), 0);
	}
	
	@Test
	public void testEquals() {
		var vec0 = new Vector(5, -8);
		var vec1 = new Vector(5, -8);
		var vec2 = new Vector(5, 3);
		
		assertTrue("Equals same object", vec0.equals(vec0));
		assertTrue("Equals different object", vec0.equals(vec1));
		assertFalse("Not equals", vec0.equals(vec2));
	}
}
