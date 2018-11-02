package ch.megil.teliaengine.game.player;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ch.megil.teliaengine.game.Hitbox;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

public class PlayerTest {
	private Image depiction;
	private Hitbox hitbox;
	
	@Before
	public void setUp() throws Exception {
		depiction = new Image("https://picsum.photos/50/50", 50, 50, false, false);
		hitbox = new Hitbox(null, 50, 50);
	}

	@Test
	public void testPlayer() {
//		depiction.setLayoutX(10);
//		depiction.setLayoutY(20);

		var player = new Player(depiction, hitbox);
		player.setPosX(5);
		player.setPosY(15);

		assertEquals(depiction, player.getDepiction());
		assertEquals(5, player.getPosX(), 0);
		assertEquals(15, player.getPosY(), 0);
//		assertEquals(5, depiction.getLayoutX(), 0);
//		assertEquals(15, depiction.getLayoutY(), 0);
	}
	
	@Test
	public void testPositionBinding() {
		var player = new Player(depiction, hitbox);
		player.setPosX(0);
		player.setPosY(0);

		assertEquals(0, player.getPosX(), 0);
		assertEquals(0, player.getPosY(), 0);

//		depiction.setLayoutX(5);
//		depiction.setLayoutY(15);

		assertEquals(5, player.getPosX(), 0);
		assertEquals(15, player.getPosY(), 0);
		
		player.setPosX(15);
		player.setPosY(5);

//		assertEquals(15, depiction.getLayoutX(), 0);
//		assertEquals(5, depiction.getLayoutY(), 0);
	}
	
	@Test
	public void testHitbox() {
		var player = new Player(depiction, hitbox);
		
		player.setPosX(20);
		player.setPosY(30);
		
		assertEquals(20, player.getHitbox().getOrigin().getX(), 0);
		assertEquals(30, player.getHitbox().getOrigin().getY(), 0);
	}
}
