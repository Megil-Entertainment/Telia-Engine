package ch.megil.teliaengine.game;

import javafx.scene.Node;

public class GameObject {
	private String name;
	private Vector position;
	private Node depiction;
	
	public GameObject(String name, Node depiction) {
		this.name = name;
		this.depiction = depiction;
		
		this.position = new Vector(depiction.getLayoutX(), depiction.getLayoutY());
		
		this.depiction.layoutXProperty().bindBidirectional(position.xProperty());
		this.depiction.layoutYProperty().bindBidirectional(position.yProperty());
	}
	
	public GameObject(String name, Node depiction, double posX, double posY) {
		this(name, depiction);
		this.position.setX(posX);
		this.position.setY(posY);
	}

	public String getName() {
		return name;
	}

	public double getPosX() {
		return position.getX();
	}
	
	public void setPosX(double posX) {
		this.position.setX(posX);
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
