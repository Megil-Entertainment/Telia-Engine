package ch.megil.teliaengine.game.player;

import java.util.List;

import ch.megil.teliaengine.configuration.PhysicsConstants;
import ch.megil.teliaengine.file.PlayerFileManager;
import ch.megil.teliaengine.file.exception.AssetLoadException;
import ch.megil.teliaengine.game.GameElement;
import ch.megil.teliaengine.game.Hitbox;
import ch.megil.teliaengine.game.Vector;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Player extends GameElement{
	private static Player instance;
	
	private boolean jumpUsed;
	
	private Vector acceleration;
	private Vector velocity;

	
	protected Player(String depictionName, Image depiction, Hitbox hitbox, Color color) {
		super(depictionName, depiction, hitbox, color);
		jumpUsed = false;
		
		acceleration = Vector.ZERO;
		velocity = Vector.ZERO;
		
		super.setPosition(Vector.ZERO);

	}
	
	/**
	 * Returns the current player instance.
	 * @throws RuntimeException If not initialized and Player can not be loaded.
	 */
	public static Player get() throws RuntimeException {
		if (instance == null) {
			try {
			reload();
			} catch (AssetLoadException e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}
	
	public static void reload() throws AssetLoadException {
		instance = new PlayerFileManager().load(Player::new);
	}
	
	public static Player getEngineCopy() {
		var player = get();
		var hitboxSize = player.getHitbox().getVectorSize();
		
		var enginePlayer = new Player(player.getDepictionName(), player.getDepiction(), new Hitbox(player.getPosition(), hitboxSize.getX(), hitboxSize.getY()), player.getColor());
		enginePlayer.setPosition(player.getPosition());
		
		return enginePlayer;
	}
	
	public void applyForce(Vector f) {
		acceleration = acceleration.add(f);
	}
	
	public void applyAcceleration(Vector a) {
		velocity = velocity.add(a);
	}
	
	public void update(List<Hitbox> possibleCollisions) {
		velocity = velocity.add(acceleration);
		velocity = new Vector(velocity.getX(), Math.min(velocity.getY(), PhysicsConstants.TERMINAL_FALL_VELOCITY.get().getY()));

		for (var v : velocity.splitToComponentSizeOne()) {
			//x collision
			var np = getPosition().add(v.xVector());
			setPosition(np);
			if (possibleCollisions.stream().anyMatch(getHitbox()::checkCollision)) {
				np = getPosition().add(v.xVector().negate());
				setPosition(np);
			}
			
			//y collision
			np = getPosition().add(v.yVector());
			setPosition(np);
			if (possibleCollisions.stream().anyMatch(getHitbox()::checkCollision)) {
				if (v.getY() > 0) {
					jumpUsed = false;
					acceleration = new Vector(acceleration.getX(), 0);
					velocity = new Vector(velocity.getX(), 0);
				}
				np = getPosition().add(v.yVector().negate());
				setPosition(np);
			}
		}
		
		setPosition(getPosition().round());
	}
	
	public boolean isJumpUsed() {
		return jumpUsed;
	}
	
	public void useJump() {
		jumpUsed = true;
	}
}
