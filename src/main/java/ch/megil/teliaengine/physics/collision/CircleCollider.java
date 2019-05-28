package ch.megil.teliaengine.physics.collision;

import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.exception.CollisionNotImplementedException;

public class CircleCollider extends Collider {
	private Vector center;
	private double radius;
	private double squareRadius;
	
	public CircleCollider(Vector center, double radius) {
		this.center = center;
		this.radius = radius;
		this.squareRadius = radius*radius;
		
		var offset = new Vector(radius, radius);
		setBoundingBox(center.subtract(offset), center.add(offset));
	}

	@Override
	protected boolean checkDetailedCollision(Collider other) {
		if (other instanceof CircleCollider) {
			var otherRadius = ((CircleCollider) other).radius;
			return center.subtract(((CircleCollider) other).center).squareLength() < (radius + otherRadius) * (radius + otherRadius);
		} else if (other instanceof RectangleCollider) {
			return ((RectangleCollider) other).getNearestPoint(center).subtract(center).squareLength() < squareRadius;
		} else if (other instanceof TriangleCollider) {
			return ((TriangleCollider) other).getSquareDistanceToPoint(center) < squareRadius;
		}
		throw new CollisionNotImplementedException(this, other);
	}
	
	@Override
	public void move(Vector move) {
		super.move(move);
		center = center.add(move);
	}
}
