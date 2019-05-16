package ch.megil.teliaengine.game;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.collision.Collider;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class GameObjectTest {
	@Mock
	private Image depiction;
	private Collider hitbox;
	private Color color;

	@Before
	public void setUp() throws Exception {
		depiction = mock(Image.class);
		hitbox = new Collider(Vector.ZERO, 50, 50);
		color = Color.BLACK;
	}

	@Test
	public void testGameObjectStringNode() {
		var obj = new GameObject("testObject", "depiction", depiction, hitbox, color);

		assertEquals(depiction, obj.getDepiction());
		assertEquals(0, obj.getPosition().getX(), 0);
		assertEquals(0, obj.getPosition().getY(), 0);
	}

	@Test
	public void testGameObjectStringNodeDoubleDouble() {
		var obj = new GameObject("testObject", "depiction", depiction, hitbox, color, 30, 40);

		assertEquals(depiction, obj.getDepiction());
		assertEquals(30, obj.getPosition().getX(), 0);
		assertEquals(40, obj.getPosition().getY(), 0);
		assertEquals(30, obj.getHitbox().getOrigin().getX(), 0);
		assertEquals(40, obj.getHitbox().getOrigin().getY(), 0);
	}
}
