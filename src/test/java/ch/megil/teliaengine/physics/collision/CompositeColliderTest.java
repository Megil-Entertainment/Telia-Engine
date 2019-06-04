package ch.megil.teliaengine.physics.collision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.megil.teliaengine.physics.Vector;

public class CompositeColliderTest {

	@Test
	public void testCompositeCollider() {
		var collider = new CompositeCollider(
				new RectangleCollider(new Vector(9, 1), 2, 9),
				new CircleCollider(new Vector(10, 13), 4),
				new TriangleCollider(new Vector(11, 4), new Vector(15, 7), new Vector(11, 5)),
				new TriangleCollider(new Vector(9, 6), new Vector(7, 8), new Vector(9, 7)));
		
		assertEquals("Bounding Box begin", new Vector(6, 1), collider.getBoundingBoxBegin());
		assertEquals("Bounding Box end", new Vector(15, 17), collider.getBoundingBoxEnd());
		assertEquals("Movement vector", Vector.ZERO, collider.getMovement());
	}
	

	@Test
	public void testCheckCollisionAgainstComposite() {
		var compCollider0 = new CompositeCollider(
				new RectangleCollider(new Vector(9, 1), 2, 9),
				new CircleCollider(new Vector(10, 13), 4),
				new TriangleCollider(new Vector(11, 4), new Vector(15, 7), new Vector(11, 5)),
				new TriangleCollider(new Vector(9, 6), new Vector(7, 8), new Vector(9, 7)));
		
		var compCollider1 = new CompositeCollider(
				new RectangleCollider(new Vector(3, 3), 2, 7),
				new TriangleCollider(new Vector(2, 1), new Vector(3, 3), new Vector(3, 5)),
				new TriangleCollider(new Vector(5, 3), new Vector(6, 1), new Vector(5, 5)),
				new TriangleCollider(new Vector(3, 10), new Vector(5, 10), new Vector(4, 14)));

		var compCollider2 = new CompositeCollider(
				new RectangleCollider(new Vector(6, 4), 1, 3),
				new TriangleCollider(new Vector(5, 1), new Vector(7, 4), new Vector(6, 4)),
				new TriangleCollider(new Vector(6, 7), new Vector(7, 7), new Vector(8, 8)));
		
		var compCollider3 = new CompositeCollider(
				new TriangleCollider(new Vector(5, 5), new Vector(6, 9), new Vector(5, 10)),
				new TriangleCollider(new Vector(6, 11), new Vector(9, 7), new Vector(6, 9)),
				new TriangleCollider(new Vector(6, 9), new Vector(6, 11), new Vector(5, 10)),
				new TriangleCollider(new Vector(5, 10), new Vector(6, 11), new Vector(4, 14)));
		
		var compCollider4 = new CompositeCollider(
				new RectangleCollider(new Vector(9, 1), 2, 9),
				new CircleCollider(new Vector(10, 13), 4),
				new TriangleCollider(new Vector(11, 4), new Vector(15, 7), new Vector(11, 5)),
				new TriangleCollider(new Vector(9, 6), new Vector(7, 8), new Vector(9, 7)));
		
		assertFalse(compCollider0.checkCollision(compCollider1));
		assertFalse(compCollider1.checkCollision(compCollider0));
		assertTrue(compCollider0.checkCollision(compCollider2));
		assertTrue(compCollider2.checkCollision(compCollider0));
		assertTrue(compCollider1.checkCollision(compCollider2));
		assertTrue(compCollider2.checkCollision(compCollider1));
		assertFalse(compCollider0.checkCollision(compCollider3));
		assertFalse(compCollider3.checkCollision(compCollider0));
		assertFalse(compCollider1.checkCollision(compCollider3));
		assertFalse(compCollider3.checkCollision(compCollider1));
		assertTrue(compCollider2.checkCollision(compCollider3));
		assertTrue(compCollider3.checkCollision(compCollider2));
		assertTrue(compCollider0.checkCollision(compCollider4));
		assertTrue(compCollider4.checkCollision(compCollider0));
	}
	
	@Test
	public void testCheckCollisionAgainstCompositeWithMovement() {
		var testCollider = new CompositeCollider(
				new RectangleCollider(new Vector(9, 1), 2, 9),
				new CircleCollider(new Vector(10, 13), 4),
				new TriangleCollider(new Vector(11, 4), new Vector(15, 7), new Vector(11, 5)),
				new TriangleCollider(new Vector(9, 6), new Vector(7, 8), new Vector(9, 7)));
		
		var compColliderLeft = new CompositeCollider(
				new TriangleCollider(new Vector(11, 3), new Vector(11, 8), new Vector(9, 10)),
				new TriangleCollider(new Vector(9, 10), new Vector(11, 9), new Vector(11, 11)));
		
		var compColliderRight = new CompositeCollider(
				new TriangleCollider(new Vector(11, 1), new Vector(15, 7), new Vector(11, 4)),
				new TriangleCollider(new Vector(11, 5), new Vector(15, 7), new Vector(11, 9)));
		
		assertTrue(testCollider.checkCollision(compColliderLeft));
		assertFalse(testCollider.checkCollision(compColliderRight));
		assertEquals("Movement vector", Vector.ZERO, testCollider.getMovement());
		
		testCollider.move(new Vector(2, 2));
		assertEquals("Movement vector", new Vector(2, 2), testCollider.getMovement());

		assertFalse(testCollider.checkCollision(compColliderLeft));
		assertTrue(testCollider.checkCollision(compColliderRight));
		assertEquals("Movement vector", Vector.ZERO, testCollider.getMovement());

		testCollider.move(new Vector(-1, -1));
		assertEquals("Movement vector", new Vector(-1, -1), testCollider.getMovement());

		assertTrue(testCollider.checkCollision(compColliderLeft));
		assertTrue(testCollider.checkCollision(compColliderRight));
		assertEquals("Movement vector", Vector.ZERO, testCollider.getMovement());
	}

