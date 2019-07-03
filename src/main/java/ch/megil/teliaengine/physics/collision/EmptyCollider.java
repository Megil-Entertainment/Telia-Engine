package ch.megil.teliaengine.physics.collision;

import ch.megil.teliaengine.physics.Vector;

/**
 * Collider that never collides for use with non collidable objects.
 */
public class EmptyCollider extends Collider {
	@Override
	public boolean checkCollision(Collider other) {
		return false;
	}
	
	@Override
	protected boolean checkDetailedCollision(Collider other) {
		return false;
	}
	
	@Override
	public void move(Vector move) {}
}
