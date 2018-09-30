package ch.megil.teliaengine.base;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MapEditor extends Pane{
	private double mouseX;
	private double mouseY;
	
	@FXML
	public void setOnMouseDragged(MouseEvent event) {
		onMousePressed(event);
		var source = (Node) event.getSource();
		double deltaX = source.getLayoutX() - mouseX;
		double deltaY = source.getLayoutY() - mouseY;
		relocate(getLayoutX() + deltaX, getLayoutY() + deltaY);
		mouseX = event.getSceneX() ;
        mouseY = event.getSceneY() ;
	}
	
	private void onMousePressed(MouseEvent event) {
		mouseX = event.getSceneX();
		mouseY = event.getSceneY();
	}
	
	@FXML
	public void dragPane(MouseEvent event) {
		if(event.isSecondaryButtonDown()) {
			setOnMouseDragged(event);
			event.consume();
		}
	}
	
	@FXML
	public void dragNode(MouseEvent event) {
		if(event.isPrimaryButtonDown()) {
			setOnMouseDragged(event);
			event.consume();
		}
	}
}
