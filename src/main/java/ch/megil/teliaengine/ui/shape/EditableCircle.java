package ch.megil.teliaengine.ui.shape;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class EditableCircle extends Pane {
	private BiConsumer<Double, Double> onCenterMove;
	private Consumer<Double> onRadiusChange;
	
	private Circle circle;
	private EditableVertex center;
	private EditableVertex size;
	
	public EditableCircle(double centerX, double centerY, double radius) {
		onCenterMove = (x, y) -> {};
		onRadiusChange = r -> {};
		
		circle = new Circle(centerX, centerY, radius);
		circle.setFill(Color.TRANSPARENT);
		circle.setStroke(Color.BLACK);
		
		center = new EditableVertex(centerX, centerY);
		center.setOnDragPosition((x, y) -> {
			circle.setCenterX(x);
			circle.setCenterY(y);
			size.setPosition(x+circle.getRadius(), y);
			onCenterMove.accept(x, y);
		});
		size = new EditableVertex(centerX+radius, centerY);
		size.setOnDragPosition((x, y) -> {
			var r = Math.sqrt(Math.pow(x - center.getPositionX(), 2) + Math.pow(y - center.getPositionY(), 2));
			circle.setRadius(r);
			onRadiusChange.accept(r);
		});
		
		getChildren().add(circle);
		getChildren().add(center);
		getChildren().add(size);
	}
	
	public EditableCircle(double centerX, double centerY, double radius, Paint stroke) {
		this(centerX, centerY, radius);
		circle.setStroke(stroke);
		center.setStroke(stroke);
		size.setStroke(stroke);
	}
	
	public void setOnCenterMove(BiConsumer<Double, Double> onCenterMove) {
		this.onCenterMove = onCenterMove;
	}
	
	public void setOnRadiusChange(Consumer<Double> onRadiusChange) {
		this.onRadiusChange = onRadiusChange;
	}
}
