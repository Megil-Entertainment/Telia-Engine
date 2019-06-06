package ch.megil.teliaengine.game;

import java.util.function.Consumer;

import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.collision.Collider;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class GameElement {
	private Vector position;
	private Collider hitbox;
	private Image depiction;
	private String depictionName;
	private Integer depictionIndex;
	private Color color;
	private Consumer<Vector> onPositionUpdate;
	
	public GameElement(String depictionName, Image image, Collider hitbox, Color color) {
		this.position = Vector.ZERO;
		this.depiction = image;
		this.depictionName = depictionName;
		this.hitbox = hitbox;
		this.color = color;
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
		hitbox.move(position.subtract(this.position));
		this.position = position;
		onPositionUpdate.accept(position);
	}
	
	public Image getDepiction() {
		return depiction;
	}
	
	public String getDepictionName() {
		return depictionName;
	}
	
	public Integer getDepictionIndex() {
		return depictionIndex;
	}
	
	public void setDepictionIndex(Integer depictionIndex) {
		this.depictionIndex = depictionIndex;
	}
	
	public Collider getHitbox() {
		return hitbox;
	}
	
	public Color getColor() {
		return color;
	}
}
