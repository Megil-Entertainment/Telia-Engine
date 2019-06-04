package ch.megil.teliaengine.physics.collision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import ch.megil.teliaengine.physics.Vector;

public class ColliderTest {
	@Test
	public void testCollider() {
		var collider = new Collider() {
			@Override
			protected boolean checkDetailedCollision(Collider other) {
				return false;
			}
		};
		
		assertEquals("Bounding Box begin", Vector.ZERO, collider.getBoundingBoxBegin());
		assertEquals("Bounding Box end", Vector.ZERO, collider.getBoundingBoxEnd());
	}

	@Test
	public void testCheckCollision() {
		var testCollider = mock(Collider.class, CALLS_REAL_METHODS);
		testCollider.setBoundingBox(new Vector(3, 3), new Vector(8, 8));
		when(testCollider.checkDetailedCollision(any())).thenReturn(true);
		
		var other0 = mock(Collider.class, CALLS_REAL_METHODS);
		other0.setBoundingBox(new Vector(5, 5), new Vector(10, 10));
		when(other0.checkDetailedCollision(any())).thenReturn(true);
		
		var other1 = mock(Collider.class, CALLS_REAL_METHODS);
		other1.setBoundingBox(new Vector(20, 20), new Vector(25, 25));
		when(other1.checkDetailedCollision(any())).thenReturn(true);
		
		var other2 = mock(Collider.class, CALLS_REAL_METHODS);
		other2.setBoundingBox(new Vector(3, 8), new Vector(8, 13));
		when(other2.checkDetailedCollision(any())).thenReturn(true);
		
		var other3 = mock(Collider.class, CALLS_REAL_METHODS);
		other3.setBoundingBox(new Vector(3, 3), new Vector(8, 8));
		when(other3.checkDetailedCollision(any())).thenReturn(true);
		
		var other4 = mock(Collider.class, CALLS_REAL_METHODS);
		other4.setBoundingBox(new Vector(2, 2), new Vector(9, 9));
		when(other4.checkDetailedCollision(any())).thenReturn(true);
		
		assertTrue(testCollider.checkCollision(other0));
		assertTrue(other0.checkCollision(testCollider));
		assertFalse(testCollider.checkCollision(other1));
		assertFalse(testCollider.checkCollision(other2));
		assertTrue(testCollider.checkCollision(other3));
		assertTrue(testCollider.checkCollision(other4));
		assertTrue(other4.checkCollision(testCollider));
		testCollider.move(new Vector(20, 20));
		assertFalse(testCollider.checkCollision(other0));
	}

	@Test
	public void testSetBoundingBox() {
		var collider = mock(Collider.class, CALLS_REAL_METHODS);
		collider.setBoundingBox(new Vector(2, 2), new Vector(7, 7));

		assertEquals("Bounding Box begin", new Vector(2, 2), collider.getBoundingBoxBegin());
		assertEquals("Bounding Box end", new Vector(7, 7), collider.getBoundingBoxEnd());
	}

	@Test
	public void testRound() {
		var collider = mock(Collider.class, CALLS_REAL_METHODS);
		collider.setBoundingBox(new Vector(0.000001, 0.49), new Vector(0.5, 0.75));
		collider.round();
		assertEquals(0, collider.getBoundingBoxBegin().getX(), 0);
		assertEquals(0, collider.getBoundingBoxBegin().getY(), 0);
		assertEquals(1, collider.getBoundingBoxEnd().getX(), 0);
		assertEquals(1, collider.getBoundingBoxEnd().getY(), 0);
	}

	@Test
	public void testMove() {
		var collider = mock(Collider.class, CALLS_REAL_METHODS);
		collider.setBoundingBox(new Vector(5, 5), new Vector(10, 10));
		collider.move(new Vector(3, 3));
		assertEquals(8, collider.getBoundingBoxBegin().getX(),0);
		assertEquals(8, collider.getBoundingBoxBegin().getY(),0);
		assertEquals(13, collider.getBoundingBoxEnd().getX(),0);
		assertEquals(13, collider.getBoundingBoxEnd().getY(),0);
	}

}
