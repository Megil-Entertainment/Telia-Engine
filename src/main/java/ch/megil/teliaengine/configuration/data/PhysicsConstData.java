package ch.megil.teliaengine.configuration.data;

public class PhysicsConstData {
	private double walkSpeed;
	private double jumpStrength;
	private double gravityStrength;
	private double terminalSpeed;
	
	public double getWalkSpeed() {
		return walkSpeed;
	}
	
	public void setWalkSpeed(double walkSpeed) {
		this.walkSpeed = walkSpeed;
	}
	
	public double getJumpStrength() {
		return jumpStrength;
	}
	
	public void setJumpStrength(double jumpStrength) {
		this.jumpStrength = jumpStrength;
	}
	
	public double getGravityStrength() {
		return gravityStrength;
	}
	
	public void setGravityStrength(double gravityStrength) {
		this.gravityStrength = gravityStrength;
	}
	
	public double getTerminalSpeed() {
		return terminalSpeed;
	}
	
	public void setTerminalSpeed(double terminalSpeed) {
		this.terminalSpeed = terminalSpeed;
	}
}
