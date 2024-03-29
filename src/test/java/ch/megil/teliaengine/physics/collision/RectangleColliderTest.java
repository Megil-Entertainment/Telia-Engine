package ch.megil.teliaengine.physics.collision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.megil.teliaengine.physics.Vector;

public class RectangleColliderTest {

	@Test
	public void testRectangleCollider() {
		var collider = new RectangleCollider(new Vector(1, 2), 3, 4);
		assertEquals("Bounding Box begin", new Vector(1, 2), collider.getBoundingBoxBegin());
		assertEquals("Bounding Box end", new Vector(4, 6), collider.getBoundingBoxEnd());
	}

	@Test
	public void testCheckCollisionAgainstRectangle() {
		var recCollider0 = new RectangleCollider(new Vector(3, 3), 5, 5);
		var recCollider1 = new RectangleCollider(new Vector(5, 5), 5, 5);
		var recCollider2 = new RectangleCollider(new Vector(20, 20), 5, 5);
		var recCollider3 = new RectangleCollider(new Vector(3, 8), 5, 5);
		var recCollider4 = new RectangleCollider(new Vector(3, 3), 5, 5);
		var recCollider5 = new RectangleCollider(new Vector(2, 2), 7, 7);
		var recCollider6 = new RectangleCollider(new Vector(-1, -1), 2, 2);
		
		assertTrue(recCollider0.checkCollision(recCollider1));
		assertTrue(recCollider1.checkCollision(recCollider0));
		assertFalse(recCollider0.checkCollision(recCollider2));
		assertFalse(recCollider2.checkCollision(recCollider0));
		assertFalse(recCollider0.checkCollision(recCollider3));
		assertFalse(recCollider3.checkCollision(recCollider0));
		assertTrue(recCollider0.checkCollision(recCollider4));
		assertTrue(recCollider4.checkCollision(recCollider0));
		assertTrue(recCollider0.checkCollision(recCollider5));
		assertTrue(recCollider5.checkCollision(recCollider0));
		assertFalse(recCollider0.checkCollision(recCollider6));
		assertFalse(recCollider6.checkCollision(recCollider0));
	}
	
	@Test
	public void testCheckCollisionAgainstCircle() {
		var testCollider = new RectangleCollider(new Vector(3, 3), 5, 5);
		
		var crCollider0 = new CircleCollider(new Vector(4, 4), 2);
		var crCollider1 = new CircleCollider(new Vector(15, 6), 3);
		var crCollider2 = new CircleCollider(new Vector(6, 11), 3);
		var crCollider3 = new CircleCollider(new Vector(5, 6), 6);
		var crCollider4 = new CircleCollider(new Vector(10, 1), 2.5);
		var crCollider5 = new CircleCollider(new Vector(-1, -1), 6);
		var crCollider6 = new CircleCollider(new Vector(6, 6), 1);
		
		assertTrue(testCollider.checkCollision(crCollider0));
		assertFalse(testCollider.checkCollision(crCollider1));
		assertFalse(testCollider.checkCollision(crCollider2));
		assertTrue(testCollider.checkCollision(crCollider3));
		assertFalse(testCollider.checkCollision(crCollider4));
		assertTrue(testCollider.checkCollision(crCollider5));
		assertTrue(testCollider.checkCollision(crCollider6));
	}
	
	@Test
	public void testCheckCollisionAgainstTriangle() {
		var testCollider = new RectangleCollider(new Vector(3, 3), 5, 5);
		
		var trCollider0 = new TriangleCollider(new Vector(1, 4), new Vector(5, 2), new Vector(1, 2));
		var trCollider1 = new TriangleCollider(new Vector(6, 9), new Vector(9, 4), new Vector(7, 9));
		var trCollider2 = new TriangleCollider(new Vector(4, 9), new Vector(4, 5), new Vector(2, 5));
		var trCollider3 = new TriangleCollider(new Vector(11, 11), new Vector(12, 7), new Vector(13, 11));
		var trCollider4 = new TriangleCollider(new Vector(5, 6), new Vector(7, 4), new Vector(5, 4));
		var trCollider5 = new TriangleCollider(new Vector(6, 3), new Vector(7, 1), new Vector(8, 3));
		var trCollider6 = new TriangleCollider(new Vector(-1, -1), new Vector(11, 3), new Vector(5, 16));
		
		assertFalse(testCollider.checkCollision(trCollider0));
		assertTrue(testCollider.checkCollision(trCollider1));
		assertTrue(testCollider.checkCollision(trCollider2));
		assertFalse(testCollider.checkCollision(trCollider3));
		assertTrue(testCollider.checkCollision(trCollider4));
		assertFalse(testCollider.checkCollision(trCollider5));
		assertTrue(testCollider.checkCollision(trCollider6));
	}
	
	@Test
	public void testCheckCollisionAgainstComposite() {
		var recCollider0 = new RectangleCollider(new Vector(7, 3), 8, 3);
		var recCollider1 = new RectangleCollider(new Vector(13, 5), 1, 3);
		var recCollider2 = new RectangleCollider(new Vector(11, 11), 1, 2);
		var recCollider3 = new RectangleCollider(new Vector(17, 11), 2, 3);
		var recCollider4 = new RectangleCollider(new Vector(12, 1), 3, 1);
		
		var compositeCollider = new CompositeCollider(
				new RectangleCollider(new Vector(9, 1), 2, 9),
				new CircleCollider(new Vector(10, 13), 4),
				new TriangleCollider(new Vector(11, 4), new Vector(15, 7), new Vector(11, 5)),
				new TriangleCollider(new Vector(9, 6), new Vector(7, 8), new Vector(9, 7)));
		
		assertTrue(recCollider0.checkCollision(compositeCollider));
		assertTrue(recCollider1.checkCollision(compositeCollider));
		assertTrue(recCollider2.checkCollision(compositeCollider));
		assertFalse(recCollider3.checkCollision(compositeCollider));
		assertFalse(recCollider4.checkCollision(compositeCollider));
	}
	
	@Test
	public void testGetSquareDistanceToPoint() {
		var collider = new RectangleCollider(new Vector(3, 3), 5, 5);
		
		var point0 = new Vector(3, 3);
		var point1 = new Vector(5, 6);
		var point2 = new Vector(-1, -1);
		var point3 = new Vector(8, 1);
		var point4 = new Vector(8, 6);
		var point5 = new Vector(6, 11);
		
		var res = collider.getSquareDistanceToPoint(point0);
		assertEquals(0, res, 0);
		
		res = collider.getSquareDistanceToPoint(point1);
		assertEquals(0, res, 0);

		res = collider.getSquareDistanceToPoint(point2);
		assertEquals(32, res, 0);

		res = collider.getSquareDistanceToPoint(point3);
		assertEquals(4, res, 0);

		res = collider.getSquareDistanceToPoint(point4);
		assertEquals(0, res, 0);

		res = collider.getSquareDistanceToPoint(point5);
		assertEquals(9, res, 0);
	}

	@Test
	public void testMove() {
		var collider = new RectangleCollider(new Vector(5, 5), 5, 5);
		collider.move(new Vector(3, 3));
		assertEquals("Bounding Box begin", new Vector(8, 8), collider.getBoundingBoxBegin());
		assertEquals("Bounding Box end", new Vector(13, 13), collider.getBoundingBoxEnd());
	}

}
