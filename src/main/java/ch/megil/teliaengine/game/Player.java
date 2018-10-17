package ch.megil.teliaengine.game;

import javafx.scene.Node;

public class Player {
	private Vector position;
	private Node depiction;
	
	public Player(Node depiction, double posX, double posY) {
		this.depiction = depiction;
		
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
