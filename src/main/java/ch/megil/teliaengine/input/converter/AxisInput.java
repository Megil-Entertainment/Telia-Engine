package ch.megil.teliaengine.input.converter;

public class AxisInput {
	private int glfwGamepadAxis;
	private float minValue;
	private float maxValue;
	
	public AxisInput(int glfwGamepadAxis, float minValue, float maxValue) {
		this.glfwGamepadAxis = glfwGamepadAxis;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public int getGlfwGamepadAxis() {
		return glfwGamepadAxis;
	}
	
	public boolean isInThreshold(float value) {
		return value >= minValue && value <= maxValue;
	}
}