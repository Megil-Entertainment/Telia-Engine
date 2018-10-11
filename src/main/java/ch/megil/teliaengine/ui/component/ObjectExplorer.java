package ch.megil.teliaengine.ui.component;

import ch.megil.teliaengine.file.GameObjectSaveLoad;
import ch.megil.teliaengine.game.GameObject;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ObjectExplorer extends ScrollPane{
	private VBox container;
	
	private MapEditor mapEditor;
	
	public ObjectExplorer() {
		setFitToWidth(true);
		
		container = new VBox();
		container.setFillWidth(true);
		setContent(container);
		
//		getChildren().addListener((ListChangeListener<Node>) c -> {
//			while(c.next()) {
//				c.getAddedSubList().forEach(n -> {
//					n.setOnMousePressed(this::createNewObject);
//					});
//			}});
		
		new GameObjectSaveLoad().loadAll().forEach(this::loadGameObject);
		
	}
	
	public void loadGameObject(GameObject obj) {
		var listItem = new GameObjectListItem();
		listItem.setGameObject(obj);
		listItem.setHoverColor(Color.LIME);
		listItem.setOnAction(this::createNewObject);
		container.getChildren().add(listItem);
	}
	
	public void createNewObject(GameObject object) {
		var newObject = new GameObjectSaveLoad().load(object.getName());
		mapEditor.addGameObject(newObject);
	}
	
	public void setMapEditor(MapEditor mapEditor) {
		this.mapEditor = mapEditor;
	}
	
}
