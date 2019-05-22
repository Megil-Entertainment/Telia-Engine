package ch.megil.teliaengine.physics.collision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import ch.megil.teliaengine.physics.Vector;

public class ColliderTest {
	private Collider collider1;
	private Collider collider2;
	private Collider collider3;
	private Collider collider4;
	private Collider collider5;
	private Collider collider6;

	@Before
	public void setUp() throws Exception {		
		collider1 = mock(Collider.class, CALLS_REAL_METHODS);
		collider1.setBoundingBox(new Vector(5,5), new Vector(10, 10));
		when(collider1.checkDetailedCollision(any())).thenReturn(true);
		
		collider2 = mock(Collider.class, CALLS_REAL_METHODS);
		collider2.setBoundingBox(new Vector(3,3), new Vector(8, 8));
		when(collider2.checkDetailedCollision(any())).thenReturn(true);
		
		collider3 = mock(Collider.class, CALLS_REAL_METHODS);
		collider3.setBoundingBox(new Vector(20,20), new Vector(25, 25));
		when(collider3.checkDetailedCollision(any())).thenReturn(true);
		
		collider4 = mock(Collider.class, CALLS_REAL_METHODS);
		collider4.setBoundingBox(new Vector(3,8), new Vector(8, 13));
		when(collider4.checkDetailedCollision(any())).thenReturn(true);
		
		collider5 = mock(Collider.class, CALLS_REAL_METHODS);
		collider5.setBoundingBox(new Vector(3,3), new Vector(8, 8));
		when(collider5.checkDetailedCollision(any())).thenReturn(true);
		
		collider6 = mock(Collider.class, CALLS_REAL_METHODS);
		collider6.setBoundingBox(new Vector(2,2), new Vector(7, 7));
		when(collider6.checkDetailedCollision(any())).thenReturn(true);
	}
	
	@Test
	public void testBoundingBoxCollision() {
		assertTrue(collider1.checkCollision(collider2));
		assertTrue(collider2.checkCollision(collider1));
		assertFalse(collider1.checkCollision(collider3));
		assertFalse(collider2.checkCollision(collider3));
		assertFalse(collider2.checkCollision(collider4));
		assertTrue(collider2.checkCollision(collider5));
		assertTrue(collider2.checkCollision(collider6));
		assertTrue(collider6.checkCollision(collider2));
		collider2.move(new Vector(20, 20));
		assertFalse(collider2.checkCollision(collider1));
	}
	
	@Test
	public void testMove() {
		collider1.move(new Vector(3, 3));
		assertEquals(8, collider1.getBoundingBoxBegin().getX(),0);
		assertEquals(8, collider1.getBoundingBoxBegin().getY(),0);
		assertEquals(13, collider1.getBoundingBoxEnd().getX(),0);
		assertEquals(13, collider1.getBoundingBoxEnd().getY(),0);
	}
	
	@Test
	public void testRound() {
		collider1.setBoundingBox(new Vector(0.000001, 0.49), new Vector(0.5, 0.75));
		collider1.round();
		assertEquals(0, collider1.getBoundingBoxBegin().getX(),0);
		assertEquals(0, collider1.getBoundingBoxBegin().getY(),0);
		assertEquals(1, collider1.getBoundingBoxEnd().getX(),0);
		assertEquals(1, collider1.getBoundingBoxEnd().getY(),0);
	}
}
