package ch.megil.teliaengine.input.converter;

import ch.megil.teliaengine.input.VirtualController;

public class AxisInput {
	private VirtualController input;
	private float minValue;
	private float maxValue;
	
	public AxisInput(VirtualController input, float minValue, float maxValue) {
		this.input = input;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public VirtualController getInput() {
		return input;
	}
	
	public boolean isInThreshold(float value) {
		return value >= minValue && value <= maxValue;
	}
}