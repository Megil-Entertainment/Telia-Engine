package ch.megil.teliaengine.physics.collision;

import ch.megil.teliaengine.physics.Vector;

/**
 * Collider base with basic boundingbox check.
 */
public abstract class Collider {
	private Vector origin;
	private Vector boundingBoxSize;
	
	public Collider(Vector origin) {
		this.origin = origin;
	}
	
	public boolean checkCollision(Collider other) {
		var collision = false;
		var endpoint = origin.add(boundingBoxSize);
		var hitboxEndpoint = other.origin.add(other.boundingBoxSize);
		
		if(this.equals(other) ||
			((origin.getY() > other.origin.getY() && origin.getY() < hitboxEndpoint.getY()) ||
				(endpoint.getY() > other.origin.getY() && endpoint.getY() < hitboxEndpoint.getY())) &&
			((origin.getX() > other.origin.getX() && origin.getX() < hitboxEndpoint.getX()) ||
				(endpoint.getX() > other.origin.getX() && endpoint.getX() < hitboxEndpoint.getX())) ||
			(origin.getY() < other.origin.getY() && endpoint.getY() > hitboxEndpoint.getY() && 
				origin.getX() < other.origin.getX() && endpoint.getX() > hitboxEndpoint.getX())) {
			collision = checkDetailedCollision(other);
		}
		
		return collision;
	}
	
	protected abstract boolean checkDetailedCollision(Collider other);
	
	public Vector getOrigin() {
		return origin;
	}
	
	public void setOrigin(Vector origin) {
		this.origin = origin;
	}
	
	protected Vector getBoundingBoxSize() {
		return boundingBoxSize;
	}
	
	protected void setBoundingBoxSize(Vector boundingBoxSize) {
		this.boundingBoxSize = boundingBoxSize;
	}
}
