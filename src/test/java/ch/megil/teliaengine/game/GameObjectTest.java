package ch.megil.teliaengine.game;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

public class GameObjectTest {
	private Node depiction;

	@Before
	public void setUp() throws Exception {
		depiction = new Rectangle(50, 50);
	}

	@Test
	public void testGameObjectStringNode() {
		depiction.setLayoutX(10);
		depiction.setLayoutY(20);

		var obj = new GameObject("testObject", depiction);

		assertEquals(depiction, obj.getDepiction());
		assertEquals(10, obj.getPosX(), 0);
		assertEquals(20, obj.getPosY(), 0);
	}

	@Test
	public void testGameObjectStringNodeDoubleDouble() {
		var obj = new GameObject("testObject", depiction, 30, 40);


		assertEquals(depiction, obj.getDepiction());
		assertEquals(30, obj.getPosX(), 0);
		assertEquals(40, obj.getPosY(), 0);
		assertEquals(30, depiction.getLayoutX(), 0);
		assertEquals(40, depiction.getLayoutY(), 0);
	}

	@Test
	public void testPositionBinding() {
		var obj = new GameObject("testObject", depiction);

		assertEquals(0, obj.getPosX(), 0);
		assertEquals(0, obj.getPosY(), 0);

		depiction.setLayoutX(5);
		depiction.setLayoutY(15);

		assertEquals(5, obj.getPosX(), 0);
		assertEquals(15, obj.getPosY(), 0);
		
		obj.setPosX(15);
		obj.setPosY(5);

		assertEquals(15, depiction.getLayoutX(), 0);
		assertEquals(5, depiction.getLayoutY(), 0);
	}
}
