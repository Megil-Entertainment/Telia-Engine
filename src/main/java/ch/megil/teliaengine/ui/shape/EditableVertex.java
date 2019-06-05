package ch.megil.teliaengine.ui.shape;

import java.util.function.BiConsumer;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class EditableVertex extends Rectangle {
	private static final double SIZE = 6;
	private static final double OFFSET = SIZE / 2;
	
	private BiConsumer<Double, Double> onPositionChange;
	
	public EditableVertex(double x, double y) {
		super(x - OFFSET, y - OFFSET, SIZE, SIZE);
		setFill(Color.TRANSPARENT);
		setStroke(Color.BLACK);
		this.onPositionChange = (a, b) -> {};

		setOnMouseDragged(this::onDrag);
	}
	
	public EditableVertex(double x, double y, Paint stroke) {
		this(x, y);
		setStroke(stroke);
	}
	
	private void onDrag(MouseEvent event) {
		setX(event.getX() - OFFSET);
		setY(event.getY() - OFFSET);
		onPositionChange.accept(event.getX(), event.getY());
	}
	
	public double getPositionX() {
		return getX() + OFFSET;
	}
	
	public double getPositionY() {
		return getY() + OFFSET;
	}
	
	public void setOnPositionChange(BiConsumer<Double, Double> onPositionChange) {
		this.onPositionChange = onPositionChange;
	}
}
