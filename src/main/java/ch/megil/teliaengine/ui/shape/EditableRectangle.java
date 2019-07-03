package ch.megil.teliaengine.ui.shape;

import java.util.function.BiConsumer;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class EditableRectangle extends Pane {
	private BiConsumer<Double, Double> onOriginChange;
	private BiConsumer<Double, Double> onSizeChange;
	
	private Rectangle rectangle;
	private EditableVertex p0;
	private EditableVertex p1;
	private EditableVertex p2;
	private EditableVertex p3;
	
	public EditableRectangle(double originX, double originY, double width, double height) {
		onOriginChange = (x, y) -> {};
		onSizeChange = (w, h) -> {};
		
		rectangle = new Rectangle(originX, originY, width, height);
		rectangle.setFill(Color.TRANSPARENT);
		rectangle.setStroke(Color.BLACK);
		
		p0 = new EditableVertex(originX, originY);
		p1 = new EditableVertex(originX+width, originY);
		p2 = new EditableVertex(originX+width, originY+height);
		p3 = new EditableVertex(originX, originY+height);
		p0.setOnDragPosition(createDragListener(p2, p1, p3));
		p1.setOnDragPosition(createDragListener(p3, p0, p2));
		p2.setOnDragPosition(createDragListener(p0, p3, p1));
		p3.setOnDragPosition(createDragListener(p1, p2, p0));
		
		getChildren().add(rectangle);
		getChildren().add(p0);
		getChildren().add(p1);
		getChildren().add(p2);
		getChildren().add(p3);
	}
	
	public EditableRectangle(double x, double y, double width, double height, Paint stroke) {
		this(x, y, width, height);
		setStroke(stroke);
	}
	
	private BiConsumer<Double, Double> createDragListener(EditableVertex offsetCalc, EditableVertex changeX, EditableVertex changeY) {
		return (x, y) -> {
			var offsetX = offsetCalc.getPositionX() - x;
			var offsetY = offsetCalc.getPositionY() - y;
			
			changeX.setPosition(x+offsetX, y);
			changeY.setPosition(x, y+offsetY);
		
			updateRectangle();
		};
	}
	
	private void updateRectangle() {
		var x = p0.getPositionX();
		var y = p0.getPositionY();
		var w = p2.getPositionX() - x;
		var h = p2.getPositionY() - y;
		
		if (w < 0) {
		} else {
			x = x+w;
			w = 0-w;
		}
		rectangle.setX(x);
		rectangle.setWidth(w);
		if (h < 0) {
			y = y+h;
			h = 0-h;
		}
		rectangle.setY(y);
		rectangle.setHeight(h);
		
		onOriginChange.accept(x, y);
		onSizeChange.accept(w, h);
	}
	
	public void setStroke(Paint stroke) {
		rectangle.setStroke(stroke);
		p0.setStroke(stroke);
		p1.setStroke(stroke);
		p2.setStroke(stroke);
		p3.setStroke(stroke);
	}
	
	public void setOnOriginChange(BiConsumer<Double, Double> onOriginChange) {
		this.onOriginChange = onOriginChange;
	}
	
	public void setOnSizeChange(BiConsumer<Double, Double> onSizeChange) {
		this.onSizeChange = onSizeChange;
	}
	
	public double getOriginX() {
		return rectangle.getX();
	}
	
	public double getOriginY() {
		return rectangle.getY();
	}
	
	public double getSizeWidth() {
		return rectangle.getWidth();
	}
	
	public double getSizeHeight() {
		return rectangle.getHeight();
	}
}
