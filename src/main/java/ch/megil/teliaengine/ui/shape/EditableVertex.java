package ch.megil.teliaengine.ui.shape;

import java.util.function.BiConsumer;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class EditableVertex extends Rectangle implements EditableShape {
	private static final double SIZE = 6;
	private static final double OFFSET = SIZE / 2;
	
	private double offset;
	private BiConsumer<Double, Double> onDragPosition;
	
	public EditableVertex(double x, double y) {
		super(x - OFFSET, y - OFFSET, SIZE, SIZE);
		offset = OFFSET;
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
		setX(event.getX() - offset);
		setY(event.getY() - offset);
		onDragPosition.accept(event.getX(), event.getY());
	}
	
	@Override
	public void setSizeFactor(double sizeFactor) {
		setStrokeWidth(sizeFactor);
		var x = getPositionX();
		var y = getPositionY();
		offset = OFFSET * sizeFactor;
		setWidth(SIZE * sizeFactor);
		setHeight(SIZE * sizeFactor);
		setPosition(x, y);
	}
	
	public void setPosition(double x, double y) {
		setX(x - offset);
		setY(y - offset);
	}
	
	public double getPositionX() {
		return getX() + offset;
	}
	
	public double getPositionY() {
		return getY() + offset;
	}
	
	public void setOnDragPosition(BiConsumer<Double, Double> onDragPosition) {
		this.onDragPosition = onDragPosition;
	}
}
