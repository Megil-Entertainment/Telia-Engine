package ch.megil.teliaengine.physics.collision;

import ch.megil.teliaengine.physics.Vector;

/**
 * Collider base with basic boundingbox check.
 */
public abstract class Collider {
	private Vector boundingBoxBegin;
	private Vector boundingBoxEnd;
	
	public Collider() {
		boundingBoxBegin = Vector.ZERO;
		boundingBoxEnd = Vector.ZERO;
	}
	
	public boolean checkCollision(Collider other) {
		var collision = false;
		
		if(((boundingBoxBegin.getX() > other.boundingBoxBegin.getX() && boundingBoxBegin.getX() < other.boundingBoxEnd.getX())
				|| (boundingBoxEnd.getX() > other.boundingBoxBegin.getX() && boundingBoxEnd.getX() < other.boundingBoxEnd.getX())
				|| (boundingBoxBegin.getX() <= other.boundingBoxBegin.getX() && boundingBoxEnd.getX() >= other.boundingBoxEnd.getX()))
			&& ((boundingBoxBegin.getY() > other.boundingBoxBegin.getY() && boundingBoxBegin.getY() < other.boundingBoxEnd.getY())
				|| (boundingBoxEnd.getY() > other.boundingBoxBegin.getY() && boundingBoxEnd.getY() < other.boundingBoxEnd.getY())
				|| (boundingBoxBegin.getY() <= other.boundingBoxBegin.getY() && boundingBoxEnd.getY() >= other.boundingBoxEnd.getY()))) {
			collision = checkDetailedCollision(other);
		}
		
		return collision;
	}
	
	protected abstract boolean checkDetailedCollision(Collider other);
	
	public Vector getBoundingBoxBegin() {
		return boundingBoxBegin;
	}
	
	protected void setBoundingBoxBegin(Vector boundingBoxBegin) {
		this.boundingBoxBegin = boundingBoxBegin;
	}
	
	public Vector getBoundingBoxEnd() {
		return boundingBoxEnd;
	}
	
	protected void setBoundingBoxEnd(Vector boundingBoxEnd) {
		this.boundingBoxEnd = boundingBoxEnd;
	}
	
	protected void setBoundingBox(Vector boundingBoxBegin, Vector boundingBoxEnd) {
		this.boundingBoxBegin = boundingBoxBegin;
		this.boundingBoxEnd = boundingBoxEnd;
	}
	
	public void move(Vector move) {
		boundingBoxBegin = boundingBoxBegin.add(move);
		boundingBoxEnd = boundingBoxEnd.add(move);
	}
}
