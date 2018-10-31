package ch.megil.teliaengine.game;

import javafx.scene.image.Image;

public class GameObject extends GameElement{
	private String name;
	
	public GameObject(String name, Image depiction, Hitbox hitbox) {
		super(depiction, hitbox);
		this.name = name;
		this.setPosition(new Vector(Vector.ZERO.getX(), Vector.ZERO.getY()));
		
		//this.depiction.layoutXProperty().bindBidirectional(position.xProperty());
		//this.depiction.layoutYProperty().bindBidirectional(position.yProperty());
	}
	
	public GameObject(String name, Image depiction, Hitbox hitbox, double posX, double posY) {
		this(name, depiction, hitbox);
		setPosX(posX);
		setPosY(posY);
	}

	public String getName() {
		return name;
	}
}
