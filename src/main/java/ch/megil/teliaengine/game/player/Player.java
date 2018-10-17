package ch.megil.teliaengine.game.player;

import ch.megil.teliaengine.file.PlayerLoad;
import ch.megil.teliaengine.game.Vector;
import javafx.scene.Node;

public final class Player {
	private static Player instance;
	
	private Vector position;
	private Node depiction;
	
	protected Player(Node depiction) {
		this.depiction = depiction;
		
		this.position = new Vector(depiction.getLayoutX(), depiction.getLayoutY());
		
		this.depiction.layoutXProperty().bindBidirectional(position.xProperty());
		this.depiction.layoutYProperty().bindBidirectional(position.yProperty());
	}
	
	public static Player get() {
		if (instance == null) {
			instance = new PlayerLoad().load(Player::new);
		}
		return instance;
	}
	
	public double getPosX() {
		return position.getX();
	}
	
	public void setPosX(double posX) {
		this.position.setX(posX);;
	}

	public double getPosY() {
		return position.getY();
	}
	
	public void setPosY(double posY) {
		this.position.setY(posY);
	}

	public Node getDepiction() {
		return depiction;
	}
}
