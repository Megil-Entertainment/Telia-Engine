package ch.megil.teliaengine.ui.component;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MapEditor extends Pane{
	private double dx;
	private double dy;
	
	public MapEditor() {
		getChildren().addListener((ListChangeListener<Node>) c -> {
			while(c.next()) {
				c.getAddedSubList().forEach(n -> {
					n.setOnMousePressed(this::onDragStart);
					n.setOnMouseDragged(this::onDragNode);
					});
			}});
		
		getChildren().add(new Rectangle(50,50,Color.BLUE));
		getChildren().add(new Rectangle(50,50,Color.RED));
	}
	
	public void onDragStart(MouseEvent event) {
		var source = (Node) event.getSource();
		dx = source.getLayoutX() - event.getSceneX();
		dy = source.getLayoutY() - event.getSceneY();
	}
	
	private void moveNode(MouseEvent event) {
		var source = (Node) event.getSource();
		source.setLayoutX(event.getSceneX() + dx);
		source.setLayoutY(event.getSceneY() + dy);
	}
	
	public void onDragNode(MouseEvent event) {
		if(event.isPrimaryButtonDown()) {
			moveNode(event);
			event.consume();
		}
	}
}
