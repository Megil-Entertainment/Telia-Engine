package ch.megil.teliaengine.base;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MapEditor extends Pane{
	private double mouseX;
	private double mouseY;
	
	public void setOnMouseDragged(MouseEvent event) {
		onMousePressed(event);
		double deltaX = event.getSceneX() - mouseX;
		double deltaY = event.getSceneY() - mouseY;
		relocate(getLayoutX() + deltaX, getLayoutY() + deltaY);
		mouseX = event.getSceneX() ;
        mouseY = event.getSceneY() ;
	}
	
	private void onMousePressed(MouseEvent event) {
		mouseX = event.getSceneX();
		mouseY = event.getSceneY();
	}
}
