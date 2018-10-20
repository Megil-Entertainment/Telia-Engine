package ch.megil.teliaengine.game;

import javafx.scene.Node;

public class GameObject {
	private String name;
	private Vector position;
	private Node depiction;
	private Hitbox hitbox;
	
	public GameObject(String name, Node depiction, Hitbox hitbox) {
		this.name = name;
		this.depiction = depiction;
		this.hitbox = hitbox;
		
		this.position = new Vector(depiction.getLayoutX(), depiction.getLayoutY());
		
		this.depiction.layoutXProperty().bindBidirectional(position.xProperty());
		this.depiction.layoutYProperty().bindBidirectional(position.yProperty());
	}
	
	public GameObject(String name, Node depiction, Hitbox hitbox, double posX, double posY) {
		this(name, depiction, hitbox);
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
	
	public Hitbox getHitbox() {
		hitbox.setOrigin(position);
		return hitbox;
	}
}
