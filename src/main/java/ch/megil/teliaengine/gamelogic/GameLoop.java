package ch.megil.teliaengine.gamelogic;

import ch.megil.teliaengine.configuration.PhysicsConstants;
import ch.megil.teliaengine.game.GameObject;
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
		var strokes = keyHandler.getKeyStrokes();
		var pressed = strokes.get(0);
		var released = strokes.get(1);
		
		for (var key : pressed) {
			switch (key) {
				case WALK_RIGHT:
					Player.get().applyVelocity(PhysicsConstants.WALK_SPEED_RIGHT.get());
					break;
				case WALK_LEFT:
					Player.get().applyVelocity(PhysicsConstants.WALK_SPEED_LEFT.get());
					break;
				case JUMP:
					Player.get().applyAcceleration(PhysicsConstants.JUMP_ACCELERATION.get());
					break;
				default:
					break;
			}
		}
		
		for (var key : released) {
			switch (key) {
				case WALK_RIGHT:
					Player.get().applyVelocity(PhysicsConstants.WALK_SPEED_RIGHT.get().negate());
					break;
				case WALK_LEFT:
					Player.get().applyVelocity(PhysicsConstants.WALK_SPEED_LEFT.get().negate());
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
			
			Player.get().update(GameState.get().getMap().getMapObjects().stream().map(GameObject::getHitbox));
		}
	}
	
	public KeyHandler getKeyHandler() {
		return keyHandler;
	}
}
