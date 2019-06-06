package ch.megil.teliaengine.gamelogic;

import java.util.stream.Collectors;

import ch.megil.teliaengine.configuration.PhysicsConstants;
import ch.megil.teliaengine.game.GameObject;
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
					GameState.get().getPlayer().applyAcceleration(PhysicsConstants.WALK_SPEED_RIGHT.get());
					break;
				case WALK_LEFT:
					GameState.get().getPlayer().applyAcceleration(PhysicsConstants.WALK_SPEED_LEFT.get());
					break;
				case JUMP:
					if (!GameState.get().getPlayer().isJumpUsed()) {
						GameState.get().getPlayer().useJump();
						GameState.get().getPlayer().applyForce(PhysicsConstants.JUMP_FORCE.get());
					}
					break;
				default:
					break;
			}
		}
		
		for (var key : released) {
			switch (key) {
				case WALK_RIGHT:
					GameState.get().getPlayer().applyAcceleration(PhysicsConstants.WALK_SPEED_RIGHT.get().negate());
					break;
				case WALK_LEFT:
					GameState.get().getPlayer().applyAcceleration(PhysicsConstants.WALK_SPEED_LEFT.get().negate());
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void handle(long now) {
		var delta = now - lastRun;
		inputHandler.updateGamepad();
		if (delta >= TICK_SPEED) {
			lastRun = now;
			runInputs();
			
			GameState.get().getPlayer().applyForce(PhysicsConstants.GRAVITY.get());
			GameState.get().getPlayer().update(GameState.get().getMap().getMapObjects().stream().map(GameObject::getHitbox).collect(Collectors.toList()));
		}
	}
	
	public InputHandler getInputHandler() {
		return inputHandler;
	}
}
