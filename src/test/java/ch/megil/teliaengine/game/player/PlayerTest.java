package ch.megil.teliaengine.game.player;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import ch.megil.teliaengine.game.Hitbox;
import ch.megil.teliaengine.game.Vector;
import javafx.scene.image.Image;

public class PlayerTest {
	
	@Mock
	private Image depiction;
	private Hitbox playerHitbox;
	
	private List<Hitbox> collision;
	
	@Before
	public void setUp() throws Exception {
		depiction = mock(Image.class);
		playerHitbox = new Hitbox(Vector.ZERO, 50, 50);
		
		collision = new ArrayList<>();
		collision.add(new Hitbox(new Vector(87, 0), 100, 50));
	}

	@Test
	public void testUpdate() {
		var player = new Player(depiction, playerHitbox);
		
		player.applyAcceleration(new Vector(5, 3));
		player.update(collision);
		assertEquals(5, player.getPosition().getX(), 0);
		assertEquals(3, player.getPosition().getY(), 0);
		
		player.update(collision);
		assertEquals(10, player.getPosition().getX(), 0);
		assertEquals(6, player.getPosition().getY(), 0);
		
		player.applyAcceleration(new Vector(5, 0));
		player.update(collision);
		assertEquals(20, player.getPosition().getX(), 0);
		assertEquals(9, player.getPosition().getY(), 0);
		
		player.applyAcceleration(new Vector(-10, -3));
		player.update(collision);
		assertEquals(20, player.getPosition().getX(), 0);
		assertEquals(9, player.getPosition().getY(), 0);
		
		player.applyVelocity(new Vector(5, 3));
		player.update(collision);
		assertEquals(25, player.getPosition().getX(), 0);
		assertEquals(12, player.getPosition().getY(), 0);

		player.update(collision);
		assertEquals(25, player.getPosition().getX(), 0);
		assertEquals(12, player.getPosition().getY(), 0);
		
		player.applyAcceleration(new Vector(5, 3));
		player.update(collision);
		assertEquals(30, player.getPosition().getX(), 0);
		assertEquals(15, player.getPosition().getY(), 0);

		player.update(collision);
		assertEquals(35, player.getPosition().getX(), 0);
		assertEquals(18, player.getPosition().getY(), 0);

		player.update(collision);
		assertEquals(37, player.getPosition().getX(), 0);
		assertEquals(21, player.getPosition().getY(), 0);
	}
}
