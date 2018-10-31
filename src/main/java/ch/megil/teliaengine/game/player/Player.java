package ch.megil.teliaengine.game.player;

import java.util.List;

import ch.megil.teliaengine.configuration.PhysicsConstants;
import ch.megil.teliaengine.file.PlayerLoad;
import ch.megil.teliaengine.game.GameElement;
import ch.megil.teliaengine.game.Hitbox;
import ch.megil.teliaengine.game.Vector;
import javafx.scene.Node;
import javafx.scene.image.Image;

public final class Player extends GameElement{
	private static Player instance;
	private static Player engine;
	
	private boolean jumpUsed;
	
	private Vector acceleration;
	private Vector velocity;

	
	protected Player(Image depiction, Hitbox hitbox) {
		super(depiction, hitbox);
		jumpUsed = false;
		
		acceleration = Vector.ZERO;
		velocity = Vector.ZERO;
		
		super.setPosition(Vector.ZERO);
		
		//this.depiction.layoutXProperty().bindBidirectional(position.xProperty());
		//this.depiction.layoutYProperty().bindBidirectional(position.yProperty());
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
			engine.getPosition().xProperty().addListener((obs, ov, nv) -> get().setPosX(nv.doubleValue()));
			engine.getPosition().yProperty().addListener((obs, ov, nv) -> get().setPosY(nv.doubleValue()));
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
			var np = add(v.xVector());
			setPosX(np.getX());
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
	
	public boolean isJumpUsed() {
		return jumpUsed;
	}
	
	public void useJump() {
		jumpUsed = true;
	}
}
