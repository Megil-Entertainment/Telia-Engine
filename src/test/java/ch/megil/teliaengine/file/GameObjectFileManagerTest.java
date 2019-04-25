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

import ch.megil.teliaengine.configuration.ProjectFolderConfiguration;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;

public class GameObjectFileManagerTest {
	private static File parentDir = new File(ProjectFolderConfiguration.ASSETS_OBJECTS.getConfiguration());

	@Rule
	public TemporaryFolder testObjectDir = new TemporaryFolder(parentDir);

	private GameObjectFileManager gameObjectFileManager;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
	}

	@Before
	public void setUp() throws Exception {
		gameObjectFileManager = new GameObjectFileManager();

		var red = testObjectDir.newFile("red.tobj");
		try (var writer = new BufferedWriter(new FileWriter(red))) {
			writer.write("50.0:60.0:red:#FF0000");
		}
		
		var fail = testObjectDir.newFile("fail.tobj");
		try (var writer = new BufferedWriter(new FileWriter(fail))) {
			writer.write("50.0:60.0");
		}
	}

	@Test
	public void testLoad() throws Exception {
		var obj = gameObjectFileManager.load(testObjectDir.getRoot().getName() + "/red");

		assertEquals(testObjectDir.getRoot().getName() + "/red", obj.getName());
		assertEquals(0.0, obj.getPosition().getX(), 0);
		assertEquals(0.0, obj.getPosition().getY(), 0);
		assertEquals(50.0, (obj.getDepiction()).getWidth(), 0);
		assertEquals(60.0, (obj.getDepiction()).getHeight(), 0);
	}
	
	@Test(expected = AssetNotFoundException.class)
	public void testLoadNotExisting() throws Exception {
		gameObjectFileManager.load(testObjectDir.getRoot().getName() + "/nonExisting");
	}
	
	@Test(expected = AssetFormatException.class)
	public void testLoadFalseFormat() throws Exception {
		gameObjectFileManager.load(testObjectDir.getRoot().getName() + "/fail");
	}
}
