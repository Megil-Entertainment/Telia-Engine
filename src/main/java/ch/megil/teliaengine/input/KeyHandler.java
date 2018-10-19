package ch.megil.teliaengine.input;

import ch.megil.teliaengine.gamelogic.GameLoop;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyHandler {
	public void press(KeyEvent ke) {
		GameLoop.get().addInput(convert(ke.getCode()));
	}
	
	public void release(KeyEvent ke) {
		GameLoop.get().removeInput(convert(ke.getCode()));
	}
	
	private VirtualController convert(KeyCode code) {
		var vCode = VirtualController.NONE;
		switch (code) {
			case D:
			case RIGHT:
				vCode = VirtualController.WALK_RIGHT;
				break;
			case A:
			case LEFT:
				vCode = VirtualController.WALK_LEFT;
				break;
			default:
				break;
		}
		return vCode;
	}
}
