package ch.megil.teliaengine.game.player;

import ch.megil.teliaengine.file.PlayerLoad;
import ch.megil.teliaengine.game.Vector;
import javafx.scene.Node;

public final class Player {
	private static Player instance;
	private static Player engine;
	
	private Vector position;
	private Node depiction;
	
	private Vector acceleration;
	private Vector velocity;
	
	protected Player(Node depiction) {
		acceleration = Vector.ZERO;
		velocity = Vector.ZERO;
		
		this.depiction = depiction;
		
		this.position = new Vector(depiction.getLayoutX(), depiction.getLayoutY());
		
		this.depiction.layoutXProperty().bindBidirectional(position.xProperty());
		this.depiction.layoutYProperty().bindBidirectional(position.yProperty());
	}
	
	public static Player get() {
		if (instance == null) {
			instance = new PlayerLoad().load(Player::new);
		}
		return instance;
	}
	
	public static Player getEngine() {
		if (engine == null) {
			engine = new PlayerLoad().load(Player::new);
			engine.setPosX(get().getPosX());
			engine.setPosY(get().getPosY());
			engine.position.xProperty().addListener((obs, ov, nv) -> get().position.setX(nv.doubleValue()));
			engine.position.yProperty().addListener((obs, ov, nv) -> get().position.setY(nv.doubleValue()));
		}
		return engine;
	}
	
	public void applyAcceleration(Vector a) {
		acceleration = acceleration.add(a);
	}
	
	public void applyVelocity(Vector v) {
		velocity = velocity.add(v);
	}
	
	public void update() {
		velocity = velocity.add(acceleration);
		//TODO: terminate velocity
		var np = position.add(velocity);
		position.setX(np.getX());
		position.setY(np.getY());
	}
	
	public double getPosX() {
		return position.getX();
	}
	
	public void setPosX(double posX) {
		this.position.setX(posX);
	}

	public double getPosY() {
		return position.getY();
	}
	
	public void setPosY(double posY) {
		this.position.setY(posY);
	}

	public Node getDepiction() {
		return depiction;
	}
}
