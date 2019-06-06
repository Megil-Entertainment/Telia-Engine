package ch.megil.teliaengine.configuration;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import ch.megil.teliaengine.configuration.data.PhysicsConstData;

public class PhysicsConstantsTest {
	private PhysicsConstData physicsConstData;
	
	@Before
	public void setUp() throws Exception {
		physicsConstData = new PhysicsConstData();
		physicsConstData.setWalkSpeed(10);
		physicsConstData.setJumpStrength(10);
		physicsConstData.setGravityStrength(5);
		physicsConstData.setTerminalSpeed(20);
	}

	@Test
	public void test() {
		var props = new Properties();
		PhysicsConstants.writeDataToProperties(props, physicsConstData);
		
		assertEquals("walkSpeedR", "10.0/0", props.getProperty("walkSpeedR"));
		assertEquals("walkSpeedL", "-10.0/0", props.getProperty("walkSpeedL"));
		assertEquals("jumpForce", "0/-10.0", props.getProperty("jumpForce"));
		assertEquals("gravity", "0/5.0", props.getProperty("gravity"));
		assertEquals("terminalFallVelocity", "0/20.0", props.getProperty("terminalFallVelocity"));
	}

}
