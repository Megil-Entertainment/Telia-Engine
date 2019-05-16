package ch.megil.teliaengine.physics.collision;

import ch.megil.teliaengine.physics.Vector;

public class Hitbox {
	private Vector origin;
	private Vector size;
	
	public Hitbox(Vector origin, double width, double height) {
		this.origin = origin;
		this.size = new Vector(width, height);
	}
	
	public boolean checkCollision(Hitbox hitbox) {
		var collision = false;
		var endpoint = origin.add(size);
		var hitboxEndpoint = hitbox.origin.add(hitbox.size);
		
		if(this.equals(hitbox) ||
			((origin.getY() > hitbox.origin.getY() && origin.getY() < hitboxEndpoint.getY()) ||
				(endpoint.getY() > hitbox.origin.getY() && endpoint.getY() < hitboxEndpoint.getY())) &&
			((origin.getX() > hitbox.origin.getX() && origin.getX() < hitboxEndpoint.getX()) ||
				(endpoint.getX() > hitbox.origin.getX() && endpoint.getX() < hitboxEndpoint.getX())) ||
			(origin.getY() < hitbox.origin.getY() && endpoint.getY() > hitboxEndpoint.getY() && 
				origin.getX() < hitbox.origin.getX() && endpoint.getX() > hitboxEndpoint.getX())) {
			collision = true;
		}
		
		return collision;
	}
	
	public Vector getOrigin() {
		return origin;
	}
	
	public void setOrigin(Vector origin) {
		this.origin = origin;
	}
	
	public Vector getVectorSize() {
		return this.size;
	}
	
	public void setVectorSize(double width, double height) {
		this.size = new Vector(width, height);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
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
		Hitbox other = (Hitbox) obj;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		return true;
	}
}
