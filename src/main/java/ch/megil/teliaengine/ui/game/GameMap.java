package ch.megil.teliaengine.ui.game;

import ch.megil.teliaengine.file.MapSaveLoad;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.game.player.Player;
import javafx.scene.layout.Pane;

public class GameMap extends Pane {
	private Map map;

	public GameMap(String mapName) throws AssetNotFoundException, AssetFormatException {
		map = new MapSaveLoad().load(mapName, false);

		map.getMapObjects().stream().map(GameObject::getDepiction).forEach(getChildren()::add);
		getChildren().add(Player.get().getDepiction());
	}
}