package ch.megil.teliaengine.physics.collision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ch.megil.teliaengine.physics.Vector;

public class SquareColliderTest {
	private Vector vector1;
	private Vector vector2;
	private Vector vector3;
	private Vector vector4;
	private Vector vector5;
	private Vector vector6;
	private SquareCollider sqCollider1;
	private SquareCollider sqCollider2;
	private SquareCollider sqCollider3;
	private SquareCollider sqCollider4;
	private SquareCollider sqCollider5;
	private SquareCollider sqCollider6;
	
	@Before
	public void setUp() {
		vector1 = new Vector(5,5);
		vector2 = new Vector(3,3);
		vector3 = new Vector(20,20);
		vector4 = new Vector(3,8);
		vector5 = new Vector(3,3);
		vector6 = new Vector(2,2);
		
		sqCollider1 = new SquareCollider(vector1, 5, 5);
		sqCollider2 = new SquareCollider(vector2, 5, 5);
		sqCollider3 = new SquareCollider(vector3, 5, 5);
		sqCollider4 = new SquareCollider(vector4, 5, 5);
		sqCollider5 = new SquareCollider(vector5, 5, 5);
		sqCollider6 = new SquareCollider(vector6, 7, 7);
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
		assertTrue(sqCollider2.checkCollision(sqCollider5));//
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
