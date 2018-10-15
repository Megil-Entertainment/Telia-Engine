package ch.megil.teliaengine.game;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Vector {
	private DoubleProperty posX;
	private DoubleProperty posY;
	
	public Vector(double x, double y) {
		this.posX = new SimpleDoubleProperty(x);
		this.posY = new SimpleDoubleProperty(y);
	}
	
	public void setX(double x) {
		this.posX.set(x);
	}
	
	public double getX() {
		return posX.get();
	}
	
	public void setY(double y) {
		this.posY.set(y);
	}
	
	public double getY() {
		return posY.get();
	}
	
	public DoubleProperty getPosX() {
		return posX;
	}
	
	public DoubleProperty getPosY() {
		return posY;
	}
}
