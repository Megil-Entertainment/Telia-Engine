package ch.megil.teliaengine.file;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.project.ProjectController;

public class GameObjectFileManagerTest {
	private static File parentDir = new File(".");

	@Rule
	public TemporaryFolder testProjectDir = new TemporaryFolder(parentDir);

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
		
		//create folders
		testProjectDir.newFolder("assets");
		testProjectDir.newFolder("assets", "object");
		testProjectDir.newFolder("assets", "texture");
		
		//create player
		var player = testProjectDir.newFile("assets/player.tobj");
		try (var writer = new BufferedWriter(new FileWriter(player))) {
			writer.write("10.0:10.0:player:#000000");
		}
		testProjectDir.newFile("assets/texture/player.png");
		
		//create objects
		var red = testProjectDir.newFile("assets/object/red.tobj");
		try (var writer = new BufferedWriter(new FileWriter(red))) {
			writer.write("50.0:60.0:red:#FF0000");
		}
		var redTexFile = testProjectDir.newFile("assets/texture/red.png");
		var redTexture = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
		ImageIO.write(redTexture, "PNG", redTexFile);
		
		var fail = testProjectDir.newFile("assets/object/fail.tobj");
		try (var writer = new BufferedWriter(new FileWriter(fail))) {
			writer.write("50.0:60.0");
		}
		
		//create info and load project
		var projectInfo = testProjectDir.newFile("test.teliaproject");
		var project = new ProjectFileManager().loadProject(projectInfo);
		ProjectController.get().openProject(project);
	}

	@Test
	public void testLoad() throws Exception {
		var obj = gameObjectFileManager.load("red");

		assertEquals("red", obj.getName());
		assertEquals(0.0, obj.getPosition().getX(), 0);
		assertEquals(0.0, obj.getPosition().getY(), 0);
		assertEquals(50.0, (obj.getDepiction()).getWidth(), 0);
		assertEquals(60.0, (obj.getDepiction()).getHeight(), 0);
	}
	
	@Test(expected = AssetNotFoundException.class)
	public void testLoadNotExisting() throws Exception {
		gameObjectFileManager.load("nonExisting");
	}
	
	@Test(expected = AssetFormatException.class)
	public void testLoadFalseFormat() throws Exception {
		gameObjectFileManager.load("fail");
	}
}
