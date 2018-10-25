package ch.megil.teliaengine.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.logging.LogHandler;

public class MapSaveLoad {
	private void checkAndCreateDirectory() {
		var dir = new File(GameConfiguration.ASSETS_MAPS.getConfiguration());
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	
	public void save(Map map) {
		checkAndCreateDirectory();
		var fileName = GameConfiguration.ASSETS_MAPS.getConfiguration() + "/" + map.getName() + GameConfiguration.FILE_EXT_MAP.getConfiguration();
		
		var propSeperator = GameConfiguration.SEPERATOR_PROPERTY.getConfiguration();
		var entrySeperator = GameConfiguration.SEPARATOR_ENTRY.getConfiguration();
		
		try (var writer = new BufferedWriter(new FileWriter(fileName))) {
			writer.write(map.getWidth() + propSeperator + map.getHeight() + entrySeperator);
			writer.write(Player.get().getPosX() + propSeperator + Player.get().getPosY() + entrySeperator);
			for (var o : map.getMapObjects()) {
				writer.write(o.getName() + propSeperator + o.getPosX() + propSeperator + o.getPosY() + entrySeperator);
			}
		} catch (IOException e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	public Map load(String mapName, boolean recoverMode) throws AssetNotFoundException, AssetFormatException {
		var fileName = GameConfiguration.ASSETS_MAPS.getConfiguration() + "/" + mapName
				+ GameConfiguration.FILE_EXT_MAP.getConfiguration();
		var file = new File(fileName);

		try (var scanner = new Scanner(file)) {
			scanner.useDelimiter(GameConfiguration.SEPARATOR_ENTRY.getConfiguration());

			var mapSize = scanner.next().split(GameConfiguration.SEPERATOR_PROPERTY.getConfiguration());
			var playerPos = scanner.next().split(GameConfiguration.SEPERATOR_PROPERTY.getConfiguration());
			
			var player = Player.get();
			player.setPosX(Double.parseDouble(playerPos[0]));
			player.setPosY(Double.parseDouble(playerPos[1]));
			
			var map = new Map(mapName, Double.parseDouble(mapSize[0]), Double.parseDouble(mapSize[1]));

			var objectLoader = new GameObjectSaveLoad();
			while (scanner.hasNext()) {
				var objSpec = scanner.next().split(GameConfiguration.SEPERATOR_PROPERTY.getConfiguration());
				try {
					var obj = objectLoader.load(objSpec[0]);
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
