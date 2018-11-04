package ch.megil.teliaengine.file;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameObjectSaveLoadTest {
	private static File parentDir = new File(GameConfiguration.ASSETS_OBJECTS.getConfiguration());

	@Rule
	public TemporaryFolder testObjectDir = new TemporaryFolder(parentDir);

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

		var red = testObjectDir.newFile("red.tobj");
		try (var writer = new BufferedWriter(new FileWriter(red))) {
			writer.write("50.0:60.0:red");
		}
		
		var fail = testObjectDir.newFile("fail.tobj");
		try (var writer = new BufferedWriter(new FileWriter(fail))) {
			writer.write("50.0:60.0");
		}
	}

	@Test
	public void testLoad() throws Exception {
		var obj = gameObjectSaveLoad.load(testObjectDir.getRoot().getName() + "/red");

		assertEquals(testObjectDir.getRoot().getName() + "/red", obj.getName());
		assertEquals(0.0, obj.getPosX(), 0);
		assertEquals(0.0, obj.getPosY(), 0);
		assertEquals(50.0, (obj.getDepiction()).getWidth(), 0);
		assertEquals(60.0, (obj.getDepiction()).getHeight(), 0);
	}
	
	@Test(expected = AssetNotFoundException.class)
	public void testLoadNotExisting() throws Exception {
		gameObjectSaveLoad.load(testObjectDir.getRoot().getName() + "/nonExisting");
	}
	
	@Test(expected = AssetFormatException.class)
	public void testLoadFalseFormat() throws Exception {
		gameObjectSaveLoad.load(testObjectDir.getRoot().getName() + "/fail");
	}
}
