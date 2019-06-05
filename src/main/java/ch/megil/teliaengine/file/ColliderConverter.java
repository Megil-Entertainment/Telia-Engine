package ch.megil.teliaengine.file;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.collision.CircleCollider;
import ch.megil.teliaengine.physics.collision.Collider;
import ch.megil.teliaengine.physics.collision.CompositeCollider;
import ch.megil.teliaengine.physics.collision.RectangleCollider;
import ch.megil.teliaengine.physics.collision.TriangleCollider;

public class ColliderConverter {
	private static final String RECTANGLE = "rectangle";
	private static final String CIRCLE = "circle";
	private static final String TRIANGLE = "triangle";
	private static final String COMPOSITE = "composite";
	
	/*
	 * None??
	 * RectangleCollider
	 * 		rectangle:originX:originY:width:height
	 * CircleCollider
	 * 		circle:centerX:centerY:radius
	 * TriangleCollider
	 * 		triangle:p0x:p0y:p1x:p1y:p2x:p2y
	 * CompositeCollider
	 * 		composite:{collider;...}
	 */
	public Collider convertToCollider(String colliderStr) {
		var spec = colliderStr.split(FileConfiguration.SEPERATOR_PROPERTY.getConfiguration());
		if (spec[0].equals(RECTANGLE)) {
			return new RectangleCollider(
					new Vector(Double.parseDouble(spec[1]), Double.parseDouble(spec[2])),
					Double.parseDouble(spec[3]),
					Double.parseDouble(spec[4]));
		} else if (spec[0].equals(CIRCLE)) {
			return new CircleCollider(
					new Vector(Double.parseDouble(spec[1]), Double.parseDouble(spec[2])),
					Double.parseDouble(spec[3]));
		} else if (spec[0].equals(TRIANGLE)) {
			return new TriangleCollider(
					new Vector(Double.parseDouble(spec[1]), Double.parseDouble(spec[2])),
					new Vector(Double.parseDouble(spec[3]), Double.parseDouble(spec[4])),
					new Vector(Double.parseDouble(spec[5]), Double.parseDouble(spec[6])));
		} else if (spec[0].equals(COMPOSITE)) {
			return new CompositeCollider();
		} else {
			return null;//TODO: none
		}
	}
	
	public String convertToEntryString(Collider collider) {
		return "Rectangle:0:0:5:5";
	}
}
