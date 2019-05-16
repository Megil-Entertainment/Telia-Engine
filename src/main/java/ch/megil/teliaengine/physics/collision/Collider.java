package ch.megil.teliaengine.physics.collision;

import ch.megil.teliaengine.physics.Vector;

public class Collider {
	private Vector origin;
	
	public Collider(Vector origin) {
		this.origin = origin;
	}
	
	public boolean checkCollision(Collider collider) {
//		var collision = false;
//		var endpoint = origin.add(size);
//		var hitboxEndpoint = hitbox.origin.add(hitbox.size);
//		
//		if(this.equals(hitbox) ||
//			((origin.getY() > hitbox.origin.getY() && origin.getY() < hitboxEndpoint.getY()) ||
//				(endpoint.getY() > hitbox.origin.getY() && endpoint.getY() < hitboxEndpoint.getY())) &&
//			((origin.getX() > hitbox.origin.getX() && origin.getX() < hitboxEndpoint.getX()) ||
//				(endpoint.getX() > hitbox.origin.getX() && endpoint.getX() < hitboxEndpoint.getX())) ||
//			(origin.getY() < hitbox.origin.getY() && endpoint.getY() > hitboxEndpoint.getY() && 
//				origin.getX() < hitbox.origin.getX() && endpoint.getX() > hitboxEndpoint.getX())) {
//			collision = true;
//		}
//		
//		return collision;
		
		return false;
	}
	
	public Vector getOrigin() {
		return origin;
	}
	
	public void setOrigin(Vector origin) {
		this.origin = origin;
	}
}
