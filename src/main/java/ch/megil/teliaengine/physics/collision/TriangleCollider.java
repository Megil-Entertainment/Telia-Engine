package ch.megil.teliaengine.physics.collision;

import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.exception.CollisionNotImplementedException;

public class TriangleCollider extends Collider {
	private Vector p0;
	private Vector p1;
	private Vector p2;
	
	/**
	 * @param origin of the triangle
	 * @param p0 measured from origin
	 * @param p1 measured from origin
	 * @param p2 measured from origin
	 */
	public TriangleCollider(Vector origin, Vector p0, Vector p1, Vector p2) {
		this.p0 = p0.add(origin);
		this.p1 = p1.add(origin);
		this.p2 = p2.add(origin);
		var xmin = Math.min(Math.min(p0.getX(), p1.getX()), p2.getX());
		var xmax = Math.max(Math.max(p0.getX(), p1.getX()), p2.getX());
		var ymin = Math.min(Math.min(p0.getY(), p1.getY()), p2.getY());
		var ymax = Math.max(Math.max(p0.getY(), p1.getY()), p2.getY());
		setBoundingBox(new Vector(xmin, ymin), new Vector(xmax, ymax));
	}
	
	@Override
	protected boolean checkDetailedCollision(Collider other) {
		if (other instanceof CircleCollider) {
			other.checkDetailedCollision(this);
		} else if (other instanceof RectangleCollider) {
			//TODO: implement collision
		} else if (other instanceof TriangleCollider) {
			//TODO: implement collision
		}
		throw new CollisionNotImplementedException(this, other);
	}
	
	public double getSquareDistanceToPoint(Vector point) {
		var d0 = point.squareDistanceToLineSegment(p0, p1);
		var d1 = point.squareDistanceToLineSegment(p1, p2);
		var d2 = point.squareDistanceToLineSegment(p2, p0);
		return Math.min(Math.min(d0, d1), d2);
	}
	
	@Override
	public void move(Vector move) {
		super.move(move);
		p0 = p0.add(move);
		p1 = p1.add(move);
		p2 = p2.add(move);
	}
}
