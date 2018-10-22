package ch.megil.teliaengine.gamelogic;

import ch.megil.teliaengine.game.Map;

public class GameState {
	private static GameState instatnce;
	
	private Map map;
	
	protected GameState() {
		map = new Map(0, 0);
	}
	
	public static GameState get() {
		if (instatnce == null) {
			instatnce = new GameState();
		}
		return instatnce;
	}
	
	public synchronized Map getMap() {
		return map;
	}
	
	public synchronized void setMap(Map map) {
		this.map = map;
	}
}
