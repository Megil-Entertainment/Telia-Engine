package ch.megil.teliaengine.ui.game;

import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.gamelogic.GameLoop;
import ch.megil.teliaengine.gamelogic.GameState;
import ch.megil.teliaengine.ui.GameElementImageView;
import javafx.scene.layout.Pane;

public class GameMap extends Pane {
	public GameMap() {
		GameState.get().getMap().getMapObjects().stream().map(GameElementImageView::new).forEach(getChildren()::add);
		getChildren().add(new GameElementImageView(Player.get()));
		
		for(var iv : getChildren()) {
			((GameElementImageView)iv).getGameElement().setOnPositionUpdate(v->{
				iv.setLayoutX(v.getX());
				iv.setLayoutY(v.getY());
			});
		}
		
		setOnKeyPressed(GameLoop.get().getKeyHandler()::press);
		setOnKeyReleased(GameLoop.get().getKeyHandler()::release);
	}
}