package ch.megil.teliaengine.physics.collision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.megil.teliaengine.physics.Vector;

public class TriangleColliderTest {

	@Test
	public void testTriangleCollider() {
		var colliderWithOrigin = new TriangleCollider(new Vector(2, 2), new Vector(0, 0), new Vector(5, 1), new Vector(2, 4));
		assertEquals("Bounding Box begin", new Vector(2, 2), colliderWithOrigin.getBoundingBoxBegin());
		assertEquals("Bounding Box end", new Vector(7, 6), colliderWithOrigin.getBoundingBoxEnd());
		assertEquals("P0", new Vector(2, 2), colliderWithOrigin.getP0());
		assertEquals("P1", new Vector(7, 3), colliderWithOrigin.getP1());
		assertEquals("P2", new Vector(4, 6), colliderWithOrigin.getP2());
		
		var colliderNoOrigin = new TriangleCollider(new Vector(2, 2), new Vector(7, 3), new Vector(4, 6));
		assertEquals("Bounding Box begin", new Vector(2, 2), colliderNoOrigin.getBoundingBoxBegin());
		assertEquals("Bounding Box end", new Vector(7, 6), colliderNoOrigin.getBoundingBoxEnd());
		assertEquals("P0", new Vector(2, 2), colliderNoOrigin.getP0());
		assertEquals("P1", new Vector(7, 3), colliderNoOrigin.getP1());
		assertEquals("P2", new Vector(4, 6), colliderNoOrigin.getP2());
	}
	
	@Test
	public void testCheckCollisionAgainstTriangle() {
		var trCollider0 = new TriangleCollider(new Vector(2, 2), new Vector(7, 3), new Vector(4, 6));
		var trCollider1 = new TriangleCollider(new Vector(5, 5), new Vector(6, 4), new Vector(7, 7));
		var trCollider2 = new TriangleCollider(new Vector(1, 5), new Vector(8, 5), new Vector(1, 3));
		var trCollider3 = new TriangleCollider(new Vector(2, 6), new Vector(7, 8), new Vector(3, 9));
		var trCollider4 = new TriangleCollider(new Vector(6, 4), new Vector(10, 4), new Vector(10, 7));
		var trCollider5 = new TriangleCollider(new Vector(4, 4), new Vector(3, 3), new Vector(5, 3));
		var trCollider6 = new TriangleCollider(new Vector(3, 3), new Vector(-1, -1), new Vector(4, -1));
		var trCollider7 = new TriangleCollider(new Vector(2, 2), new Vector(7, 3), new Vector(5, 0));
		var trCollider8 = new TriangleCollider(new Vector(2, 2), new Vector(7, 3), new Vector(4, 6));
		var trCollider9 = new TriangleCollider(new Vector(2, 2), new Vector(4, 6), new Vector(7, 3));
		
		assertFalse(trCollider0.checkCollision(trCollider1));
		assertFalse(trCollider1.checkCollision(trCollider0));
		assertTrue(trCollider0.checkCollision(trCollider2));
		assertTrue(trCollider2.checkCollision(trCollider0));
		assertFalse(trCollider0.checkCollision(trCollider3));
		assertFalse(trCollider3.checkCollision(trCollider0));
		assertFalse(trCollider0.checkCollision(trCollider4));
		assertFalse(trCollider4.checkCollision(trCollider0));
		assertTrue(trCollider0.checkCollision(trCollider5));
		assertTrue(trCollider5.checkCollision(trCollider0));
		assertTrue(trCollider0.checkCollision(trCollider6));
		assertTrue(trCollider6.checkCollision(trCollider0));
		assertFalse(trCollider0.checkCollision(trCollider7));
		assertFalse(trCollider7.checkCollision(trCollider0));
		assertTrue(trCollider0.checkCollision(trCollider8));
		assertTrue(trCollider8.checkCollision(trCollider0));
		assertTrue(trCollider0.checkCollision(trCollider9));
		assertTrue(trCollider9.checkCollision(trCollider0));
	}

	@Test
	public void testCheckCollisionAgainstRectangle() {
		var testCollider = new TriangleCollider(new Vector(2, 2), new Vector(9, 2), new Vector(8, 10));

		var recCollider0 = new RectangleCollider(new Vector(4, 3), 3, 1);
		var recCollider1 = new RectangleCollider(new Vector(2, 6), 2, 2);
		var recCollider2 = new RectangleCollider(new Vector(1, 0), 9, 10);
		var recCollider3 = new RectangleCollider(new Vector(12, 4), 3, 3);
		var recCollider4 = new RectangleCollider(new Vector(6, -2), 3, 4);
		var recCollider5 = new RectangleCollider(new Vector(3, 1), 1, 4);
		
		assertTrue(testCollider.checkCollision(recCollider0));
		assertFalse(testCollider.checkCollision(recCollider1));
		assertTrue(testCollider.checkCollision(recCollider2));
		assertFalse(testCollider.checkCollision(recCollider3));
		assertFalse(testCollider.checkCollision(recCollider4));
		assertTrue(testCollider.checkCollision(recCollider5));
	}
	
