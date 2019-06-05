package ch.megil.teliaengine.physics.exception;

import ch.megil.teliaengine.physics.collision.Collider;

public class CollisionNotImplementedException extends RuntimeException {
	private static final long serialVersionUID = -7426523265059918778L;

	public CollisionNotImplementedException(Collider a, Collider b) {
		super("Collision between " + a.getClass().getName() + " and " + b.getClass().getName() + " is not implemented.");
	}
}
