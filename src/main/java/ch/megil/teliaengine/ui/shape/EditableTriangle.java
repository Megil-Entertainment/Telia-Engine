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
		p0.setOnDragPosition((x, y) -> {
			edge0.setStartX(x);
			edge0.setStartY(y);
			edge2.setEndX(x);
			edge2.setEndY(y);
			onP0Change.accept(x, y);
			});
		p1 = new EditableVertex(x1, y1);
		p1.setOnDragPosition((x, y) -> {
			edge1.setStartX(x);
			edge1.setStartY(y);
			edge0.setEndX(x);
			edge0.setEndY(y);
			onP1Change.accept(x, y);
			});
		p2 = new EditableVertex(x2, y2);
		p2.setOnDragPosition((x, y) -> {
			edge2.setStartX(x);
			edge2.setStartY(y);
			edge1.setEndX(x);
			edge1.setEndY(y);
			onP2Change.accept(x, y);
			});
		
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
