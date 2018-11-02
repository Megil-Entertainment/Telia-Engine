package ch.megil.teliaengine.game;

import javafx.scene.image.Image;

public class GameElement {
	private Vector position;
	private Hitbox hitbox;
	private Image depiction;
	
	public GameElement(Image image, Hitbox hitbox) {
		this.position = Vector.ZERO;
		this.depiction = image;
		this.hitbox = hitbox;
	}
	
	
	public double getPosX() {
		return getPosition().getX();
	}
	
	public void setPosX(double posX) {
		position = new Vector(posX, position.getY());
	}

	public double getPosY() {
		return getPosition().getY();
	}
	
	public void setPosY(double posY) {
		position = new Vector(position.getX(), posY);
	}
	
	public Image getDepiction() {
		return depiction;
	}
	
	public Hitbox getHitbox() {
		hitbox.setOrigin(getPosition());
		return hitbox;
	}
	
	public Vector getPosition() {
		return position;
	}


	public void setPosition(Vector position) {
		this.position = position;
	}
}
