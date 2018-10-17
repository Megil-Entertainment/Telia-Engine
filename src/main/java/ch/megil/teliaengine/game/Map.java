package ch.megil.teliaengine.game;

import java.util.ArrayList;
import java.util.List;

import ch.megil.teliaengine.game.player.Player;

public class Map {
	private String name;
	private double width;
	private double height;
	private Player player;
	private List<GameObject> mapObjects;
	
	public Map(double width, double height) {
		this.width = width;
		this.height = height;
		this.player = Player.get();
		this.mapObjects = new ArrayList<>();
	}
	
	public Map(String name, double width, double height) {
		this(width, height);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public Player getPlayer() {
		return player;
	}

	public void addObject(GameObject object) {
		mapObjects.add(object);
	}
	
	public List<GameObject> getMapObjects() {
		return mapObjects;
	}
}
