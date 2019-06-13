package ch.megil.teliaengine.ui.shape;

import java.util.function.BiConsumer;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

public class EditableTriangle extends Pane {
	private BiConsumer<Double, Double> onP0Change;
	private BiConsumer<Double, Double> onP1Change;
	private BiConsumer<Double, Double> onP2Change;
	
	private Line edge0;
	private Line edge1;
	private Line edge2;
	
	private EditableVertex p0;
	private EditableVertex p1;
	private EditableVertex p2;
	
	public EditableTriangle(double x0, double y0, double x1, double y1, double x2, double y2) {
		onP0Change = (x, y) -> {};
		onP1Change = (x, y) -> {};
		onP2Change = (x, y) -> {};
		
		edge0 = new Line(x0, y0, x1, y1);
		edge1 = new Line(x1, y1, x2, y2);
		edge2 = new Line(x2, y2, x0, y0);
		
		p0 = new EditableVertex(x0, y0);
		p0.setOnDragPosition(createDragListener(edge0, edge2, onP0Change));
		p1 = new EditableVertex(x1, y1);
		p1.setOnDragPosition(createDragListener(edge1, edge0, onP1Change));
		p2 = new EditableVertex(x2, y2);
		p2.setOnDragPosition(createDragListener(edge2, edge1, onP2Change));
		
		getChildren().add(edge0);
		getChildren().add(edge1);
		getChildren().add(edge2);

		getChildren().add(p0);
		getChildren().add(p1);
		getChildren().add(p2);
	}
	
	public EditableTriangle(double x0, double y0, double x1, double y1, double x2, double y2, Paint stroke) {
		this(x0, y0, x1, y1, x2, y2);
		p0.setStroke(stroke);
		p1.setStroke(stroke);
		p2.setStroke(stroke);
		edge0.setStroke(stroke);
		edge1.setStroke(stroke);
		edge2.setStroke(stroke);
	}
	
	private BiConsumer<Double, Double> createDragListener(Line edgeStart, Line edgeEnd, BiConsumer<Double, Double> onDragListener) {
		return (x, y) -> {
			edgeStart.setStartX(x);
			edgeStart.setStartY(y);
			edgeEnd.setEndX(x);
			edgeEnd.setEndY(y);
			onDragListener.accept(x, y);
		};
	}
	
	public void setOnP0Change(BiConsumer<Double, Double> onP0Change) {
		this.onP0Change = onP0Change;
	}
	
	public void setOnP1Change(BiConsumer<Double, Double> onP1Change) {
		this.onP1Change = onP1Change;
	}
	
	public void setOnP2Change(BiConsumer<Double, Double> onP2Change) {
		this.onP2Change = onP2Change;
	}
}
