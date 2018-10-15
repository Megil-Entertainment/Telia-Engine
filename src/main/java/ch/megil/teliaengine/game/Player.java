package ch.megil.teliaengine.game;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player {
	private DoubleProperty posX;
	private DoubleProperty posY;
	private Node depiction;
	
	public Player(Node depiction, double posX, double posY) {
		this.depiction = new Rectangle(10, 10, Color.BLACK);
		
		this.posX = new SimpleDoubleProperty(depiction.getLayoutX());
		this.posY = new SimpleDoubleProperty(depiction.getLayoutY());
		
		this.depiction.layoutXProperty().bindBidirectional(this.posX);
		this.depiction.layoutYProperty().bindBidirectional(this.posY);
		
		this.posX.set(posX);
		this.posY.set(posY);
	}
	
	public double getPosX() {
		return posX.get();
	}
	
	public void setPosX(double posX) {
		this.posX.set(posX);
	}

	public double getPosY() {
		return posY.get();
	}
	
	public void setPosY(double posY) {
		this.posY.set(posY);
	}

	public Node getDepiction() {
		return depiction;
	}
}
