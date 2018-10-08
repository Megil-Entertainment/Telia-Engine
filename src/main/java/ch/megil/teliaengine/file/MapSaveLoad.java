package ch.megil.teliaengine.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.logging.LogHandler;

public class MapSaveLoad {
	private void checkAndCreateDirectory() {
		var dir = new File(GameConfiguration.ASSETS_MAPS.getConfiguration());
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	
	public void save(Map map, String mapName) {
		checkAndCreateDirectory();
		var fileName = GameConfiguration.ASSETS_MAPS.getConfiguration() + "/" + mapName + GameConfiguration.FILE_EXT_MAP.getConfiguration();
		
		var propSeperator = GameConfiguration.SEPERATOR_PROPERTY.getConfiguration();
		var entrySeperator = GameConfiguration.SEPARATOR_ENTRY.getConfiguration();
		
		try (var writer = new BufferedWriter(new FileWriter(fileName))) {
			writer.write(map.getWidth() + propSeperator + map.getHeight() + entrySeperator);
			writer.write(map.getPlayerX() + propSeperator + map.getPlayerY() + entrySeperator);
			for (var o : map.getMapObjects()) {
				writer.write(o.getName() + propSeperator + o.getPosX() + propSeperator + o.getPosY() + entrySeperator);
			}
		} catch (IOException e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
}
