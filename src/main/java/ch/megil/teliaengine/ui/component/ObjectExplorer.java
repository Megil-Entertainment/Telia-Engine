package ch.megil.teliaengine.ui.component;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ObjectExplorer extends Pane{
	private MapEditor mapEditor;
	
	public ObjectExplorer() {
		getChildren().addListener((ListChangeListener<Node>) c -> {
			while(c.next()) {
				c.getAddedSubList().forEach(n -> {
					n.setOnMousePressed(this::createNewObject);
					});
			}});
		
		getChildren().add(new Rectangle(50,50,Color.BLUE));
		getChildren().add(new Rectangle(50,50,Color.RED));
	}
	
	public void createNewObject(MouseEvent event) {
		if(event.isPrimaryButtonDown()) {
			var source = (Node) event.getSource();
			getChildren().remove(source);
			mapEditor.getChildren().add(source);
			source.setLayoutX(mapEditor.getLayoutX());
			source.setLayoutY(mapEditor.getLayoutY());
		}
	}
	
	public void setMapEditor(MapEditor mapEditor) {
		this.mapEditor = mapEditor;
	}
}
