package ch.megil.teliaengine.game.player;

import java.util.List;

import ch.megil.teliaengine.configuration.PhysicsConstants;
import ch.megil.teliaengine.file.PlayerLoad;
import ch.megil.teliaengine.game.Hitbox;
import ch.megil.teliaengine.game.Vector;
import javafx.scene.Node;

public final class Player {
	private static Player instance;
	private static Player engine;
	
	private Vector position;
	private Node depiction;
	private Hitbox hitbox;
	private boolean jumpUsed;
	
	private Vector acceleration;
	private Vector velocity;
	
	protected Player(Node depiction, Hitbox hitbox) {
		jumpUsed = false;
		
		acceleration = Vector.ZERO;
		velocity = Vector.ZERO;
		
		this.depiction = depiction;
		this.hitbox = hitbox;
		
		this.position = new Vector(depiction.getLayoutX(), depiction.getLayoutY());
		
		this.depiction.layoutXProperty().bindBidirectional(position.xProperty());
		this.depiction.layoutYProperty().bindBidirectional(position.yProperty());
	}
	
	public static Player get() {
		if (instance == null) {
			instance = new PlayerLoad().load(Player::new);
		}
		return instance;
	}
	
	public static Player getEngine() {
		if (engine == null) {
			engine = new PlayerLoad().load(Player::new);
			engine.setPosX(get().getPosX());
			engine.setPosY(get().getPosY());
			engine.position.xProperty().addListener((obs, ov, nv) -> get().position.setX(nv.doubleValue()));
			engine.position.yProperty().addListener((obs, ov, nv) -> get().position.setY(nv.doubleValue()));
		}
		return engine;
	}
	
	public void applyAcceleration(Vector a) {
		acceleration = acceleration.add(a);
	}
	
	public void applyVelocity(Vector v) {
		velocity = velocity.add(v);
	}
	
	public void update(List<Hitbox> possibleCollisions) {
		velocity = velocity.add(acceleration);
		velocity = new Vector(velocity.getX(), Math.min(velocity.getY(), PhysicsConstants.TERMINAL_FALL_VELOCITY.get().getY()));
		
		for (var v : velocity.splitToComponentSizeOne()) {
			//x collision
			var np = position.add(v.xVector());
			position.setX(np.getX());
			position.setY(np.getY());
			if (possibleCollisions.stream().anyMatch(getHitbox()::checkCollision)) {
				np = position.add(v.xVector().negate());
				position.setX(np.getX());
				position.setY(np.getY());
			}
			
			//y collision
			np = position.add(v.yVector());
			position.setX(np.getX());
			position.setY(np.getY());
			if (possibleCollisions.stream().anyMatch(getHitbox()::checkCollision)) {
				if (v.getY() > 0) {
					jumpUsed = false;
					acceleration = new Vector(acceleration.getX(), 0);
					velocity = new Vector(velocity.getX(), 0);
				}
				np = position.add(v.yVector().negate());
				position.setX(np.getX());
				position.setY(np.getY());
			}
		}
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
	
	public boolean isJumpUsed() {
		return jumpUsed;
	}
	
	public void useJump() {
		jumpUsed = true;
	}
}
