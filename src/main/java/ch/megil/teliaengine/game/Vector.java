package ch.megil.teliaengine.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Vector {
	public static final Vector ZERO = new Vector(0, 0);
	
	private double x;
	private double y;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public Vector negate() {
		return new Vector(-x, -y);
	}
	
	public Vector add(Vector vector) {
		return new Vector(this.x + vector.x, this.y + vector.y);
	}
	
	public Vector xVector() {
		return new Vector(x, 0);
	}
	
	public Vector yVector() {
		return new Vector(0, y);
	}
	
	public List<Vector> splitToComponentSizeOne() {
		var tx = (int) Math.abs(x);
		var ty = (int) Math.abs(y);
		if (tx == 0 && ty == 0) {
			return new ArrayList<>();
		} else if (tx > ty) {
			return Collections.nCopies(tx, new Vector(x/tx, y/tx));
		} else {
			return Collections.nCopies(ty, new Vector(x/ty, y/ty));
		}
	}
}
