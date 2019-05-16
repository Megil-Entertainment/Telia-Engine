package ch.megil.teliaengine.file;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.game.Vector;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.project.ProjectController;

public class MapFileManagerTest {
	private static File parentDir = new File(".");
	
	@Rule
	public TemporaryFolder testProjectDir = new TemporaryFolder(parentDir);

	@Mock
	private static GameObject obj1;
	@Mock
	private static GameObject obj2;
	@Mock
	private static Map testMap;
	@Mock
	private static Player player;
	
	private static Vector vector1;
	private static Vector vector2;
	private static Vector playerVector;

	private MapFileManager mapFileManager;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		
		obj1 = mock(GameObject.class);
		vector1 = new Vector(10.0, 20.0);
		when(obj1.getName()).thenReturn("red");
		when(obj1.getPosition()).thenReturn(vector1);

		obj2 = mock(GameObject.class);
		vector2 = new Vector(100.0, 50.0);
		when(obj2.getName()).thenReturn("blue");
		when(obj2.getPosition()).thenReturn(vector2);

		testMap = mock(Map.class);
		when(testMap.getWidth()).thenReturn(150.0);
		when(testMap.getHeight()).thenReturn(100.0);
		when(testMap.getMapObjects()).thenReturn(Arrays.asList(obj1, obj2));
		
		player = mock(Player.class);
		playerVector = new Vector(20.0, 80.0);
		when(player.getPosition()).thenReturn(playerVector);
	}
	
	@Before
	public void setUp() throws Exception {
		mapFileManager = new MapFileManager();
		
		//create folders
		testProjectDir.newFolder("assets");
		testProjectDir.newFolder("assets", "maps");
		testProjectDir.newFolder("assets", "object");
		testProjectDir.newFolder("assets", "texture");
		
		//create player
		var player = testProjectDir.newFile("assets/player.tobj");
		try (var writer = new BufferedWriter(new FileWriter(player))) {
			writer.write("10.0:10.0:player:#000000");
		}
		testProjectDir.newFile("assets/texture/player.png");
		
		//create texture
		var textureFile = testProjectDir.newFile("assets/texture/tex.png");
		var texture = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
		ImageIO.write(texture, "PNG", textureFile);
		
		//create objects
		var redObj = testProjectDir.newFile("assets/object/red.tobj");
		try (var writer = new BufferedWriter(new FileWriter(redObj))) {
			writer.write("50.0:60.0:tex:#FF0000");
		}
		
		var failObj = testProjectDir.newFile("assets/object/fail.tobj");
		try (var writer = new BufferedWriter(new FileWriter(failObj))) {
			writer.write("50.0:60.0");
		}
		
		//create maps
		var correct = testProjectDir.newFile("assets/maps/correct.tmap");
		try (var writer = new BufferedWriter(new FileWriter(correct))) {
			writer.write( "100.0:70.0\n"
						+ "15.0:10.0\n"
						+ "red:50.0:30.0\n"
						+ "red:40.0:30.0");
		}
		
		var failOnMap = testProjectDir.newFile("assets/maps/failOnMap.tmap");
		try (var writer = new BufferedWriter(new FileWriter(failOnMap))) {
			writer.write( "100.0:70.0\n"
						+ "15.0:10.0\n"
						+ "red:50.0\n"
						+ "red:20.0:10.0\n");
		}
		
		var failOnObj = testProjectDir.newFile("assets/maps/failOnObj.tmap");
		try (var writer = new BufferedWriter(new FileWriter(failOnObj))) {
			writer.write( "100.0:70.0\n"
						+ "15.0:10.0\n"
						+ "fail:50.0:30.0");
		}
		
		var missingObject = testProjectDir.newFile("assets/maps/missingObject.tmap");
		try (var writer = new BufferedWriter(new FileWriter(missingObject))) {
			writer.write( "100.0:70.0\n"
						+ "15.0:10.0\n"
						+ "test:50.0:30.0");
		}
		
		var recover = testProjectDir.newFile("assets/maps/recover.tmap");
		try (var writer = new BufferedWriter(new FileWriter(recover))) {
			writer.write( "100.0:70.0\n"
						+ "15.0:10.0\n"
						+ "red:10.0:40.0\n"
						+ "test:50.0:20.0\n"
						+ "red:20.0:40.0\n"
						+ "red:70.0:40.0\n"
						+ "fail:50.0:30.0\n"
						+ "red:40.0:30.0");
		}
		
		//create info and load project
		var projectInfo = testProjectDir.newFile("test.teliaproject");
		var project = new ProjectFileManager().loadProject(projectInfo);
		ProjectController.get().openProject(project);
	}

	@Test
	public void testSave() throws Exception {
		when(testMap.getName()).thenReturn("testSave");
		
		mapFileManager.save(testMap,player);
		var file = new File(testProjectDir.getRoot().getName() + "/assets/maps/testSave.tmap");

		try (var reader = new BufferedReader(new FileReader(file))) {
			assertEquals("150.0:100.0", reader.readLine());
			assertEquals("20.0:80.0", reader.readLine());
			assertEquals("red:10.0:20.0", reader.readLine());
			assertEquals("blue:100.0:50.0", reader.readLine());
		}
	}

	@Test
	public void testLoad() throws Exception {
		var mapName = "correct";
		var map = mapFileManager.load(mapName, false);

		assertEquals(mapName, map.getName());
		assertEquals(100.0, map.getWidth(), 0);
		assertEquals(70.0, map.getHeight(), 0);
		assertEquals(15.0, Player.get().getPosition().getX(), 0);
		assertEquals(10.0, Player.get().getPosition().getY(), 0);

		assertEquals(2, map.getMapObjects().size());
	}

	@Test(expected = AssetNotFoundException.class)
	public void testLoadNotExisting() throws Exception {
		var mapName = "nonExisting";
		mapFileManager.load(mapName, false);
	}

	@Test(expected = AssetFormatException.class)
	public void testLoadFalseFormat() throws Exception {
		var mapName = "failOnMap";
		mapFileManager.load(mapName, false);
	}

	@Test(expected = AssetFormatException.class)
	public void testLoadFalseFormatInObject() throws Exception {
		var mapName = "failOnObj";
		mapFileManager.load(mapName, false);
	}

	@Test(expected = AssetNotFoundException.class)
	public void testLoadMissingObject() throws Exception {
		var mapName = "missingObject";
		mapFileManager.load(mapName, false);
	}

	@Test
	public void testLoadRecoverMode() throws Exception {
		var mapName = "recover";
		var map = mapFileManager.load(mapName, true);

		assertEquals(mapName, map.getName());
		assertEquals(100.0, map.getWidth(), 0);
		assertEquals(70.0, map.getHeight(), 0);
		assertEquals(15.0, Player.get().getPosition().getX(), 0);
		assertEquals(10.0, Player.get().getPosition().getY(), 0);

		assertEquals(4, map.getMapObjects().size());
	}
}
