package ch.megil.teliaengine.file;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.game.Hitbox;
import ch.megil.teliaengine.game.Vector;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.game.player.PlayerConstructor;
import ch.megil.teliaengine.logging.LogHandler;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PlayerLoad {
	public Player load(PlayerConstructor constructor) {
		var fileName = GameConfiguration.ASSET_PLAYER.getConfiguration();
		var file = new File(fileName);
		
		var depiction = new Image("www.zhaw.ch");
		
		var hitbox = new Hitbox(Vector.ZERO, Double.parseDouble(GameConfiguration.PLAYER_WIDTH.getConfiguration()),
				Double.parseDouble(GameConfiguration.PLAYER_HEIGHT.getConfiguration()));
		
		try (var scanner = new Scanner(file)) {
			scanner.useDelimiter(GameConfiguration.SEPARATOR_ENTRY.getConfiguration());
			
			var spec = scanner.next().split(GameConfiguration.SEPERATOR_PROPERTY.getConfiguration());
			depiction = new Image("www.zhaw.ch");
			hitbox =  new Hitbox(Vector.ZERO, Double.parseDouble(spec[0]), Double.parseDouble(spec[1]));
		} catch (IOException e) {
			LogHandler.info("Player spec not found.");
			LogHandler.log(e, Level.INFO);
		} catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
			LogHandler.severe("Player spec not correctly formated.");
		}
		
		return constructor.invoke(depiction, hitbox);
	}
}
