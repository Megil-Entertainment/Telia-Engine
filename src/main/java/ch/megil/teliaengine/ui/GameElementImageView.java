package ch.megil.teliaengine.ui;

import ch.megil.teliaengine.game.GameElement;
import javafx.scene.image.ImageView;

public class GameElementImageView extends ImageView{
	private GameElement gameElement;
	
	public GameElementImageView(GameElement gameElement) {
		super(gameElement.getDepiction());
		this.gameElement = gameElement;
		
		setLayoutX(gameElement.getPosX());
		setLayoutY(gameElement.getPosY());
	}
	
	
	public void setImageViewLayoutX(double value) {
		super.setLayoutX(value);
		gameElement.setPosX(value);
	}
	
	public void setImageViewLayoutY(double value) {
		super.setLayoutY(value);
		gameElement.setPosY(value);
	}
	
	public GameElement getGameElement() {
		return gameElement;
	}
}
