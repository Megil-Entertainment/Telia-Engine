package ch.megil.teliaengine.physics.collision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
	}
	

	@Test
	public void testCheckCollisionAgainstComposite() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckCollisionAgainstRectangle() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testCheckCollisionAgainstCircle() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testCheckCollisionAgainstTriangle() {
		fail("Not yet implemented");
	}

	@Test
	public void testMove() {
		fail("Not yet implemented");
	}

}
