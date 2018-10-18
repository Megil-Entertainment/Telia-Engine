package ch.megil.teliaengine.game;

public class Hitbox {
	Vector origin;
	Vector endpoint;
	
	public Hitbox(Vector origin, double width, double height) {
		this.origin = origin;
		this.endpoint = new Vector(this.origin.getX()+width, this.origin.getY()+height);
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
	}
	
	public Vector getVectorSize() {
		return this.endpoint;
	}
	
	public void setVectorSize(double width, double height) {
		this.endpoint.setX(width);
		this.endpoint.setY(height);
	}
	
}
