package ch.megil.teliaengine.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.configuration.ProjectFolderConfiguration;
import ch.megil.teliaengine.file.exception.AssetCreationException;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.logging.LogHandler;
import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.collision.RectangleCollider;
import javafx.scene.paint.Color;

public class GameObjectFileManager {
	public GameObject load(String name) throws AssetNotFoundException, AssetFormatException {
		var fileName = ProjectFolderConfiguration.ASSETS_OBJECTS.getConfigurationWithProjectPath() + "/" + name + FileConfiguration.FILE_EXT_OBJECT.getConfiguration();
		var file = new File(fileName);
		
		try (var reader = new BufferedReader(new FileReader(file))) {
			var spec = reader.readLine().split(FileConfiguration.SEPERATOR_PROPERTY.getConfiguration());
			var depictionName = spec[2];
			var depiction = TextureFileManager.get().load(depictionName, Double.parseDouble(spec[0]), Double.parseDouble(spec[1]));
			var hitbox = new RectangleCollider(Vector.ZERO, Double.parseDouble(spec[0]), Double.parseDouble(spec[1]));
			var color = Color.web(spec[3]);
			
			var obj = new GameObject(name, depictionName, depiction, hitbox, color);
			
			return obj;
		} catch (IOException e) {
			throw new AssetNotFoundException("Game Object not found: " + name, e);
		} catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
			throw new AssetFormatException("Game Object not correctly formated: " + name, e);
		}
	}
	
	public List<GameObject> loadAll() {
		List<GameObject> res = new ArrayList<>();
		
		var path = new File(ProjectFolderConfiguration.ASSETS_OBJECTS.getConfigurationWithProjectPath());
		
		for (var file : path.list()) {
			var name = file.split(FileConfiguration.FILE_EXT_OBJECT.getConfiguration())[0];
			try {
				res.add(load(name));
			} catch (AssetNotFoundException | AssetFormatException e) {
				LogHandler.log(e, Level.SEVERE);
			}
		}
		
		return res;
	}
	
	public void create(GameObject obj) throws AssetCreationException {
		var fileName = ProjectFolderConfiguration.ASSETS_OBJECTS.getConfigurationWithProjectPath() + "/" + obj.getDepictionName() + FileConfiguration.FILE_EXT_OBJECT.getConfiguration();
		
		var propSeperator = FileConfiguration.SEPERATOR_PROPERTY.getConfiguration();
		
		try (var writer = new BufferedWriter(new FileWriter(fileName))) {
			writer.write(obj.getDepiction().getWidth() + propSeperator + obj.getDepiction().getHeight() + propSeperator +
					obj.getDepictionName() + propSeperator + obj.getColor());
		} catch (IOException e) {
			throw new AssetCreationException("Game Object could not be created", e);
		}
	}
}
