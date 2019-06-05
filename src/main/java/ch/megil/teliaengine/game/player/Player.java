package ch.megil.teliaengine.game.player;

import java.util.List;

import ch.megil.teliaengine.configuration.PhysicsConstants;
import ch.megil.teliaengine.game.GameElement;
import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.collision.Collider;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Player extends GameElement {
	private boolean jumpUsed;
	
	private Vector acceleration;
	private Vector velocity;

	public Player(String depictionName, Image depiction, Collider hitbox, Color color) {
		super(depictionName, depiction, hitbox, color);
		jumpUsed = false;
		
		acceleration = Vector.ZERO;
		velocity = Vector.ZERO;
		
		super.setPosition(Vector.ZERO);
	}
	
	public void applyForce(Vector f) {
		acceleration = acceleration.add(f);
	}
	
	public void applyAcceleration(Vector a) {
		velocity = velocity.add(a);
	}
	
	public void update(List<Collider> possibleCollisions) {
		velocity = velocity.add(acceleration);
		velocity = new Vector(velocity.getX(), Math.min(velocity.getY(), PhysicsConstants.TERMINAL_FALL_VELOCITY.get().getY()));

		for (var v : velocity.splitToComponentSizeOne()) {
			//x collision
			var np = getPosition().add(v.xVector());
			setPosition(np);
			if (possibleCollisions.stream().anyMatch(getHitbox()::checkCollision)) {
				np = getPosition().add(v.xVector().negate());
				setPosition(np);
			}
			
			//y collision
			np = getPosition().add(v.yVector());
			setPosition(np);
			if (possibleCollisions.stream().anyMatch(getHitbox()::checkCollision)) {
				if (v.getY() > 0) {
					jumpUsed = false;
					acceleration = new Vector(acceleration.getX(), 0);
					velocity = new Vector(velocity.getX(), 0);
				}
				np = getPosition().add(v.yVector().negate());
				setPosition(np);
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