	@Test
	public void testCheckCollisionAgainstRectangle() {
		var compCollider = new CompositeCollider(
				new RectangleCollider(new Vector(9, 1), 2, 9),
				new CircleCollider(new Vector(10, 13), 4),
				new TriangleCollider(new Vector(11, 4), new Vector(15, 7), new Vector(11, 5)),
				new TriangleCollider(new Vector(9, 6), new Vector(7, 8), new Vector(9, 7)));
		
		var recCollider0 = new RectangleCollider(new Vector(7, 3), 8, 3);
		var recCollider1 = new RectangleCollider(new Vector(13, 5), 1, 3);
		var recCollider2 = new RectangleCollider(new Vector(11, 11), 1, 2);
		var recCollider3 = new RectangleCollider(new Vector(17, 11), 2, 3);
		var recCollider4 = new RectangleCollider(new Vector(12, 1), 3, 1);
		
		assertTrue(compCollider.checkCollision(recCollider0));
		assertTrue(compCollider.checkCollision(recCollider1));
		assertTrue(compCollider.checkCollision(recCollider2));
		assertFalse(compCollider.checkCollision(recCollider3));
		assertFalse(compCollider.checkCollision(recCollider4));
	}
	
	@Test
	public void testCheckCollisionAgainstCircle() {
		var compCollider = new CompositeCollider(
				new RectangleCollider(new Vector(9, 1), 2, 9),
				new CircleCollider(new Vector(10, 13), 4),
				new TriangleCollider(new Vector(11, 4), new Vector(15, 7), new Vector(11, 5)),
				new TriangleCollider(new Vector(9, 6), new Vector(7, 8), new Vector(9, 7)));

		var crCollider0 = new CircleCollider(new Vector(10, 6), 1);
		var crCollider1 = new CircleCollider(new Vector(13, 7), 1);
		var crCollider2 = new CircleCollider(new Vector(12, 12), 3);
		var crCollider3 = new CircleCollider(new Vector(14, 3), 2);
		var crCollider4 = new CircleCollider(new Vector(19, 12), 2);
		
		assertTrue(compCollider.checkCollision(crCollider0));
		assertTrue(compCollider.checkCollision(crCollider1));
		assertTrue(compCollider.checkCollision(crCollider2));
		assertFalse(compCollider.checkCollision(crCollider3));
		assertFalse(compCollider.checkCollision(crCollider4));
	}
	
	@Test
	public void testCheckCollisionAgainstTriangle() {
		var compCollider = new CompositeCollider(
				new RectangleCollider(new Vector(9, 1), 2, 9),
				new CircleCollider(new Vector(10, 13), 4),
				new TriangleCollider(new Vector(11, 4), new Vector(15, 7), new Vector(11, 5)),
				new TriangleCollider(new Vector(9, 6), new Vector(7, 8), new Vector(9, 7)));
		
		var trCollider0 = new TriangleCollider(new Vector(7, 2), new Vector(9, 3), new Vector(7, 7));
		var trCollider1 = new TriangleCollider(new Vector(8, 6), new Vector(12, 7), new Vector(8, 9));
		var trCollider2 = new TriangleCollider(new Vector(13, 5), new Vector(15, 5), new Vector(11, 12));
		var trCollider3 = new TriangleCollider(new Vector(16, 9), new Vector(19, 15), new Vector(16, 15));
		var trCollider4 = new TriangleCollider(new Vector(9, 2), new Vector(11, 2), new Vector(10, 4));
		
		assertFalse(compCollider.checkCollision(trCollider0));
		assertTrue(compCollider.checkCollision(trCollider1));
		assertTrue(compCollider.checkCollision(trCollider2));
		assertFalse(compCollider.checkCollision(trCollider3));
		assertTrue(compCollider.checkCollision(trCollider4));
	}

	@Test
	public void testMove() {
		var collider = new CompositeCollider(
				new RectangleCollider(new Vector(9, 1), 2, 9),
				new CircleCollider(new Vector(10, 13), 4),
				new TriangleCollider(new Vector(11, 4), new Vector(15, 7), new Vector(11, 5)),
				new TriangleCollider(new Vector(9, 6), new Vector(7, 8), new Vector(9, 7)));
		
		collider.move(new Vector(3, 5));
		assertEquals("Bounding Box begin", new Vector(9, 6), collider.getBoundingBoxBegin());
		assertEquals("Bounding Box end", new Vector(18, 22), collider.getBoundingBoxEnd());
		assertEquals("Movement vector", new Vector(3, 5), collider.getMovement());
		
		collider.move(new Vector(3, 5));
		assertEquals("Bounding Box begin", new Vector(12, 11), collider.getBoundingBoxBegin());
		assertEquals("Bounding Box end", new Vector(21, 27), collider.getBoundingBoxEnd());
		assertEquals("Movement vector", new Vector(6, 10), collider.getMovement());
	}

}
