package ch.megil.teliaengine.ui;

import ch.megil.teliaengine.game.GameElement;
import ch.megil.teliaengine.physics.Vector;
import javafx.scene.image.ImageView;

public class GameElementImageView extends ImageView{
	private GameElement gameElement;
	
	public GameElementImageView(GameElement gameElement) {
		super(gameElement.getDepiction());
		this.gameElement = gameElement;
		
		setLayoutX(gameElement.getPosition().getX());
		setLayoutY(gameElement.getPosition().getY());
	}
	
	
	public void setImageViewLayoutX(double value) {
		super.setLayoutX(value);
		gameElement.setPosition(new Vector(value, gameElement.getPosition().getY()));
	}
	
	public void setImageViewLayoutY(double value) {
		super.setLayoutY(value);
		gameElement.setPosition(new Vector(gameElement.getPosition().getX(), value));
	}
	
	public GameElement getGameElement() {
		return gameElement;
	}
	
}
