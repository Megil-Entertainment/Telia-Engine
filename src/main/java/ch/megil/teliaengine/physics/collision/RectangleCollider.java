package ch.megil.teliaengine.physics.collision;

import ch.megil.teliaengine.physics.Vector;

public class RectangleCollider extends Collider {
	private Vector size;

	public RectangleCollider(Vector origin, double width, double height) {
		size = new Vector(width, height);
		setBoundingBox(origin, origin.add(size));
	}
	
	@Override
	protected boolean checkDetailedCollision(Collider other) {
		return true;
	}
	
	public Vector getNearestPoint(Vector point) {
		return point.clamp(getBoundingBoxBegin(), getBoundingBoxEnd());
	}
}
