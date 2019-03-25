package ch.megil.teliaengine.configuration;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;

import ch.megil.teliaengine.game.Vector;
import ch.megil.teliaengine.logging.LogHandler;
import ch.megil.teliaengine.project.ProjectController;

public enum PhysicsConstants {
	WALK_SPEED_RIGHT("walkSpeedR"),
	WALK_SPEED_LEFT("walkSpeedL"),
	JUMP_FORCE("jumpForce"),
	GRAVITY("gravity"),
	TERMINAL_FALL_VELOCITY("terminalFallVelocity");
	
	private static Properties physicsProperties;
	
	static {
		reload();
	}

	private static void load(PhysicsConstants phConst) {
		var vecComp = physicsProperties.getProperty(phConst.key).split("/");
		phConst.v = new Vector(Double.parseDouble(vecComp[0]), Double.parseDouble(vecComp[1]));
	}
	
	public static void reload() {
		physicsProperties = new XProperties();

		try (var in = new FileInputStream(ProjectController.get().getProjectPath() + ConfigurationContstants.PHYSIC_CONSTANTS)) {
			physicsProperties.load(in);
		} catch (Exception e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	private String key;
	private Vector v;
	
	private PhysicsConstants(String key) {
		this.key = key;
	}
	
	public Vector get() {
		if (v == null) {
			load(this);
		}
		return v;
	}
}
