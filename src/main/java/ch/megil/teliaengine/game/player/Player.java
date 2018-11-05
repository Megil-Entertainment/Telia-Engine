package ch.megil.teliaengine.game.player;

import java.util.List;

import ch.megil.teliaengine.configuration.PhysicsConstants;
import ch.megil.teliaengine.file.PlayerLoad;
import ch.megil.teliaengine.game.GameElement;
import ch.megil.teliaengine.game.Hitbox;
import ch.megil.teliaengine.game.Vector;
import javafx.scene.image.Image;

public class Player extends GameElement{
	private static Player instance;
	
	private boolean jumpUsed;
	
	private Vector acceleration;
	private Vector velocity;

	
	protected Player(Image depiction, Hitbox hitbox) {
		super(depiction, hitbox);
		jumpUsed = false;
		
		acceleration = Vector.ZERO;
		velocity = Vector.ZERO;
		
		super.setPosition(Vector.ZERO);

	}
	
	public static Player get() {
		if (instance == null) {
			instance = new PlayerLoad().load(Player::new);
		}
		return instance;
	}
	
	public static Player getEngineCopy() {
		var player = get();
		var hitboxSize = player.getHitbox().getVectorSize();
		
		var enginePlayer = new Player(player.getDepiction(), new Hitbox(player.getPosition(), hitboxSize.getX(), hitboxSize.getY()));
		enginePlayer.setPosition(player.getPosition());
		
		return enginePlayer;
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
			var np = getPosition().add(v.xVector());
			setPosX(np.getX());
			setPosY(np.getY());
			if (possibleCollisions.stream().anyMatch(getHitbox()::checkCollision)) {
				np = getPosition().add(v.xVector().negate());
				setPosX(np.getX());
				setPosY(np.getY());
			}
			
			//y collision
			np = getPosition().add(v.yVector());
			setPosX(np.getX());
			setPosY(np.getY());
			if (possibleCollisions.stream().anyMatch(getHitbox()::checkCollision)) {
				if (v.getY() > 0) {
					jumpUsed = false;
					acceleration = new Vector(acceleration.getX(), 0);
					velocity = new Vector(velocity.getX(), 0);
				}
				np = getPosition().add(v.yVector().negate());
				setPosX(np.getX());
				setPosY(np.getY());
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
