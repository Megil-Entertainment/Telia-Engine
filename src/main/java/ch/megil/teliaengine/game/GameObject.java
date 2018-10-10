package ch.megil.teliaengine.game;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;

public class GameObject {
	private String name;
	private DoubleProperty posX;
	private DoubleProperty posY;
	private Node depiction;
	
	public GameObject(String name, Node depiction) {
		this.name = name;
		this.depiction = depiction;
		
		this.posX = new SimpleDoubleProperty(depiction.getLayoutX());
		this.posY = new SimpleDoubleProperty(depiction.getLayoutY());
		
		this.depiction.layoutXProperty().bindBidirectional(posX);
		this.depiction.layoutYProperty().bindBidirectional(posY);
	}
	
	public GameObject(String name, Node depiction, double posX, double posY) {
		this(name, depiction);
		this.posX.set(posX);
		this.posY.set(posY);
	}

	public String getName() {
		return name;
	}

	public double getPosX() {
		return posX.get();
	}

	public double getPosY() {
		return posY.get();
	}

	public Node getDepiction() {
		return depiction;
	}
}
