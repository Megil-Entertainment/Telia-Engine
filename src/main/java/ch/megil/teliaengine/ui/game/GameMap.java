package ch.megil.teliaengine.ui.game;

import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.gamelogic.GameLoop;
import ch.megil.teliaengine.gamelogic.GameState;
import ch.megil.teliaengine.ui.MyImageView;
import javafx.scene.layout.Pane;

public class GameMap extends Pane {
	public GameMap() {
		GameState.get().getMap().getMapObjects().stream().map(MyImageView::new).forEach(getChildren()::add);
		getChildren().add(new MyImageView(Player.get()));
		
		for(var iv : getChildren()) {
			((MyImageView)iv).getGameElement().setOnPositionUpdate(v->{
				iv.setLayoutX(v.getX());
				iv.setLayoutY(v.getY());
			});
		}
		
		setOnKeyPressed(GameLoop.get().getKeyHandler()::press);
		setOnKeyReleased(GameLoop.get().getKeyHandler()::release);
	}
}