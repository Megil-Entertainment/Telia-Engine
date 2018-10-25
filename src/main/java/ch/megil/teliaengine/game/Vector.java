package ch.megil.teliaengine.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Vector {
	public static final Vector ZERO = new Vector(0, 0);
	
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
	
	public Vector negate() {
		return new Vector(-getX(), -getY());
	}
	
	public Vector add(Vector vector) {
		return new Vector(this.getX() + vector.getX(), this.getY() + vector.getY());
	}
	
	public Vector xVector() {
		return new Vector(getX(), 0);
	}
	
	public Vector yVector() {
		return new Vector(0, getY());
	}
	
	public List<Vector> splitToComponentSizeOne() {
		var tx = (int) Math.abs(getX());
		var ty = (int) Math.abs(getY());
		if (tx == 0 && ty == 0) {
			return new ArrayList<>();
		} else if (tx > ty) {
			return Collections.nCopies(tx, new Vector(getX()/tx, getY()/tx));
		} else {
			return Collections.nCopies(ty, new Vector(getX()/ty, getY()/ty));
		}
	}
}
