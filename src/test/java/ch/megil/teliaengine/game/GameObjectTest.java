package ch.megil.teliaengine.game;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

public class GameObjectTest {
	private Node depiction;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		depiction = new Rectangle(50, 50);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGameObjectStringNode() {
		depiction.setLayoutX(10);
		depiction.setLayoutY(20);
		
		var obj = new GameObject("testObject", depiction);
		
		assertEquals(10, obj.getPosX(), 0);
		assertEquals(20, obj.getPosY(), 0);
	}

	@Test
	public void testGameObjectStringNodeDoubleDouble() {
		var obj = new GameObject("testObject", depiction, 30, 40);
		
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
	}
}
