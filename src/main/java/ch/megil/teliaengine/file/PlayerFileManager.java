package ch.megil.teliaengine.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.configuration.ProjectFolderConfiguration;
import ch.megil.teliaengine.file.exception.AssetCreationException;
import ch.megil.teliaengine.file.exception.AssetLoadException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.collision.RectangleCollider;
import javafx.scene.paint.Color;

public class PlayerFileManager {
	public Player load() throws AssetLoadException {
		var fileName = ProjectFolderConfiguration.ASSET_PLAYER.getConfigurationWithProjectPath() + FileConfiguration.FILE_EXT_OBJECT.getConfiguration();
		var file = new File(fileName);
		
		try (var scanner = new Scanner(file)) {
			scanner.useDelimiter(FileConfiguration.SEPARATOR_ENTRY.getConfiguration());
			
			var spec = scanner.next().split(FileConfiguration.SEPERATOR_PROPERTY.getConfiguration());

			var depictionName = spec[2];
			var depiction = TextureFileManager.get().load(depictionName, Double.parseDouble(spec[0]), Double.parseDouble(spec[1]));
			var hitbox =  new RectangleCollider(Vector.ZERO, Double.parseDouble(spec[0]), Double.parseDouble(spec[1]));
			var color = Color.web(spec[3]);
			
			return new Player(depictionName, depiction, hitbox, color);
		} catch (IOException e) {
			throw new AssetNotFoundException("Player spec not found", e);
		} catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
			throw new AssetLoadException("Player spec not correctly formated.", e);
		} catch (AssetNotFoundException e) {
			throw new AssetNotFoundException("Player texture not extisting", e);
		}
	}
	
	public void createPlayer(File projectDir, double width, double height, File texture) throws AssetCreationException {
		var fileName = projectDir + ProjectFolderConfiguration.ASSET_PLAYER.getConfigurationWithoutProjectPath() + FileConfiguration.FILE_EXT_OBJECT.getConfiguration();
		
		var depictionName = texture.getName().replace(FileConfiguration.FILE_EXT_TEXTURE.getConfiguration(), "");
		TextureFileManager.get().importTextureToOtherProject(projectDir, depictionName, texture);
		
		var propSeperator = FileConfiguration.SEPERATOR_PROPERTY.getConfiguration();
		try (var writer = new BufferedWriter(new FileWriter(fileName))) {
			writer.write(width + propSeperator + height + propSeperator + depictionName + propSeperator + "#000000");
		} catch (IOException e) {
			throw new AssetCreationException(e);
		}
	}
}
