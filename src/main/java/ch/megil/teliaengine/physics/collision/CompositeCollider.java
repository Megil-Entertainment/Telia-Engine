package ch.megil.teliaengine.physics.collision;

import java.util.Arrays;
import java.util.List;

import ch.megil.teliaengine.physics.Vector;

public class CompositeCollider extends Collider {
	private Vector movement;
	private List<Collider> colliders;
	
	public CompositeCollider(Collider... colliders) {
		this.movement = Vector.ZERO;
		this.colliders = Arrays.asList(colliders);
		var xmin = colliders[0].getBoundingBoxBegin().getX();
		var xmax = colliders[0].getBoundingBoxEnd().getX();
		var ymin = colliders[0].getBoundingBoxBegin().getY();
		var ymax = colliders[0].getBoundingBoxEnd().getY();
		for (var i = 1; i < colliders.length; i++) {
			xmin = Math.min(xmin, colliders[i].getBoundingBoxBegin().getX());
			xmax = Math.max(xmax, colliders[i].getBoundingBoxEnd().getX());
			ymin = Math.min(ymin, colliders[i].getBoundingBoxBegin().getY());
			ymax = Math.max(ymax, colliders[i].getBoundingBoxEnd().getY());
		}
		setBoundingBox(new Vector(xmin, ymin), new Vector(xmax, ymax));
	}
	
	@Override
	protected boolean checkDetailedCollision(Collider other) {
		if (movement.equals(Vector.ZERO)) {
			for (Collider collider : colliders) {
				if (collider.checkCollision(other)) {
					return true;
				}
			}
			return false;
		} else {
			var collision = false;
			var i = 0;
			for (; i < colliders.size(); i++) {
				colliders.get(i).move(movement);
				if (colliders.get(i).checkCollision(other)) {
					collision = true;
					break;
				}
			}
			for (; i < colliders.size(); i++) {
				colliders.get(i).move(movement);
			}
			movement = Vector.ZERO;
			return collision;
		}
	}
	
	@Override
	public void move(Vector move) {
		super.move(move);
		movement = movement.add(move);
	}
	
	protected Vector getMovement() {
		return movement;
	}
}
