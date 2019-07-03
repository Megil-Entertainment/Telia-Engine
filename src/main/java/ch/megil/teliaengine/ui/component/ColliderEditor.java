package ch.megil.teliaengine.ui.component;

import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.collision.CircleCollider;
import ch.megil.teliaengine.physics.collision.Collider;
import ch.megil.teliaengine.physics.collision.EmptyCollider;
import ch.megil.teliaengine.physics.collision.RectangleCollider;
import ch.megil.teliaengine.physics.collision.TriangleCollider;
import ch.megil.teliaengine.ui.shape.EditableCircle;
import ch.megil.teliaengine.ui.shape.EditableRectangle;
import ch.megil.teliaengine.ui.shape.EditableShape;
import ch.megil.teliaengine.ui.shape.EditableTriangle;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ColliderEditor extends Pane {
	private static final double SCROLL_FACTOR = 1.1;
	private static final double DEFAULT_ORIGIN = 0;
	private static final double DEFAULT_MAX = 50;
	
	private ColliderType type;
	private ScrollPane scrollPane;
	private Pane content;
	private ImageView objectView;
	private Pane colliderShape;
	
	public ColliderEditor() {
		setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		
		addEventFilter(ScrollEvent.SCROLL, this::onScroll);
		
		scrollPane = new ScrollPane();
		scrollPane.prefWidthProperty().bind(widthProperty());
		scrollPane.prefHeightProperty().bind(heightProperty());
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		getChildren().add(scrollPane);
		
		content = new Pane();
		scrollPane.setContent(new Group(content));
		
		objectView = new ImageView();
		content.getChildren().add(objectView);
	}
	
	private void onScroll(ScrollEvent e) {
		if (e.isControlDown()) {
			var scrollFactor = e.getDeltaY() > 0 ? SCROLL_FACTOR : 1/SCROLL_FACTOR;
			content.setScaleX(content.getScaleX() * scrollFactor);
			content.setScaleY(content.getScaleY() * scrollFactor);
			((EditableShape)colliderShape).setSizeFactor(1/content.getScaleX());
			e.consume();
		}
	}
	
	public void setObjectImage(Image object) {
		objectView.setImage(object);
	}
	
	public void setColliderType(ColliderType type) {
		content.getChildren().remove(colliderShape);
		this.type = type;
		var imgWidth = objectView.getImage() != null ? objectView.getImage().getWidth() : DEFAULT_MAX;
		var imgHeight = objectView.getImage() != null ? objectView.getImage().getHeight() : DEFAULT_MAX;
		switch (type) {
			case NONE:
				return;
			case RECTANGLE:
				colliderShape = new EditableRectangle(DEFAULT_ORIGIN, DEFAULT_ORIGIN, imgWidth, imgHeight, Color.AQUA);
				break;
			case TRIANGLE:
				colliderShape = new EditableTriangle(DEFAULT_ORIGIN, DEFAULT_ORIGIN, imgWidth, imgHeight, DEFAULT_ORIGIN, imgHeight, Color.AQUA);
				break;
			case CIRCLE:
				var radius = imgWidth < imgHeight ? imgWidth/2 : imgHeight/2;
				colliderShape = new EditableCircle(DEFAULT_ORIGIN+radius, DEFAULT_ORIGIN+radius, radius, Color.AQUA);
				break;
		}
		((EditableShape)colliderShape).setSizeFactor(1/content.getScaleX());
		content.getChildren().add(colliderShape);
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
