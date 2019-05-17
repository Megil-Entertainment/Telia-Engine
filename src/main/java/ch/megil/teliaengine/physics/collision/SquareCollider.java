package ch.megil.teliaengine.physics.collision;

import ch.megil.teliaengine.physics.Vector;

public class SquareCollider extends Collider {
	private Vector size;

	public SquareCollider(Vector origin, double width, double height) {
		super(origin);
		size = new Vector(width, height);
		setBoundingBoxEnd(origin.add(size));
	}
	
	@Override
	protected boolean checkDetailedCollision(Collider other) {
		return true;
	}
}
