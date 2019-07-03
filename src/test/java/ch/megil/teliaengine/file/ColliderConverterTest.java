package ch.megil.teliaengine.file;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
	public void testConvertToColliderRectangle() {
		var colliderStr = "rectangle:5.0:10.0:20.0:30.0";
		
		var collider = colliderConverter.convertToCollider(colliderStr);
		assertThat(collider, instanceOf(RectangleCollider.class));
		
		var colliderTyped = (RectangleCollider)collider;
		assertEquals(5.0, colliderTyped.getBoundingBoxBegin().getX(), 0.0);
		assertEquals(10.0, colliderTyped.getBoundingBoxBegin().getY(), 0.0);
		assertEquals(25.0, colliderTyped.getBoundingBoxEnd().getX(), 0.0);
		assertEquals(40.0, colliderTyped.getBoundingBoxEnd().getY(), 0.0);
	}
	
	@Test
	public void testConvertToColliderCircle() {
		var colliderStr = "circle:20.0:10.0:5.0";

		var collider = colliderConverter.convertToCollider(colliderStr);
		assertThat(collider, instanceOf(CircleCollider.class));

		var colliderTyped = (CircleCollider)collider;
		assertEquals(20.0, colliderTyped.getCenter().getX(), 0.0);
		assertEquals(10.0, colliderTyped.getCenter().getY(), 0.0);
		assertEquals(5.0, colliderTyped.getRadius(), 0.0);
	}
	
	@Test
	public void testConvertToColliderTriangle() {
		var colliderStr = "triangle:5.0:5.0:15.0:4.0:3.0:18.0";

		var collider = colliderConverter.convertToCollider(colliderStr);
		assertThat(collider, instanceOf(TriangleCollider.class));

		var colliderTyped = (TriangleCollider)collider;
		assertEquals(5.0, colliderTyped.getP0().getX(), 0.0);
		assertEquals(5.0, colliderTyped.getP0().getY(), 0.0);
		assertEquals(15.0, colliderTyped.getP1().getX(), 0.0);
		assertEquals(4.0, colliderTyped.getP1().getY(), 0.0);
		assertEquals(3.0, colliderTyped.getP2().getX(), 0.0);
		assertEquals(18.0, colliderTyped.getP2().getY(), 0.0);
	}
	
	@Test
	public void testConvertToColliderComposite() {
		var colliderStr = "composite:;rectangle:5.0:10.0:20.0:30.0;circle:20.0:10.0:5.0;triangle:5.0:5.0:15.0:4.0:3.0:18.0";

		var collider = colliderConverter.convertToCollider(colliderStr);
		assertThat(collider, instanceOf(CompositeCollider.class));
		
		var colliderTyped = (CompositeCollider)collider;
		assertEquals(3, colliderTyped.getColliders().size());
	}
	
	@Test
	public void testConvertToEntryStringRectangle() {
		Collider collider = new RectangleCollider(new Vector(5, 10), 20, 30);
		var res = colliderConverter.convertToEntryString(collider);
		assertEquals("rectangle:5.0:10.0:20.0:30.0", res);
	}
	
	@Test
	public void testConvertToEntryStringCircle() {
		Collider collider = new CircleCollider(new Vector(20, 10), 5);
		var res = colliderConverter.convertToEntryString(collider);
		assertEquals("circle:20.0:10.0:5.0", res);
	}
	
	@Test
	public void testConvertToEntryStringTriangle() {
		Collider collider = new TriangleCollider(new Vector(5, 5), new Vector(15, 4), new Vector(3, 18));
		var res = colliderConverter.convertToEntryString(collider);
		assertEquals("triangle:5.0:5.0:15.0:4.0:3.0:18.0", res);
	}
	
	@Test
	public void testConvertToEntryStringComposite() {
		var colRec = new RectangleCollider(new Vector(5, 10), 20, 30);
		var colCir = new CircleCollider(new Vector(20, 10), 5);
		var colTri = new TriangleCollider(new Vector(5, 5), new Vector(15, 4), new Vector(3, 18));
		Collider collider = new CompositeCollider(colRec, colCir, colTri);
		
		var compRes = colliderConverter.convertToEntryString(collider);
		assertTrue("Starts with composite", compRes.startsWith("composite:;"));
		assertTrue("Contains rectangle collider", compRes.contains("rectangle:5.0:10.0:20.0:30.0"));
		assertTrue("Contains circle collider", compRes.contains("circle:20.0:10.0:5.0"));
		assertTrue("Contains triangle collider", compRes.contains("triangle:5.0:5.0:15.0:4.0:3.0:18.0"));
		assertEquals(4, compRes.split(";").length);
	}
}
