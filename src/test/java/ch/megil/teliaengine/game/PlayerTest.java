package ch.megil.teliaengine.game;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

public class PlayerTest {
	private Node depiction;
	
	@Before
	public void setUp() throws Exception {
		depiction = new Rectangle(50, 50);
	}

	@Test
	public void testPlayer() {
		depiction.setLayoutX(10);
		depiction.setLayoutY(20);

		var player = new Player(depiction, 5, 15);

		assertEquals(5, player.getPosX(), 0);
		assertEquals(15, player.getPosY(), 0);
	}
	
	@Test
	public void testPositionBinding() {
		var player = new Player(depiction, 0, 0);

		assertEquals(0, player.getPosX(), 0);
		assertEquals(0, player.getPosY(), 0);

		depiction.setLayoutX(5);
		depiction.setLayoutY(15);

		assertEquals(5, player.getPosX(), 0);
		assertEquals(15, player.getPosY(), 0);
		
		player.setPosX(15);
		player.setPosY(5);

		assertEquals(15, depiction.getLayoutX(), 0);
		assertEquals(5, depiction.getLayoutY(), 0);
	}
}
