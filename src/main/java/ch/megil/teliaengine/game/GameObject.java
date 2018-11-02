package ch.megil.teliaengine.game;

import javafx.scene.image.Image;

public class GameObject extends GameElement{
	private String name;
	
	public GameObject(String name, Image depiction, Hitbox hitbox) {
		super(depiction, hitbox);
		this.name = name;
		this.setPosition(Vector.ZERO);
	}
	
	public GameObject(String name, Image depiction, Hitbox hitbox, double posX, double posY) {
		this(name, depiction, hitbox);
		setPosition(new Vector(posX, posY));
	}

	public String getName() {
		return name;
	}
}
