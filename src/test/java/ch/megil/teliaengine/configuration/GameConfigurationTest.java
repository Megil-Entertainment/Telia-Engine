package ch.megil.teliaengine.configuration;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import ch.megil.teliaengine.configuration.data.GameConfigData;

public class GameConfigurationTest {
	private static final double MAX_ALLOWED_ERROR = 0.0001;
	
	private GameConfigData gameConfigData;
	
	@Before
	public void setUp() throws Exception {
		gameConfigData = new GameConfigData();
		gameConfigData.setMapWidth(1280);
		gameConfigData.setMapHeight(720);
		gameConfigData.setMapGridWidth(10);
		gameConfigData.setMapGridHeight(10);
	}
	
	@Test
	public void testWriteDataToProperties() {
		var props = new Properties();
		GameConfiguration.writeDataToProperties(props, gameConfigData);
		
		assertEquals("mapWidth", 1280, Double.parseDouble(props.getProperty("mapWidth")), MAX_ALLOWED_ERROR);
		assertEquals("mapHeight", 720, Double.parseDouble(props.getProperty("mapHeight")), MAX_ALLOWED_ERROR);
		assertEquals("mapGridWidth", 10, Double.parseDouble(props.getProperty("mapGridWidth")), MAX_ALLOWED_ERROR);
		assertEquals("mapGridHeight", 10, Double.parseDouble(props.getProperty("mapGridHeight")), MAX_ALLOWED_ERROR);
	}
}
