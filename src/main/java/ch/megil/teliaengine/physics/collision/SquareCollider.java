package ch.megil.teliaengine.physics.collision;

import ch.megil.teliaengine.physics.Vector;

public class SquareCollider extends Collider {
	private Vector size;

	public SquareCollider(Vector origin, double width, double height) {
		super(origin);
		size = new Vector(width, height);
		setBoundingBoxSize(size);
	}
	
	@Override
	protected boolean checkDetailedCollision(Collider other) {
		return true;
	}

	public Vector getVectorSize() {
		return this.size;
	}
	
	public void setVectorSize(double width, double height) {
		this.size = new Vector(width, height);
	}
}
