package ch.megil.teliaengine.physics.collision;

import ch.megil.teliaengine.physics.Vector;

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
			return center.subtract(((CircleCollider) other).center).squareLength() < (radius * radius + otherRadius * otherRadius);
		} else if (other instanceof SquareCollider) {
			return checkPointInside(((SquareCollider) other).getNearestPoint(center));
		}
		return false;
	}
	
	private boolean checkPointInside(Vector point) {
		return point.subtract(center).squareLength() < squareRadius;
	}
	
	@Override
	public void move(Vector move) {
		super.move(move);
		center = center.add(move);
	}
}
