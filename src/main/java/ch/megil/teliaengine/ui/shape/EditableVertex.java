package ch.megil.teliaengine.ui.shape;

import java.util.function.BiConsumer;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class EditableVertex extends Rectangle {
	private static final double SIZE = 6;
	private static final double OFFSET = SIZE / 2;
	
	private BiConsumer<Double, Double> onDragPosition;
	
	public EditableVertex(double x, double y) {
		super(x - OFFSET, y - OFFSET, SIZE, SIZE);
		setFill(Color.TRANSPARENT);
		setStroke(Color.BLACK);
		this.onDragPosition = (a, b) -> {};

		setOnMouseDragged(this::onDrag);
	}
	
	public EditableVertex(double x, double y, Paint stroke) {
		this(x, y);
		setStroke(stroke);
	}
	
	private void onDrag(MouseEvent event) {
		setX(event.getX() - OFFSET);
		setY(event.getY() - OFFSET);
		onDragPosition.accept(event.getX(), event.getY());
	}
	
	public void setPosition(double x, double y) {
		setX(x - OFFSET);
		setY(y - OFFSET);
	}
	
	public double getPositionX() {
		return getX() + OFFSET;
	}
	
	public double getPositionY() {
		return getY() + OFFSET;
	}
	
	public void setOnDragPosition(BiConsumer<Double, Double> onDragPosition) {
		this.onDragPosition = onDragPosition;
	}
}