	@Test
	public void testCheckCollisionAgainstCircle() {
		var testCollider = new TriangleCollider(new Vector(2, 2), new Vector(7, 1), new Vector(8, 10));

		var crCollider0 = new CircleCollider(new Vector(0, 6), 4);
		var crCollider1 = new CircleCollider(new Vector(5, 3), 1);
		var crCollider2 = new CircleCollider(new Vector(7, 11), 2);
		var crCollider3 = new CircleCollider(new Vector(9, 7), 3);
		var crCollider4 = new CircleCollider(new Vector(7, -1), 2);
		var crCollider5 = new CircleCollider(new Vector(12, 3), 2);
		
		assertFalse(testCollider.checkCollision(crCollider0));
		assertTrue(testCollider.checkCollision(crCollider1));
		assertTrue(testCollider.checkCollision(crCollider2));
		assertTrue(testCollider.checkCollision(crCollider3));
		assertFalse(testCollider.checkCollision(crCollider4));
		assertFalse(testCollider.checkCollision(crCollider5));
	}
	
	@Test
	public void testCheckCollisionAgainstComposite() {
		var trCollider0 = new TriangleCollider(new Vector(7, 2), new Vector(9, 3), new Vector(7, 7));
		var trCollider1 = new TriangleCollider(new Vector(8, 6), new Vector(12, 7), new Vector(8, 9));
		var trCollider2 = new TriangleCollider(new Vector(13, 5), new Vector(15, 5), new Vector(11, 12));
		var trCollider3 = new TriangleCollider(new Vector(16, 9), new Vector(19, 15), new Vector(16, 15));
		var trCollider4 = new TriangleCollider(new Vector(9, 2), new Vector(11, 2), new Vector(10, 4));
		
		var compositeCollider = new CompositeCollider(
				new RectangleCollider(new Vector(9, 1), 2, 9),
				new CircleCollider(new Vector(10, 13), 4),
				new TriangleCollider(new Vector(11, 4), new Vector(15, 7), new Vector(11, 5)),
				new TriangleCollider(new Vector(9, 6), new Vector(7, 8), new Vector(9, 7)));
		
		assertFalse(trCollider0.checkCollision(compositeCollider));
		assertTrue(trCollider1.checkCollision(compositeCollider));
		assertTrue(trCollider2.checkCollision(compositeCollider));
		assertFalse(trCollider3.checkCollision(compositeCollider));
		assertTrue(trCollider4.checkCollision(compositeCollider));
	}
	
	@Test
	public void testGetSquareDistanceToPoint() {
		var collider = new TriangleCollider(new Vector(2, 2), new Vector(9, 2), new Vector(8, 10));
		
		var point0 = new Vector(6, 4);
		var point1 = new Vector(1, -1);
		var point2 = new Vector(3, 6);
		var point3 = new Vector(12, 2);
		var point4 = new Vector(7, 2);
		
		var res = collider.getSquareDistanceToPoint(point0);
		assertEquals(0, res, 0);
		
		res = collider.getSquareDistanceToPoint(point1);
		assertEquals(10, res, 0);
		
		res = collider.getSquareDistanceToPoint(point2);
		assertEquals(2.56, res, 0.000001);
		
		res = collider.getSquareDistanceToPoint(point3);
		assertEquals(9, res, 0);
		
		res = collider.getSquareDistanceToPoint(point4);
		assertEquals(0, res, 0);
	}

	@Test
	public void testMove() {
		var collider = new TriangleCollider(new Vector(2, 2), new Vector(9, 2), new Vector(8, 10));
		collider.move(new Vector(3, 3));
		assertEquals("Bounding Box begin", new Vector(5, 5), collider.getBoundingBoxBegin());
		assertEquals("Bounding Box end", new Vector(12, 13), collider.getBoundingBoxEnd());
		assertEquals("P0", new Vector(5, 5), collider.getP0());
		assertEquals("P1", new Vector(12, 5), collider.getP1());
		assertEquals("P2", new Vector(11, 13), collider.getP2());
	}

}
