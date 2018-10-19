package ch.megil.teliaengine.gamelogic;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import ch.megil.teliaengine.input.VirtualController;
import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer {
	private static GameLoop instance;
	
	private Set<VirtualController> inputs;
	
	protected GameLoop() {
		inputs = Collections.synchronizedSet(EnumSet.noneOf(VirtualController.class));
	}
	
	public static GameLoop get() {
		if (instance == null) {
			instance = new GameLoop();
		}
		return instance;
	}
	
	public void addInput(VirtualController input) {
		inputs.add(input);
	}
	
	public void removeInput(VirtualController input) {
		inputs.remove(input);
	}
	
	public void runInputs() {
		var inputs = this.inputs.toArray(new VirtualController[0]);
		
		for (var i : inputs) {
			switch (i) {
				case WALK_RIGHT:
					break;
				case WALK_LEFT:
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void handle(long now) {
		System.out.println("test");
	}
}
