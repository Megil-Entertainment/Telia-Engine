package ch.megil.teliaengine.game;

import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.collision.Hitbox;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class GameObject extends GameElement {
	private String name;
	
	public GameObject(String name, String depictionName, Image depiction, Hitbox hitbox, Color color) {
		super(depictionName, depiction, hitbox, color);
		this.name = name;
	}
	
	public GameObject(String name, String depictionName, Image depiction, Hitbox hitbox, Color color, double posX, double posY) {
		this(name, depictionName, depiction, hitbox, color);
		setPosition(new Vector(posX, posY));
	}

	public String getName() {
		return name;
	}
}
