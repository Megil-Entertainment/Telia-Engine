package ch.megil.teliaengine.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.collision.CircleCollider;
import ch.megil.teliaengine.physics.collision.Collider;
import ch.megil.teliaengine.physics.collision.CompositeCollider;
import ch.megil.teliaengine.physics.collision.RectangleCollider;
import ch.megil.teliaengine.physics.collision.TriangleCollider;

public class ColliderConverterTest {
	private ColliderConverter colliderConverter;

	@Before
	public void setUp() throws Exception {
		colliderConverter = new ColliderConverter();
	}

	@Test
	public void testConvertToCollider() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testConvertToEntryString() {
		var colRec = new RectangleCollider(new Vector(5, 10), 20, 30);
		var colCir = new CircleCollider(new Vector(20, 10), 5);
		var colTri = new TriangleCollider(new Vector(5, 5), new Vector(15, 4), new Vector(3, 18));
		var colComp = new CompositeCollider(colRec, colCir, colTri);
		
		assertEquals("rectangle:5.0:10.0:20.0:30.0", colliderConverter.convertToEntryString(colRec));
		assertEquals("circle:20.0:10.0:5.0", colliderConverter.convertToEntryString(colCir));
		assertEquals("triangle:5.0:5.0:15.0:4.0:3.0:18.0", colliderConverter.convertToEntryString(colTri));
		
		var compRes = colliderConverter.convertToEntryString(colComp);
		assertTrue("Starts with composite", compRes.startsWith("composite:;"));
		assertTrue("Contains rectangle collider", compRes.contains("rectangle:5.0:10.0:20.0:30.0"));
		assertTrue("Contains circle collider", compRes.contains("circle:20.0:10.0:5.0"));
		assertTrue("Contains triangle collider", compRes.contains("triangle:5.0:5.0:15.0:4.0:3.0:18.0"));
		assertEquals(4, compRes.split(";").length);
	}
}
