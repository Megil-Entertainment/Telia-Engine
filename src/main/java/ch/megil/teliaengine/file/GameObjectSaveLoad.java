package ch.megil.teliaengine.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.logging.LogHandler;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameObjectSaveLoad {
	public GameObject load(String name) throws AssetNotFoundException, AssetFormatException {
		var fileName = GameConfiguration.ASSETS_OBJECTS.getConfiguration() + "/" + name + GameConfiguration.FILE_EXT_OBJECT.getConfiguration();
		var file = new File(fileName);
		
		try (var reader = new BufferedReader(new FileReader(file))) {
			var spec = reader.readLine().split(GameConfiguration.SEPERATOR_PROPERTY.getConfiguration());
			var depiction =  new Rectangle(Double.parseDouble(spec[0]), Double.parseDouble(spec[1]), Color.web(spec[2]));
			
			var obj = new GameObject(name, depiction);
			
			return obj;
		} catch (IOException e) {
//			LogHandler.severe("Game Object not found.");
//			LogHandler.log(e, Level.SEVERE);
			throw new AssetNotFoundException("Game Object not found: " + name, e);
		} catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
//			LogHandler.severe("Game Object not correctly formated.");
//			LogHandler.log(e, Level.SEVERE);
			throw new AssetFormatException("Game Object not correctly formated: " + name, e);
		}
	}
	
	public List<GameObject> loadAll() {
		List<GameObject> res = new ArrayList<>();
		
		var path = new File(GameConfiguration.ASSETS_OBJECTS.getConfiguration());
		
		for (var file : path.list()) {
			var name = file.split(GameConfiguration.FILE_EXT_OBJECT.getConfiguration())[0];
			try {
				res.add(load(name));
			} catch (AssetNotFoundException | AssetFormatException e) {
				LogHandler.log(e, Level.SEVERE);
			}
		}
		
		return res;
	}
}
