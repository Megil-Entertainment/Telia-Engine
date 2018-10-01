package ch.megil.teliaengine.base;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MapEditor extends Pane{
	private double dx;
	private double dy;
	
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
