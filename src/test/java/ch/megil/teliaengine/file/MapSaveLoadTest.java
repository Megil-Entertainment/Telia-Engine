package ch.megil.teliaengine.file;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.game.Map;

public class MapSaveLoadTest {
	private static File parentDir = new File(GameConfiguration.ASSETS_MAPS.getConfiguration());
	private static File objParentDir = new File(GameConfiguration.ASSETS_OBJECTS.getConfiguration());
	
	@Rule
	public TemporaryFolder testMapsDir = new TemporaryFolder(parentDir);
	@Rule
	public TemporaryFolder testObjectDir = new TemporaryFolder(objParentDir);

	@Mock
	private static GameObject obj1;
	@Mock
	private static GameObject obj2;
	@Mock
	private static Map testMap;

	private MapSaveLoad mapSaveLoad;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		if (!objParentDir.exists()) {
			objParentDir.mkdirs();
		}
		
		obj1 = mock(GameObject.class);
		when(obj1.getName()).thenReturn("red");
		when(obj1.getPosX()).thenReturn(10.0);
		when(obj1.getPosY()).thenReturn(20.0);

		obj2 = mock(GameObject.class);
		when(obj2.getName()).thenReturn("blue");
		when(obj2.getPosX()).thenReturn(100.0);
		when(obj2.getPosY()).thenReturn(50.0);

		testMap = mock(Map.class);
		when(testMap.getWidth()).thenReturn(150.0);
		when(testMap.getHeight()).thenReturn(100.0);
		when(testMap.getPlayerX()).thenReturn(20.0);
		when(testMap.getPlayerY()).thenReturn(80.0);
		when(testMap.getMapObjects()).thenReturn(Arrays.asList(obj1, obj2));
	}
	
	@Before
	public void setUp() throws Exception {
		mapSaveLoad = new MapSaveLoad();
		
		var redObj = testObjectDir.newFile("red.tobj");
		try (var writer = new BufferedWriter(new FileWriter(redObj))) {
			writer.write("50.0:60.0:FF0000");
		}
		
		var failObj = testObjectDir.newFile("fail.tobj");
		try (var writer = new BufferedWriter(new FileWriter(failObj))) {
			writer.write("50.0:60.0");
		}
		
		var correct = testMapsDir.newFile("correct.tmap");
		try (var writer = new BufferedWriter(new FileWriter(correct))) {
			writer.write( "100.0:70.0\n"
						+ "15.0:10.0\n"
						+ testObjectDir.getRoot().getName() + "/red:50.0:30.0\n"
						+ testObjectDir.getRoot().getName() + "/red:40.0:30.0");
		}
		
		var failOnMap = testMapsDir.newFile("failOnMap.tmap");
		try (var writer = new BufferedWriter(new FileWriter(failOnMap))) {
			writer.write( "100.0:70.0\n"
						+ "15.0:10.0\n"
						+ testObjectDir.getRoot().getName() + "/red:50.0\n"
						+ testObjectDir.getRoot().getName() + "/red:20.0:10.0\n");
		}
		
		var failOnObj = testMapsDir.newFile("failOnObj.tmap");
		try (var writer = new BufferedWriter(new FileWriter(failOnObj))) {
			writer.write( "100.0:70.0\n"
						+ "15.0:10.0\n"
						+ testObjectDir.getRoot().getName() + "/fail:50.0:30.0");
		}
		
		var missingObject = testMapsDir.newFile("missingObject.tmap");
		try (var writer = new BufferedWriter(new FileWriter(missingObject))) {
			writer.write( "100.0:70.0\n"
						+ "15.0:10.0\n"
						+ testObjectDir.getRoot().getName() + "/test:50.0:30.0");
		}
		
		var recover = testMapsDir.newFile("recover.tmap");
		try (var writer = new BufferedWriter(new FileWriter(recover))) {
			writer.write( "100.0:70.0\n"
						+ "15.0:10.0\n"
						+ testObjectDir.getRoot().getName() + "/red:10.0:40.0\n"
						+ testObjectDir.getRoot().getName() + "/test:50.0:20.0\n"
						+ testObjectDir.getRoot().getName() + "/red:20.0:40.0\n"
						+ testObjectDir.getRoot().getName() + "/red:70.0:40.0\n"
						+ testObjectDir.getRoot().getName() + "/fail:50.0:30.0\n"
						+ testObjectDir.getRoot().getName() + "/red:40.0:30.0");
		}
	}

	@Test
	public void testSave() throws Exception {
		var mapName = testMapsDir.getRoot().getName() + "/testSave";
		mapSaveLoad.save(testMap, mapName);
		var file = testMapsDir.getRoot().listFiles((f, n) -> n.startsWith("testSave."))[0];

		try (var reader = new BufferedReader(new FileReader(file))) {
			assertEquals("150.0:100.0", reader.readLine());
			assertEquals("20.0:80.0", reader.readLine());
			assertEquals("red:10.0:20.0", reader.readLine());
			assertEquals("blue:100.0:50.0", reader.readLine());
		}
	}
	
	@Test
	public void testLoad() throws Exception {
		var mapName = testMapsDir.getRoot().getName() + "/correct";
		var map = mapSaveLoad.load(mapName, false);
		
		assertEquals(100.0, map.getWidth(), 0);
		assertEquals(70.0, map.getHeight(), 0);
		assertEquals(15.0, map.getPlayerX(), 0);
		assertEquals(10.0, map.getPlayerY(), 0);
		
		assertEquals(2, map.getMapObjects().size());
	}
	
	@Test(expected = AssetNotFoundException.class)
	public void testLoadNotExisting() throws Exception {
		var mapName = testMapsDir.getRoot().getName() + "/nonExisting";
		mapSaveLoad.load(mapName, false);
	}
	
	@Test(expected = AssetFormatException.class)
	public void testLoadFalseFormat() throws Exception {
		var mapName = testMapsDir.getRoot().getName() + "/failOnMap";
		mapSaveLoad.load(mapName, false);
	}
	
	@Test(expected = AssetFormatException.class)
	public void testLoadFalseFormatInObject() throws Exception {
		var mapName = testMapsDir.getRoot().getName() + "/failOnObj";
		mapSaveLoad.load(mapName, false);
	}
	
	@Test(expected = AssetNotFoundException.class)
	public void testLoadMissingObject() throws Exception {
		var mapName = testMapsDir.getRoot().getName() + "/missingObject";
		mapSaveLoad.load(mapName, false);
	}
	
	@Test
	public void testLoadRecoverMode() throws Exception {
		var mapName = testMapsDir.getRoot().getName() + "/recover";
		var map = mapSaveLoad.load(mapName, true);
		
		assertEquals(100.0, map.getWidth(), 0);
		assertEquals(70.0, map.getHeight(), 0);
		assertEquals(15.0, map.getPlayerX(), 0);
		assertEquals(10.0, map.getPlayerY(), 0);
		
		assertEquals(4, map.getMapObjects().size());
	}
}
