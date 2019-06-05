package ch.megil.teliaengine.file;

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
		if (colliderStr.startsWith(RECTANGLE)) {
			return new RectangleCollider(Vector.ZERO, 5, 5);
		} else if (colliderStr.startsWith(CIRCLE)) {
			return new CircleCollider(Vector.ZERO, 5);
		} else if (colliderStr.startsWith(TRIANGLE)) {
			return new TriangleCollider(Vector.ZERO, Vector.ZERO, Vector.ZERO);
		} else if (colliderStr.startsWith(COMPOSITE)) {
			return new CompositeCollider();
		} else {
			return null;//TODO: none
		}
	}
	
	public String convertToEntryString(Collider collider) {
		return "Rectangle:0:0:5:5";
	}
}
