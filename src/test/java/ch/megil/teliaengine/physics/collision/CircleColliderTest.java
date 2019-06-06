package ch.megil.teliaengine.physics.collision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.megil.teliaengine.physics.Vector;

public class CircleColliderTest {

	@Test
	public void testCircleCollider() {
		var collider = new CircleCollider(new Vector(1, 2), 3);
		assertEquals("Bounding Box begin", new Vector(-2, -1), collider.getBoundingBoxBegin());
		assertEquals("Bounding Box end", new Vector(4, 5), collider.getBoundingBoxEnd());
		assertEquals("Circle center", new Vector(1, 2), collider.getCenter());
	}

	@Test
	public void testCheckCollisionAgainstCircle() {
		var crCollider0 = new CircleCollider(new Vector(5, 5), 3);
		var crCollider1 = new CircleCollider(new Vector(10, 5), 2);
		var crCollider2 = new CircleCollider(new Vector(5, 3), 1);
		var crCollider3 = new CircleCollider(new Vector(5, 8), 2);
		var crCollider4 = new CircleCollider(new Vector(11, 11), 2);
		var crCollider5 = new CircleCollider(new Vector(2, 2), 1);
		var crCollider6 = new CircleCollider(new Vector(5, 5), 4);
		var crCollider7 = new CircleCollider(new Vector(5, 5), 3);
		
		assertFalse(crCollider0.checkCollision(crCollider1));
		assertFalse(crCollider1.checkCollision(crCollider0));
		assertTrue(crCollider0.checkCollision(crCollider2));
		assertTrue(crCollider2.checkCollision(crCollider0));
		assertTrue(crCollider0.checkCollision(crCollider3));
		assertTrue(crCollider3.checkCollision(crCollider0));
		assertFalse(crCollider0.checkCollision(crCollider4));
		assertFalse(crCollider4.checkCollision(crCollider0));
		assertFalse(crCollider0.checkCollision(crCollider5));
		assertFalse(crCollider5.checkCollision(crCollider0));
		assertTrue(crCollider0.checkCollision(crCollider6));
		assertTrue(crCollider6.checkCollision(crCollider0));
		assertTrue(crCollider0.checkCollision(crCollider7));
		assertTrue(crCollider7.checkCollision(crCollider0));
	}
	
	@Test
	public void testCheckCollisionAgainstRectangle() {
		var testCollider = new CircleCollider(new Vector(5, 5), 4);
		
		var recCollider0 = new RectangleCollider(new Vector(1, 1), 3, 4);
		var recCollider1 = new RectangleCollider(new Vector(8, 8), 4, 4);
		var recCollider2 = new RectangleCollider(new Vector(6, 6), 1, 1);
		var recCollider3 = new RectangleCollider(new Vector(9, 2), 4, 5);
		var recCollider4 = new RectangleCollider(new Vector(-1, -1), 11, 14);
		
		assertTrue(testCollider.checkCollision(recCollider0));
		assertFalse(testCollider.checkCollision(recCollider1));
		assertTrue(testCollider.checkCollision(recCollider2));
		assertFalse(testCollider.checkCollision(recCollider3));
		assertTrue(testCollider.checkCollision(recCollider4));
	}
	
	@Test
	public void testCheckCollisionAgainstTriangle() {
		var testCollider = new CircleCollider(new Vector(5, 5), 4);
		
		var trCollider0 = new TriangleCollider(new Vector(7, 1), new Vector(9, 1), new Vector(10, 5));
		var trCollider1 = new TriangleCollider(new Vector(5, 7), new Vector(3, 4), new Vector(7, 4));
		var trCollider2 = new TriangleCollider(new Vector(-1, -1), new Vector(11, 7), new Vector(-1, 4));
		var trCollider3 = new TriangleCollider(new Vector(2, 8), new Vector(3, 9), new Vector(21, 16));
		var trCollider4 = new TriangleCollider(new Vector(9, 10), new Vector(11, 8), new Vector(11, 10));
		
		assertFalse(testCollider.checkCollision(trCollider0));
		assertTrue(testCollider.checkCollision(trCollider1));
		assertTrue(testCollider.checkCollision(trCollider2));
		assertTrue(testCollider.checkCollision(trCollider3));
		assertFalse(testCollider.checkCollision(trCollider4));
	}
	
	@Test
	public void testCheckCollisionAgainstComposite() {
		var crCollider0 = new CircleCollider(new Vector(10, 6), 1);
		var crCollider1 = new CircleCollider(new Vector(13, 7), 1);
		var crCollider2 = new CircleCollider(new Vector(12, 12), 3);
		var crCollider3 = new CircleCollider(new Vector(14, 3), 2);
		var crCollider4 = new CircleCollider(new Vector(19, 12), 2);
		
		var compositeCollider = new CompositeCollider(
				new RectangleCollider(new Vector(9, 1), 2, 9),
				new CircleCollider(new Vector(10, 13), 4),
				new TriangleCollider(new Vector(11, 4), new Vector(15, 7), new Vector(11, 5)),
				new TriangleCollider(new Vector(9, 6), new Vector(7, 8), new Vector(9, 7)));
		
		assertTrue(crCollider0.checkCollision(compositeCollider));
		assertTrue(crCollider1.checkCollision(compositeCollider));
		assertTrue(crCollider2.checkCollision(compositeCollider));
		assertFalse(crCollider3.checkCollision(compositeCollider));
		assertFalse(crCollider4.checkCollision(compositeCollider));
	}

	@Test
	public void testMove() {
		var collider = new CircleCollider(new Vector(5, 5), 3);
		collider.move(new Vector(3, 3));
		assertEquals("Bounding Box begin", new Vector(5, 5), collider.getBoundingBoxBegin());
		assertEquals("Bounding Box end", new Vector(11, 11), collider.getBoundingBoxEnd());
		assertEquals("Circle center", new Vector(8, 8), collider.getCenter());
	}

}
