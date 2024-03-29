package ch.megil.teliaengine.physics.collision;

import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.exception.CollisionNotImplementedException;

public class TriangleCollider extends Collider implements DistanceCalculatable {
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
		var xmin = Math.min(Math.min(this.p0.getX(), this.p1.getX()), this.p2.getX());
		var xmax = Math.max(Math.max(this.p0.getX(), this.p1.getX()), this.p2.getX());
		var ymin = Math.min(Math.min(this.p0.getY(), this.p1.getY()), this.p2.getY());
		var ymax = Math.max(Math.max(this.p0.getY(), this.p1.getY()), this.p2.getY());
		setBoundingBox(new Vector(xmin, ymin), new Vector(xmax, ymax));
	}
	
	/**
	 * With origin {@link Vector#ZERO}
	 * @param p0 measured from origin
	 * @param p1 measured from origin
	 * @param p2 measured from origin
	 */
	public TriangleCollider(Vector p0, Vector p1, Vector p2) {
		this(Vector.ZERO, p0, p1, p2);
	}
	
	@Override
	protected boolean checkDetailedCollision(Collider other) {
		if (other instanceof CircleCollider) {
			return other.checkDetailedCollision(this);
		} else if (other instanceof RectangleCollider) {
			var rectangle = (RectangleCollider) other;
			var v0 = p1.subtract(p0);
			var v1 = p2.subtract(p1);
			var n = v0.perpendicularDot(v1);
			
			if (checkRectangleOutsideEdge(p0, v0, n, rectangle)) {
				return false;
			}
			if (checkRectangleOutsideEdge(p1, v1, n, rectangle)) {
				return false;
			}
			var v2 = p0.subtract(p2);
			if (checkRectangleOutsideEdge(p2, v2, n, rectangle)) {
				return false;
			}
			return true;
		} else if (other instanceof TriangleCollider) {
			var triangle = (TriangleCollider) other;
			return this.checkTriangleIntersection(triangle) && triangle.checkTriangleIntersection(this);
		} else if (other instanceof CompositeCollider) {
			return other.checkDetailedCollision(this);
		}
		throw new CollisionNotImplementedException(this, other);
	}
	
	private boolean checkTriangleIntersection(TriangleCollider other) {
		var v0 = p1.subtract(p0);
		var v1 = p2.subtract(p1);
		var n = v0.perpendicularDot(v1);
		
		if (other.checkOutsideEdge(p0, v0, n)) {
			return false;
		}
		if (other.checkOutsideEdge(p1, v1, n)) {
			return false;
		}
		var v2 = p0.subtract(p2);
		if (other.checkOutsideEdge(p2, v2, n)) {
			return false;
		}
		return true;
	}
	
	private boolean checkOutsideEdge(Vector edgeOrigin, Vector edge, double normal) {
		var wTest = edge.perpendicularDot(p0.subtract(edgeOrigin));
		if (wTest*normal > 0) {
			return false;
		}
		wTest = edge.perpendicularDot(p1.subtract(edgeOrigin));
		if (wTest*normal > 0) {
			return false;
		}
		wTest = edge.perpendicularDot(p2.subtract(edgeOrigin));
		if (wTest*normal > 0) {
			return false;
		}
		return true;
	}
	
	private boolean checkRectangleOutsideEdge(Vector edgeOrigin, Vector edge, double normal, RectangleCollider collider) {
		var wTest = edge.perpendicularDot(collider.getBoundingBoxBegin().subtract(edgeOrigin));
		if (wTest*normal > 0) {
			return false;
		}
		wTest = edge.perpendicularDot(collider.getBoundingBoxEnd().subtract(edgeOrigin));
		if (wTest*normal > 0) {
			return false;
		}
		var p2 = new Vector(collider.getBoundingBoxBegin().getX(), collider.getBoundingBoxEnd().getY());
		wTest = edge.perpendicularDot(p2.subtract(edgeOrigin));
		if (wTest*normal > 0) {
			return false;
		}
		var p3 = new Vector(collider.getBoundingBoxEnd().getX(), collider.getBoundingBoxBegin().getY());
		wTest = edge.perpendicularDot(p3.subtract(edgeOrigin));
		if (wTest*normal > 0) {
			return false;
		}
		return true;
	}
	
	@Override
	public double getSquareDistanceToPoint(Vector point) {
		if (checkPointInside(point)) {
			return 0;
		}
		var d0 = point.squareDistanceToLineSegment(p0, p1);
		var d1 = point.squareDistanceToLineSegment(p1, p2);
		var d2 = point.squareDistanceToLineSegment(p2, p0);
		return Math.min(Math.min(d0, d1), d2);
	}
	
	private boolean checkPointInside(Vector point) {
		var v0 = p1.subtract(p0);
		var v1 = p2.subtract(p1);
		var n = v0.perpendicularDot(v1);
		
		var wTest = v0.perpendicularDot(point.subtract(p0));
		if (wTest*n < 0) {
			return false;
		}
		wTest = v1.perpendicularDot(point.subtract(p1));
		if (wTest*n < 0) {
			return false;
		}
		var v2 = p0.subtract(p2);
		wTest = v2.perpendicularDot(point.subtract(p2));
		if (wTest*n < 0) {
			return false;
		}
		return true;
	}
	
	@Override
	public void move(Vector move) {
		super.move(move);
		p0 = p0.add(move);
		p1 = p1.add(move);
		p2 = p2.add(move);
	}

	protected Vector getP0() {
		return p0;
	}

	protected Vector getP1() {
		return p1;
	}

	protected Vector getP2() {
		return p2;
	}
}
