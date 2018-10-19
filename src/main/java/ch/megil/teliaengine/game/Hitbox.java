package ch.megil.teliaengine.game;

public class Hitbox {
	Vector origin;
	Vector size;
	
	public Hitbox(Vector origin, double width, double height) {
		this.origin = origin;
		this.size = new Vector(width, height);
	}
	
	public boolean checkCollision(Hitbox hitbox) {
		var collision = false;
		var endpoint = origin.add(size);
		var hitboxEndpoint = hitbox.origin.add(hitbox.size);
		
		if(((origin.getY() >= hitbox.origin.getY() && origin.getY() <= hitboxEndpoint.getY()) ||
				(endpoint.getY() >= hitbox.origin.getY() && endpoint.getY() <= hitboxEndpoint.getY())) &&
			((origin.getX() >= hitbox.origin.getX() && origin.getX() <= hitboxEndpoint.getX()) ||
				(endpoint.getX() >= hitbox.origin.getX() && endpoint.getX() <= hitboxEndpoint.getX())) ||
			(origin.getY() <= hitbox.origin.getY() && endpoint.getY() >= hitboxEndpoint.getY() && 
				origin.getX() <= hitbox.origin.getX() && endpoint.getX() >= hitboxEndpoint.getX())) {
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
		this.size.setX(width);
		this.size.setY(height);
	}
	
}
