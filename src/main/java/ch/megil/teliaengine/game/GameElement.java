package ch.megil.teliaengine.game;

import java.util.function.Consumer;

import javafx.scene.image.Image;

public class GameElement {
	private Vector position;
	private Hitbox hitbox;
	private Image depiction;
	private Consumer<Vector> onPositionUpdate;
	
	public GameElement(Image image, Hitbox hitbox) {
		this.position = Vector.ZERO;
		this.depiction = image;
		this.hitbox = hitbox;
		this.onPositionUpdate = v -> {};
	}
	
	public void setOnPositionUpdate(Consumer<Vector> onPositionUpdate) {
		this.onPositionUpdate = onPositionUpdate;
	}
	
	public void setPosX(double posX) {
		setPosition(new Vector(posX, position.getY()));
	}

	public void setPosY(double posY) {
		setPosition(new Vector(position.getX(), posY));
	}
	
	public Vector getPosition() {
		return position;
	}
	
	public void setPosition(Vector position) {
		this.position = position;
		onPositionUpdate.accept(position);
	}
	
	public Image getDepiction() {
		return depiction;
	}
	
	public Hitbox getHitbox() {
		hitbox.setOrigin(getPosition());
		return hitbox;
	}
}
