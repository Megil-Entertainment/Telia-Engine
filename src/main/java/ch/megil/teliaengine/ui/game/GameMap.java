package ch.megil.teliaengine.ui.game;

import ch.megil.teliaengine.file.MapSaveLoad;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.Map;
import javafx.scene.layout.Pane;

public class GameMap extends Pane {
	private Map map;
	
	public GameMap(String mapName) throws AssetNotFoundException, AssetFormatException {
		map = new MapSaveLoad().load(mapName, false);
	}
}
