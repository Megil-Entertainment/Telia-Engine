package ch.megil.teliaengine.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ch.megil.teliaengine.configuration.GameConfiguration;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameObjectSaveLoadTest {
	private static File parentDir = new File(GameConfiguration.ASSETS_OBJECTS.getConfiguration());

	@Rule
	public TemporaryFolder testMapsDir = new TemporaryFolder(parentDir);

	private GameObjectSaveLoad gameObjectSaveLoad;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
	}

	@Before
	public void setUp() throws Exception {
		gameObjectSaveLoad = new GameObjectSaveLoad();

		var red = testMapsDir.newFile("red.tobj");
		try (var writer = new BufferedWriter(new FileWriter(red))) {
			writer.write("50.0/60.0/FF0000");
		}
	}

	@Test
	public void testLoad() {
		var optObj = gameObjectSaveLoad.load(testMapsDir.getRoot().getName() + "/red");

		assertTrue(optObj.isPresent());
		var obj = optObj.get();

		assertEquals(testMapsDir.getRoot().getName() + "/red", obj.getName());
		assertEquals(0.0, obj.getPosX(), 0);
		assertEquals(0.0, obj.getPosY(), 0);
		assertEquals(50.0, ((Rectangle) obj.getDepiction()).getWidth(), 0);
		assertEquals(60.0, ((Rectangle) obj.getDepiction()).getHeight(), 0);
		assertEquals(Color.RED, ((Rectangle) obj.getDepiction()).getFill());
	}
}
