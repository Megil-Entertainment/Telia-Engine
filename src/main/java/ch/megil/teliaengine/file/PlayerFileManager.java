package ch.megil.teliaengine.file;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.configuration.ProjectFolderConfiguration;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.Hitbox;
import ch.megil.teliaengine.game.Vector;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.game.player.PlayerConstructor;
import ch.megil.teliaengine.logging.LogHandler;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class PlayerFileManager {
	public Player load(PlayerConstructor constructor) {
		var fileName = ProjectFolderConfiguration.ASSET_PLAYER.getConfigurationWithProjectPath() + FileConfiguration.FILE_EXT_OBJECT.getConfiguration();
		var file = new File(fileName);
		
		String depictionName = null;
		
		Image depiction = new WritableImage(Integer.parseInt(GameConfiguration.PLAYER_WIDTH.getConfiguration()),
				Integer.parseInt(GameConfiguration.PLAYER_HEIGHT.getConfiguration()));
		
		var hitbox = new Hitbox(Vector.ZERO, Double.parseDouble(GameConfiguration.PLAYER_WIDTH.getConfiguration()),
				Double.parseDouble(GameConfiguration.PLAYER_HEIGHT.getConfiguration()));
		
		var color = Color.BLACK;
		
		try (var scanner = new Scanner(file)) {
			scanner.useDelimiter(FileConfiguration.SEPARATOR_ENTRY.getConfiguration());
			
			var spec = scanner.next().split(FileConfiguration.SEPERATOR_PROPERTY.getConfiguration());

			depictionName = spec[2];
			depiction = TextureFileManager.get().load(depictionName, Double.parseDouble(spec[0]), Double.parseDouble(spec[1]));
			hitbox =  new Hitbox(Vector.ZERO, Double.parseDouble(spec[0]), Double.parseDouble(spec[1]));
			color = Color.web(spec[3]);
		} catch (IOException e) {
			LogHandler.info("Player spec not found.");
			LogHandler.log(e, Level.INFO);
		} catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
			LogHandler.severe("Player spec not correctly formated.");
		} catch (AssetNotFoundException e) {
			LogHandler.severe("Player texture not existing.");
			LogHandler.log(e, Level.SEVERE);
		}
		
		return constructor.invoke(depictionName, depiction, hitbox, color);
	}
}
