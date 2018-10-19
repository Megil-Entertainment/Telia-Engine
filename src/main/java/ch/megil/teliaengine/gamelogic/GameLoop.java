package ch.megil.teliaengine.gamelogic;

import ch.megil.teliaengine.game.Vector;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.input.KeyHandler;
import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer {
	private static final long TICK_SPEED = 1000_000_000/20;
	
	private static GameLoop instance;
	
	private long lastRun;
	private KeyHandler keyHandler;
	
	protected GameLoop() {
		keyHandler = new KeyHandler();
	}
	
	public static GameLoop get() {
		if (instance == null) {
			instance = new GameLoop();
		}
		return instance;
	}
	
	public void runInputs() {
		var released = keyHandler.getReleased();
		var pressed = keyHandler.getPressed();
		
		for (var key : pressed) {
			switch (key) {
				case WALK_RIGHT:
					Player.get().applyAcceleration(new Vector(10, 0));
					break;
				case WALK_LEFT:
					Player.get().applyAcceleration(new Vector(-10, 0));
					break;
				default:
					break;
			}
		}
		
		for (var key : released) {
			switch (key) {
				case WALK_RIGHT:
					Player.get().applyAcceleration(new Vector(-10, 0));
					break;
				case WALK_LEFT:
					Player.get().applyAcceleration(new Vector(10, 0));
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void handle(long now) {
		var delta = now - lastRun;
		if (delta >= TICK_SPEED) {
			lastRun = now;
			runInputs();
			
			Player.get().update();
		}
	}
	
	public KeyHandler getKeyHandler() {
		return keyHandler;
	}
}
