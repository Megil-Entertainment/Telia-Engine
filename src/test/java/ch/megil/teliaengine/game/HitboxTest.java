package ch.megil.teliaengine.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class HitboxTest {
	private Vector vector1;
	private Vector vector2;
	private Vector vector3;
	private Vector vector4;
	private Vector vector5;
	private Vector vector6;
	private Hitbox hitbox1;
	private Hitbox hitbox2;
	private Hitbox hitbox3;
	private Hitbox hitbox4;
	private Hitbox hitbox5;
	private Hitbox hitbox6;
	
	@Before
	public void setUp() {
		vector1 = new Vector(5,5);
		vector2 = new Vector(3,3);
		vector3 = new Vector(20,20);
		vector4 = new Vector(3,8);
		vector5 = new Vector(3,3);
		vector6 = new Vector(2,2);
		
		hitbox1 = new Hitbox(vector1, 5, 5);
		hitbox2 = new Hitbox(vector2, 5, 5);
		hitbox3 = new Hitbox(vector3, 5, 5);
		hitbox4 = new Hitbox(vector4, 5, 5);
		hitbox5 = new Hitbox(vector5, 5, 5);
		hitbox6 = new Hitbox(vector6, 7, 7);
	}
	
	@Test
	public void testHitboxSizes() {
		assertEquals(5, hitbox1.getOrigin().getX(),0);
		assertEquals(5, hitbox1.getVectorSize().getX(),0);
		assertEquals(10, hitbox1.getOrigin().add(hitbox1.getVectorSize()).getX(),0);
		
		assertEquals(3, hitbox2.getOrigin().getX(),0);
		assertEquals(5, hitbox2.getVectorSize().getX(),0);
		assertEquals(8, hitbox2.getOrigin().add(hitbox2.getVectorSize()).getX(),0);
		
		assertEquals(20, hitbox3.getOrigin().getX(),0);
		assertEquals(5, hitbox3.getVectorSize().getX(),0);
		assertEquals(25, hitbox3.getOrigin().add(hitbox3.getVectorSize()).getX(),0);
	}
	
	@Test
	public void testCollisionDetection() {
		assertTrue(hitbox1.checkCollision(hitbox2));
		assertTrue(hitbox2.checkCollision(hitbox1));
		assertFalse(hitbox1.checkCollision(hitbox3));
		assertFalse(hitbox2.checkCollision(hitbox3));
		assertFalse(hitbox2.checkCollision(hitbox4));
		assertTrue(hitbox2.checkCollision(hitbox5));
		assertTrue(hitbox2.checkCollision(hitbox6));
		assertTrue(hitbox6.checkCollision(hitbox2));
		hitbox2.setOrigin(vector3);
		assertFalse(hitbox2.checkCollision(hitbox1));
	}
	
	@Test
	public void testSetOrigin() {
		hitbox1.setOrigin(vector2);
		assertEquals(3, hitbox1.getOrigin().getX(),0);
	}
	
	@Test
	public void testSetVectorSize() {
		hitbox1.setVectorSize(10, 10);
		assertEquals(10, hitbox1.getVectorSize().getX(),0);
	}
}
