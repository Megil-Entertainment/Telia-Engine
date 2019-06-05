package ch.megil.teliaengine.file;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.collision.*;

public class ColliderConverter {
	private static final String RECTANGLE = "rectangle";
	private static final String CIRCLE = "circle";
	private static final String TRIANGLE = "triangle";
	private static final String COMPOSITE = "composite";
	
	/*
	 * None
	 * RectangleCollider
	 * 		rectangle:originX:originY:width:height
	 * CircleCollider
	 * 		circle:centerX:centerY:radius
	 * TriangleCollider
	 * 		triangle:p0x:p0y:p1x:p1y:p2x:p2y
	 * CompositeCollider
	 * 		composite:;collider;collider;...
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
			var compSpec = colliderStr.split(FileConfiguration.SEPERATOR_COMPOSITE_PROPERTY.getConfiguration());
			var colliders = new Collider[compSpec.length - 1];
			for (var i = 1; i < compSpec.length; i++) {
				colliders[i-1] = convertToCollider(compSpec[i]);
			}
			return new CompositeCollider(colliders);
		} else {
			return new EmptyCollider();
		}
	}
	
	public String convertToEntryString(Collider collider) {
		var propSeperator = FileConfiguration.SEPERATOR_PROPERTY.getConfiguration();
		if (collider instanceof RectangleCollider) {
			var origin = collider.getBoundingBoxBegin();
			var size = collider.getBoundingBoxEnd().subtract(origin);
			return RECTANGLE + propSeperator + origin.getX() + propSeperator + origin.getY() + propSeperator +
					size.getX() + propSeperator + size.getY();
		} else if (collider instanceof CircleCollider) {
			var circle = (CircleCollider) collider;
			return CIRCLE + propSeperator + circle.getCenter().getX() + propSeperator + circle.getCenter().getY() +
					propSeperator + circle.getRadius();
		} else if (collider instanceof TriangleCollider) {
			var triangle = (TriangleCollider) collider;
			return TRIANGLE + propSeperator + triangle.getP0().getX() + propSeperator + triangle.getP0().getY() +
					propSeperator + triangle.getP1().getX() + propSeperator + triangle.getP1().getY() +
					propSeperator + triangle.getP2().getX() + propSeperator + triangle.getP2().getY();
		} else if (collider instanceof CompositeCollider) {
			var compPropSeperator = FileConfiguration.SEPERATOR_COMPOSITE_PROPERTY.getConfiguration();
			var colliderStr = COMPOSITE + propSeperator;
			for (var coll : ((CompositeCollider)collider).getColliders()) {
				colliderStr = colliderStr + compPropSeperator + convertToEntryString(coll);
			}
			return colliderStr;
		} else {
			return "";
		}
	}
}
