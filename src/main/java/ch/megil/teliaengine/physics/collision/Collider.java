package ch.megil.teliaengine.physics.collision;

import ch.megil.teliaengine.physics.Vector;

/**
 * Collider base with basic boundingbox check.
 */
public abstract class Collider {
	private Vector boundingBoxBegin;
	private Vector boundingBoxEnd;
	
	public Collider(Vector origin) {
		this.boundingBoxBegin = origin;
	}
	
	public boolean checkCollision(Collider other) {
		var collision = false;
		
		if(((boundingBoxBegin.getY() > other.boundingBoxBegin.getY() && boundingBoxBegin.getY() < other.boundingBoxEnd.getY()) ||
				(boundingBoxEnd.getY() > other.boundingBoxBegin.getY() && boundingBoxEnd.getY() < other.boundingBoxEnd.getY())) &&
			((boundingBoxBegin.getX() > other.boundingBoxBegin.getX() && boundingBoxBegin.getX() < other.boundingBoxEnd.getX()) ||
				(boundingBoxEnd.getX() > other.boundingBoxBegin.getX() && boundingBoxEnd.getX() < other.boundingBoxEnd.getX())) ||
			(boundingBoxBegin.getY() < other.boundingBoxBegin.getY() && boundingBoxEnd.getY() > other.boundingBoxEnd.getY() && 
				boundingBoxBegin.getX() < other.boundingBoxBegin.getX() && boundingBoxEnd.getX() > other.boundingBoxEnd.getX())) {
			collision = checkDetailedCollision(other);
		}
		
		return collision;
	}
	
	protected abstract boolean checkDetailedCollision(Collider other);
	
	public Vector getOrigin() {
		return boundingBoxBegin;
	}
	
	public void setOrigin(Vector origin) {
		this.boundingBoxBegin = origin;
	}
	
	protected Vector getBoundingBoxSize() {
		return boundingBoxEnd;
	}
	
	protected void setBoundingBoxSize(Vector boundingBoxSize) {
		this.boundingBoxEnd = boundingBoxSize;
	}
	
	public void move(Vector move) {
		boundingBoxBegin.add(move);
		boundingBoxEnd.add(move);
	}
}
