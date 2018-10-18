package ch.megil.teliaengine.game;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Vector {
	private DoubleProperty x;
	private DoubleProperty y;
	
	public Vector(double x, double y) {
		this.x = new SimpleDoubleProperty(x);
		this.y = new SimpleDoubleProperty(y);
	}
	
	public double getX() {
		return x.get();
	}
	
	public void setX(double x) {
		this.x.set(x);
	}
	
	public DoubleProperty xProperty() {
		return x;
	}
	
	public double getY() {
		return y.get();
	}
	
	public void setY(double y) {
		this.y.set(y);
	}
	
	public DoubleProperty yProperty() {
		return y;
	}
	
	public static Vector addVectors(Vector v1, Vector v2) {
		double newX = v1.getX() + v2.getX();
		double newY = v1.getY() + v2.getY();
		
		return new Vector(newX, newY);
	}
}
