package ch.megil.teliaengine.game;

public class Hitbox {
	Vector origin;
	Vector size;
	Vector endpoint;
	
	public Hitbox(Vector origin, double width, double height) {
		this.origin = origin;
		this.size = new Vector(width, height);
		endpoint = Vector.addVectors(origin, size);
	}
	
	public boolean checkCollision(Hitbox hitbox) {
		boolean collision = false;
		
		if(((origin.getY() >= hitbox.origin.getY() && origin.getY() <= hitbox.endpoint.getY()) ||
				(endpoint.getY() >= hitbox.origin.getY() && endpoint.getY() <= hitbox.endpoint.getY())) &&
			((origin.getX() >= hitbox.origin.getX() && origin.getX() <= hitbox.endpoint.getX()) ||
				(endpoint.getX() >= hitbox.origin.getX() && endpoint.getX() <= hitbox.endpoint.getX())) ||
			(origin.getY() <= hitbox.origin.getY() && endpoint.getY() >= hitbox.endpoint.getY() && 
				origin.getX() <= hitbox.origin.getX() && endpoint.getX() >= hitbox.endpoint.getX())) {
			collision = true;
		}
		
		return collision;
	}
	
	public Vector getOrigin() {
		return origin;
	}
	
	public void setOrigin(Vector origin) {
		this.origin = origin;
		this.endpoint = Vector.addVectors(origin, size);
	}
	
	public Vector getVectorSize() {
		return this.size;
	}
	
	public void setVectorSize(double width, double height) {
		this.size.setX(width);
		this.size.setY(height);
	}
	
}
