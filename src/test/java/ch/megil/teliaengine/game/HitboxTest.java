package ch.megil.teliaengine.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class HitboxTest {
	Vector vector1;
	Vector vector2;
	Vector vector3;
	Hitbox hitbox1;
	Hitbox hitbox2;
	Hitbox hitbox3;
	
	@Before
	public void setUp() {
		vector1 = new Vector(5,5);
		vector2 = new Vector(3,3);
		vector3 = new Vector(20,20);
		hitbox1 = new Hitbox(vector1, 5, 5);
		hitbox2 = new Hitbox(vector2, 5, 5);
		hitbox3 = new Hitbox(vector3, 5, 5);
	}
	
	@Test
	public void testHitboxSizes() {
		assertEquals(5, hitbox1.origin.getX(),0);
		assertEquals(5, hitbox1.size.getX(),0);
		assertEquals(10, hitbox1.origin.addVectors(hitbox1.size).getX(),0);
		
		assertEquals(3, hitbox2.origin.getX(),0);
		assertEquals(5, hitbox2.size.getX(),0);
		assertEquals(8, hitbox2.origin.addVectors(hitbox2.size).getX(),0);
		
		assertEquals(20, hitbox3.origin.getX(),0);
		assertEquals(5, hitbox3.size.getX(),0);
		assertEquals(25, hitbox3.origin.addVectors(hitbox3.size).getX(),0);
	}
	
	@Test
	public void testCollisionDetection() {
		assertTrue(hitbox1.checkCollision(hitbox2));
		assertTrue(hitbox2.checkCollision(hitbox1));
		assertFalse(hitbox1.checkCollision(hitbox3));
		assertFalse(hitbox2.checkCollision(hitbox3));
		hitbox2.setOrigin(vector3);
		assertFalse(hitbox2.checkCollision(hitbox1));
	}
	
	@Test
	public void testSetOrigin() {
		hitbox1.setOrigin(vector2);
		assertEquals(3, hitbox1.origin.getX(),0);
	}
	
	@Test
	public void testSetVectorSize() {
		hitbox1.setVectorSize(10, 10);
		assertEquals(10, hitbox1.size.getX(),0);
	}
}
