package ch.megil.teliaengine.ui.game;

import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.gamelogic.GameLoop;
import ch.megil.teliaengine.gamelogic.GameState;
import javafx.scene.layout.Pane;

public class GameMap extends Pane {
	public GameMap() {
		GameState.get().getMap().getMapObjects().stream().map(GameObject::getDepiction).forEach(getChildren()::add);
		getChildren().add(Player.get().getDepiction());
		
		setOnKeyPressed(GameLoop.get().getKeyHandler()::press);
		setOnKeyReleased(GameLoop.get().getKeyHandler()::release);
	}
}