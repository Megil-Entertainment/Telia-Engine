package ch.megil.teliaengine.physics.collision;

import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.exception.CollisionNotImplementedException;

public class RectangleCollider extends Collider {
	private Vector size;

	public RectangleCollider(Vector origin, double width, double height) {
		size = new Vector(width, height);
		setBoundingBox(origin, origin.add(size));
	}
	
	@Override
	protected boolean checkDetailedCollision(Collider other) {
		if (other instanceof CircleCollider) {
			return other.checkDetailedCollision(this);
		} else if (other instanceof RectangleCollider) {
			return true;
		} else if (other instanceof TriangleCollider) {
			//TODO: implement collision
		}
		throw new CollisionNotImplementedException(this, other);
	}
	
	public double getSquareDistanceToPoint(Vector point) {
		return point.clamp(getBoundingBoxBegin(), getBoundingBoxEnd()).subtract(point).squareLength();
	}
}
