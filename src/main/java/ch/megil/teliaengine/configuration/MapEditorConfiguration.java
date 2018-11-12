package ch.megil.teliaengine.configuration;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;

import ch.megil.teliaengine.logging.LogHandler;

public enum MapEditorConfiguration {
	GRID_WIDTH("gridWidth"),
	GRID_HEIGHT("gridHeight");
	
	private static Properties mapEditProp;
	
	static {
		mapEditProp = new XProperties();

		try (var in = new FileInputStream(ConfigurationContstants.GAME_CONFIGURATION)) {
			mapEditProp.load(in);
		} catch (Exception e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	private String key;
	
	private MapEditorConfiguration(String key) {
		this.key = key;
	}
	
	public String getConfiguration() {
		return mapEditProp.getProperty(key);
	}
}
