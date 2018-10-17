package ch.megil.teliaengine.game;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player {
	private Vector position;
	private Node depiction;
	
	public Player(Node depiction, double posX, double posY) {
		this.depiction = new Rectangle(10, 10, Color.BLACK);
		
		this.position = new Vector(depiction.getLayoutX(), depiction.getLayoutY());
		
		this.depiction.layoutXProperty().bindBidirectional(position.xProperty());
		this.depiction.layoutYProperty().bindBidirectional(position.yProperty());
		
		position.setX(posX);
		position.setY(posY);
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
