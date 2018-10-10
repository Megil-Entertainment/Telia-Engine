package ch.megil.teliaengine.file;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.game.Map;

public class MapSaveLoadTest {
	private static File parentDir = new File(GameConfiguration.ASSETS_MAPS.getConfiguration());
	
	@Rule
	public TemporaryFolder testMapsDir = new TemporaryFolder(parentDir);

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
	}

	@Test
	public void testSave() throws IOException {
		var mapName = testMapsDir.getRoot().getName() + "/testSave";
		mapSaveLoad.save(testMap, mapName);
		var file = testMapsDir.getRoot().listFiles((f, n) -> n.startsWith("testSave."))[0];

		try (var reader = new BufferedReader(new FileReader(file))) {
			assertEquals("150.0/100.0", reader.readLine());
			assertEquals("20.0/80.0", reader.readLine());
			assertEquals("red/10.0/20.0", reader.readLine());
			assertEquals("blue/100.0/50.0", reader.readLine());
		}
	}

}
