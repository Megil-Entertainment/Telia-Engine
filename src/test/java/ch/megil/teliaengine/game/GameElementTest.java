package ch.megil.teliaengine.game;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.collision.Collider;
import ch.megil.teliaengine.physics.collision.RectangleCollider;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class GameElementTest {
	private GameElement gameElement;
	private Vector vector;
	
	@Mock
	private Image image;
	
	private Collider hitbox;
	
	@Before
	public void setUp() {
		image = mock(Image.class);
		hitbox = new RectangleCollider(Vector.ZERO, 5, 5);
		
		gameElement = new GameElement("image",image,hitbox,Color.BLACK);
		vector = new Vector(2, 2);
	}
	
	@Test
	public void testPositions() {
		assertEquals(0, gameElement.getPosition().getX(),0);
		assertEquals(0, gameElement.getPosition().getY(),0);
		gameElement.setPosition(vector);
		assertEquals(vector, gameElement.getPosition());
		assertEquals(2, gameElement.getPosition().getX(),0);
		assertEquals(2, gameElement.getPosition().getY(),0);
		gameElement.setPosX(4);
		gameElement.setPosY(5);
		assertEquals(4, gameElement.getPosition().getX(),0);
		assertEquals(5, gameElement.getPosition().getY(),0);
		assertEquals(image, gameElement.getDepiction());
	}
	
	@Test
	public void testGameElementHitbox() {
		assertEquals(0, gameElement.getHitbox().getBoundingBoxBegin().getX(),0);
		assertEquals(0, gameElement.getHitbox().getBoundingBoxBegin().getY(),0);
		gameElement.setPosition(vector);
		assertEquals(2, gameElement.getHitbox().getBoundingBoxBegin().getX(),0);
		assertEquals(2, gameElement.getHitbox().getBoundingBoxBegin().getY(),0);
	}
	

}
