package ch.megil.teliaengine.physics.collision;

import java.util.Arrays;
import java.util.List;

import ch.megil.teliaengine.physics.Vector;

public class CompositeCollider extends Collider {
	private List<Collider> colliders;
	
	public CompositeCollider(Collider... colliders) {
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
		for (Collider collider : colliders) {
			if (collider.checkCollision(other)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void move(Vector move) {
		super.move(move);
		for (Collider collider : colliders) {
			collider.move(move);
		}
	}
}
