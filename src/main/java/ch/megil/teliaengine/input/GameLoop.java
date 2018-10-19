package ch.megil.teliaengine.input;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer {
	private static GameLoop instance;
	
	private Set<VirtualController> inputs;
	
	public GameLoop() {
		inputs = Collections.synchronizedSet(EnumSet.noneOf(VirtualController.class));
	}
	
	public void doInput(VirtualController input) {
		inputs.add(input);
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
		
	}
}
