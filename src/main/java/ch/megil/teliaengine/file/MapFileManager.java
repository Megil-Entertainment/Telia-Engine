package ch.megil.teliaengine.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.configuration.ProjectFolderConfiguration;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.logging.LogHandler;

public class MapFileManager {
	private void checkAndCreateDirectory() {
		var dir = new File(ProjectFolderConfiguration.ASSETS_MAPS.getConfigurationWithProjectPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	
	public void save(Map map, Player player) {
		checkAndCreateDirectory();
		var fileName = ProjectFolderConfiguration.ASSETS_MAPS.getConfigurationWithProjectPath() + "/" + map.getName() + FileConfiguration.FILE_EXT_MAP.getConfiguration();
		
		var propSeperator = FileConfiguration.SEPERATOR_PROPERTY.getConfiguration();
		var entrySeperator = FileConfiguration.SEPARATOR_ENTRY.getConfiguration();
		
		try (var writer = new BufferedWriter(new FileWriter(fileName))) {
			writer.write(map.getWidth() + propSeperator + map.getHeight() + entrySeperator);
			writer.write(player.getPosition().getX() + propSeperator + player.getPosition().getY() + entrySeperator);
			for (var o : map.getMapObjects()) {
				writer.write(o.getName() + propSeperator + o.getPosition().getX() + propSeperator + o.getPosition().getY() + entrySeperator);
			}
		} catch (IOException e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	public Map load(String mapName, boolean recoverMode, Player player) throws AssetNotFoundException, AssetFormatException {
		var fileName = ProjectFolderConfiguration.ASSETS_MAPS.getConfigurationWithProjectPath() + "/" + mapName
				+ FileConfiguration.FILE_EXT_MAP.getConfiguration();
		var file = new File(fileName);

		try (var scanner = new Scanner(file)) {
			scanner.useDelimiter(FileConfiguration.SEPARATOR_ENTRY.getConfiguration());

			var mapSize = scanner.next().split(FileConfiguration.SEPERATOR_PROPERTY.getConfiguration());
			var playerPos = scanner.next().split(FileConfiguration.SEPERATOR_PROPERTY.getConfiguration());
			
			player.setPosX(Double.parseDouble(playerPos[0]));
			player.setPosY(Double.parseDouble(playerPos[1]));
			
			var map = new Map(mapName, Double.parseDouble(mapSize[0]), Double.parseDouble(mapSize[1]));

			var objectFileManager = new GameObjectFileManager();
			while (scanner.hasNext()) {
				var objSpec = scanner.next().split(FileConfiguration.SEPERATOR_PROPERTY.getConfiguration());
				try {
					var obj = objectFileManager.load(objSpec[0]);
					obj.setPosX(Double.parseDouble(objSpec[1]));
					obj.setPosY(Double.parseDouble(objSpec[2]));
					map.addObject(obj);
				} catch (AssetNotFoundException | AssetFormatException
						| ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
					LogHandler.warning(
							"There was a problem with loading Game Object " + objSpec[0] + " inside Map: " + mapName);
					if (recoverMode) {
						LogHandler.log(e, Level.WARNING);
					} else {
						throw e;
					}
				}
			}

			return map;
		} catch (IOException e) {
			throw new AssetNotFoundException("Map not found: " + mapName, e);
		} catch (NoSuchElementException | ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
			throw new AssetFormatException("Map not correctly formated: " + mapName, e);
		}
	}
}
