package ch.megil.teliaengine.physics.collision.old;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.collision.RectangleCollider;

public class RectangleColliderTestOld {
	private Vector vector1;
	private Vector vector2;
	private Vector vector3;
	private Vector vector4;
	private Vector vector5;
	private Vector vector6;
	private RectangleCollider sqCollider1;
	private RectangleCollider sqCollider2;
	private RectangleCollider sqCollider3;
	private RectangleCollider sqCollider4;
	private RectangleCollider sqCollider5;
	private RectangleCollider sqCollider6;
	
	@Before
	public void setUp() {
		vector1 = new Vector(5,5);
		vector2 = new Vector(3,3);
		vector3 = new Vector(20,20);
		vector4 = new Vector(3,8);
		vector5 = new Vector(3,3);
		vector6 = new Vector(2,2);
		
		sqCollider1 = new RectangleCollider(vector1, 5, 5);
		sqCollider2 = new RectangleCollider(vector2, 5, 5);
		sqCollider3 = new RectangleCollider(vector3, 5, 5);
		sqCollider4 = new RectangleCollider(vector4, 5, 5);
		sqCollider5 = new RectangleCollider(vector5, 5, 5);
		sqCollider6 = new RectangleCollider(vector6, 7, 7);
	}
	
	@Test
	public void testBoundingBoxSizes() {
		assertEquals(5, sqCollider1.getBoundingBoxBegin().getX(),0);
		assertEquals(5, sqCollider1.getBoundingBoxBegin().getY(),0);
		assertEquals(10, sqCollider1.getBoundingBoxEnd().getX(),0);
		assertEquals(10, sqCollider1.getBoundingBoxEnd().getY(),0);
		
		assertEquals(3, sqCollider2.getBoundingBoxBegin().getX(),0);
		assertEquals(3, sqCollider2.getBoundingBoxBegin().getY(),0);
		assertEquals(8, sqCollider2.getBoundingBoxEnd().getX(),0);
		assertEquals(8, sqCollider2.getBoundingBoxEnd().getY(),0);
		
		assertEquals(20, sqCollider3.getBoundingBoxBegin().getX(),0);
		assertEquals(20, sqCollider3.getBoundingBoxBegin().getY(),0);
		assertEquals(25, sqCollider3.getBoundingBoxEnd().getX(),0);
		assertEquals(25, sqCollider3.getBoundingBoxEnd().getY(),0);
	}
	
	@Test
	public void testCollisionDetection() {
		assertTrue(sqCollider1.checkCollision(sqCollider2));
		assertTrue(sqCollider2.checkCollision(sqCollider1));
		assertFalse(sqCollider1.checkCollision(sqCollider3));
		assertFalse(sqCollider2.checkCollision(sqCollider3));
		assertFalse(sqCollider2.checkCollision(sqCollider4));
		assertTrue(sqCollider2.checkCollision(sqCollider5));
		assertTrue(sqCollider2.checkCollision(sqCollider6));
		assertTrue(sqCollider6.checkCollision(sqCollider2));
		sqCollider2.move(vector3);
		assertFalse(sqCollider2.checkCollision(sqCollider1));
	}
	
	@Test
	public void testMove() {
		sqCollider1.move(vector2);
		assertEquals(8, sqCollider1.getBoundingBoxBegin().getX(),0);
		assertEquals(8, sqCollider1.getBoundingBoxBegin().getY(),0);
		assertEquals(13, sqCollider1.getBoundingBoxEnd().getX(),0);
		assertEquals(13, sqCollider1.getBoundingBoxEnd().getY(),0);
	}
}
