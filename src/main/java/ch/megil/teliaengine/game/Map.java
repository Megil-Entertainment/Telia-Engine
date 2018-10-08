package ch.megil.teliaengine.game;

import java.util.ArrayList;
import java.util.List;

public class Map {
	private double width;
	private double height;
	private double playerX;
	private double playerY;
	private List<GameObject> mapObjects;
	
	public Map(double width, double height, double playerX, double playerY) {
		this.width = width;
		this.height = height;
		this.playerX = playerX;
		this.playerY = playerY;
		this.mapObjects = new ArrayList<>();
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public double getPlayerX() {
		return playerX;
	}

	public double getPlayerY() {
		return playerY;
	}

	public void addObject(GameObject object) {
		mapObjects.add(object);
	}
	
	public List<GameObject> getMapObjects() {
		return mapObjects;
	}
}
