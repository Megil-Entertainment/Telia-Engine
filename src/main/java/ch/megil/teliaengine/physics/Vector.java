package ch.megil.teliaengine.physics;

import java.util.Collections;
import java.util.List;

public class Vector {
	public static final Vector ZERO = new Vector(0, 0);
	
	private static final double TOLERANCE = 0.000000001;
	
	private double x;
	private double y;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public Vector negate() {
		return new Vector(-x, -y);
	}
	
	public Vector add(Vector vector) {
		return new Vector(this.x + vector.x, this.y + vector.y).eliminateFloatingPointError();
	}
	
	public Vector subtract(Vector vector) {
		return new Vector(this.x - vector.x, this.y - vector.y).eliminateFloatingPointError();
	}
	
	public double dot(Vector vector) {
		return x*vector.x + y*vector.y;
	}
	
	public double perpendicularDot(Vector vector) {
		return x*vector.y - y*vector.x;
	}
	
	public Vector multiplyByComponent(Vector vector) {
		return new Vector(this.x * vector.x, this.y * vector.y).eliminateFloatingPointError();
	}
	
	public double squareLength() {
		return x*x + y*y;
	}
	
	public double squareDistanceToLineSegment(Vector p0, Vector p1) {
		var v = p1.subtract(p0);
		
		var w = this.subtract(p0);
		var proj = w.dot(v);
		if(proj <= 0) {
			return w.dot(w);
		} else {
			var vsq = v.dot(v);
			if (proj >= vsq) {
				return w.dot(w) - 2*proj + vsq;
			} else {
				return w.dot(w) - proj*proj/vsq;
			}
		}
	}
	
	public Vector clamp(Vector min, Vector max) {
		return new Vector(Math.max(min.x, Math.min(x, max.x)), Math.max(min.y, Math.min(y, max.y)));
	}
	
	public Vector xVector() {
		return new Vector(x, 0);
	}
	
	public Vector yVector() {
		return new Vector(0, y);
	}
	
	private Vector eliminateFloatingPointError() {
		var xRounded = Math.rint(x);
		var yRounded = Math.rint(y);
		x = Math.abs(x - xRounded) <= TOLERANCE ? xRounded : x;
		y = Math.abs(y - yRounded) <= TOLERANCE ? yRounded : y;
		return this;
	}
	
	public List<Vector> splitToComponentSizeOne() {
		var tx = (int) Math.abs(x);
		var ty = (int) Math.abs(y);
		if (tx == 0 && ty == 0) {
			return Collections.nCopies(1, this);
		} else if (tx > ty) {
			return Collections.nCopies(tx, new Vector(x/tx, y/tx));
		} else {
			return Collections.nCopies(ty, new Vector(x/ty, y/ty));
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector other = (Vector) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
}
