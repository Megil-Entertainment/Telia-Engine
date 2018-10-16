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
		this.y.set(x);
	}
	
	public DoubleProperty getPosX() {
		return x;
	}
	
	public void setDoublePropertyX(DoubleProperty x) {
		this.x = x;
	}
	
	public double getY() {
		return y.get();
	}
	
	public void setY(double y) {
		this.y.set(y);
	}
	
	public DoubleProperty getPosY() {
		return y;
	}
	
	public void setDoublePropertyY(DoubleProperty y) {
		this.y = y;
	}
}
