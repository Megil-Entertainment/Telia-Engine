package ch.megil.teliaengine.ui.component;

import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.collision.CircleCollider;
import ch.megil.teliaengine.physics.collision.Collider;
import ch.megil.teliaengine.physics.collision.EmptyCollider;
import ch.megil.teliaengine.physics.collision.RectangleCollider;
import ch.megil.teliaengine.physics.collision.TriangleCollider;
import ch.megil.teliaengine.ui.shape.EditableCircle;
import ch.megil.teliaengine.ui.shape.EditableRectangle;
import ch.megil.teliaengine.ui.shape.EditableTriangle;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ColliderEditor extends Pane {
	private static final double DEFAULT_MAX = 50;
	private ColliderType type;
	private ImageView objectView;
	private Pane colliderShape;
	
	public ColliderEditor() {
		setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		
		objectView = new ImageView();
		objectView.fitWidthProperty().bind(widthProperty());
		objectView.fitHeightProperty().bind(heightProperty());
		getChildren().add(objectView);
	}
	
	public void setObjectImage(Image object) {
		objectView.setImage(object);
	}
	
	public void setColliderType(ColliderType type) {
		getChildren().remove(colliderShape);
		this.type = type;
		switch (type) {
			case NONE:
				return;
			case RECTANGLE:
				colliderShape = new EditableRectangle(10, 20, 120, 60, Color.AQUA);
				break;
			case TRIANGLE:
				colliderShape = new EditableTriangle(3, 3, 13, 13, 3, 13, Color.AQUA);
				break;
			case CIRCLE:
				colliderShape = new EditableCircle(20, 20, 20, Color.AQUA);
				break;
		}
		getChildren().add(colliderShape);
	}
	
	public Collider getCollider() {
		switch (type) {
			case RECTANGLE:
				var rect = (EditableRectangle) colliderShape;
				return new RectangleCollider(new Vector(rect.getOriginX(), rect.getOriginY()), rect.getSizeWidth(), rect.getSizeHeight());
			case TRIANGLE:
				var triangle = (EditableTriangle) colliderShape;
				return new TriangleCollider(new Vector(triangle.getP0X(), triangle.getP0Y()), new Vector(triangle.getP1X(), triangle.getP1Y()), new Vector(triangle.getP2X(), triangle.getP2Y()));
			case CIRCLE:
				var circle = (EditableCircle) colliderShape;
				return new CircleCollider(new Vector(circle.getCenterX(), circle.getCenterY()), circle.getRadius());
			case NONE:
			default:
				return new EmptyCollider();
		}
	}
}
