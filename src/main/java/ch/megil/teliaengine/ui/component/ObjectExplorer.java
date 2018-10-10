package ch.megil.teliaengine.ui.component;

import ch.megil.teliaengine.file.GameObjectSaveLoad;
import ch.megil.teliaengine.game.GameObject;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class ObjectExplorer extends Pane{
	private MapEditor mapEditor;
	private ListView<Node> list = new ListView<Node>();
	
	public ObjectExplorer() {
		getChildren().addListener((ListChangeListener<Node>) c -> {
			while(c.next()) {
				c.getAddedSubList().forEach(n -> {
					n.setOnMousePressed(this::createNewObject);
					});
			}});
		new GameObjectSaveLoad().loadAll().forEach(this::loadGameObject);
		this.getChildren().add(list);
	}
	
	public void loadGameObject(GameObject obj) {
		getChildren().add(obj.getDepiction());
		list.getItems().add(obj.getDepiction());
	}
	
	public void createNewObject(MouseEvent event) {
		if(event.isPrimaryButtonDown() && (!ListView.class.isInstance(event.getSource())) ) {
			System.out.println(event.getSource());
			var source = (Node) event.getSource();
			getChildren().remove(source);
			mapEditor.getChildren().add(source);
			source.setLayoutX(mapEditor.getLayoutX());
			source.setLayoutY(mapEditor.getLayoutY());
		}
	}
	
	public void fillList() {
		
	}
	
	public void setMapEditor(MapEditor mapEditor) {
		this.mapEditor = mapEditor;
	}
	
}
