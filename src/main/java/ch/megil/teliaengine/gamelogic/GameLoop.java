package ch.megil.teliaengine.gamelogic;

import java.util.stream.Collectors;

import ch.megil.teliaengine.configuration.PhysicsConstants;
import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.input.InputHandler;
import ch.megil.teliaengine.input.converter.GameKeyConverter;
import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer {
	private static final long TICK_SPEED = 1000_000_000/20;
	
	private static GameLoop instance;
	
	private long lastRun;
	private InputHandler inputHandler;
	
	protected GameLoop() {
		this.inputHandler = new InputHandler(new GameKeyConverter());
	}
	
	public static GameLoop get() {
		if (instance == null) {
			instance = new GameLoop();
		}
		return instance;
	}
	
	public void runInputs() {
		var strokes = inputHandler.getInputs();
		var pressed = strokes.get(0);
		var released = strokes.get(1);
		
		for (var key : pressed) {
			switch (key) {
				case WALK_RIGHT:
					Player.get().applyAcceleration(PhysicsConstants.WALK_SPEED_RIGHT.get());
					break;
				case WALK_LEFT:
					Player.get().applyAcceleration(PhysicsConstants.WALK_SPEED_LEFT.get());
					break;
				case JUMP:
					if (!Player.get().isJumpUsed()) {
						Player.get().useJump();
						Player.get().applyForce(PhysicsConstants.JUMP_FORCE.get());
					}
					break;
				default:
					break;
			}
		}
		
		for (var key : released) {
			switch (key) {
				case WALK_RIGHT:
					Player.get().applyAcceleration(PhysicsConstants.WALK_SPEED_RIGHT.get().negate());
					break;
				case WALK_LEFT:
					Player.get().applyAcceleration(PhysicsConstants.WALK_SPEED_LEFT.get().negate());
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
			
			Player.get().applyForce(PhysicsConstants.GRAVITY.get());
			Player.get().update(GameState.get().getMap().getMapObjects().stream().map(GameObject::getHitbox).collect(Collectors.toList()));
		}
	}
	
	public InputHandler getInputHandler() {
		return inputHandler;
	}
}
