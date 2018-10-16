package ch.megil.teliaengine.game;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player {
	private DoubleProperty posX;
	private DoubleProperty posY;
	private Vector position;
	private Node depiction;
	
	public Player(Node depiction, double posX, double posY) {
		this.depiction = new Rectangle(10, 10, Color.BLACK);
		
		this.posX = new SimpleDoubleProperty(depiction.getLayoutX());
		this.posY = new SimpleDoubleProperty(depiction.getLayoutY());
		this.position = new Vector(posX, posY);
		
		this.depiction.layoutXProperty().bindBidirectional(position.getPosX());
		this.depiction.layoutYProperty().bindBidirectional(position.getPosY());
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
