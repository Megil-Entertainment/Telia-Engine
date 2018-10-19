package ch.megil.teliaengine.game;

public class Hitbox {
	Vector origin;
	Vector size;
	
	public Hitbox(Vector origin, double width, double height) {
		this.origin = origin;
		this.size = new Vector(width, height);
	}
	
	public boolean checkCollision(Hitbox hitbox) {
		boolean collision = false;
		double vectorEndpointX = origin.addVectors(size).getX();
		double vectorEndpointY = origin.addVectors(size).getY();
		double hitboxEndpointX = hitbox.origin.addVectors(hitbox.size).getX();
		double hitboxEndpointY = hitbox.origin.addVectors(hitbox.size).getY();
		
		if(((origin.getY() >= hitbox.origin.getY() && origin.getY() <= hitboxEndpointY) ||
				(vectorEndpointY >= hitbox.origin.getY() && vectorEndpointY <= hitboxEndpointY)) &&
			((origin.getX() >= hitbox.origin.getX() && origin.getX() <= hitboxEndpointX) ||
				(vectorEndpointX >= hitbox.origin.getX() && vectorEndpointX <= hitboxEndpointX)) ||
			(origin.getY() <= hitbox.origin.getY() && vectorEndpointY >= hitboxEndpointY && 
				origin.getX() <= hitbox.origin.getX() && vectorEndpointX >= hitboxEndpointX)) {
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
