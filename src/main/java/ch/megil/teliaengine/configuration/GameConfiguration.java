package ch.megil.teliaengine.configuration;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;

import ch.megil.teliaengine.configuration.data.GameConfigData;
import ch.megil.teliaengine.logging.LogHandler;
import ch.megil.teliaengine.project.ProjectController;

public enum GameConfiguration {
	MAP_WIDTH("mapWidth"),
	MAP_HEIGHT("mapHeight"),
	MAP_GRID_WIDTH("mapGridWidth"),
	MAP_GRID_HEIGHT("mapGridHeight");
	
	private static Properties gameProp;
	
	static {
		reload();
	}
	
	public static void reload() {
		gameProp = new XProperties();
		try (var in = new FileInputStream(ProjectController.get().getProjectPath() + ConfigurationContstants.GAME_CONFIGURATION)) {
			gameProp.load(in);
		} catch (Exception e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	public static void writeDataToProperties(Properties prop, GameConfigData data) {
		prop.setProperty(MAP_WIDTH.key, Double.toString(data.getMapWidth()));
		prop.setProperty(MAP_HEIGHT.key, Double.toString(data.getMapHeight()));
	}
	
	private String key;
	
	private GameConfiguration(String key) {
		this.key = key;
	}
	
	public String getConfiguration() {
		return gameProp.getProperty(key);
	}
}
