package ch.megil.teliaengine.ui.component;

import java.util.logging.Level;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.file.GameObjectSaveLoad;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.logging.LogHandler;
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
		
		new GameObjectSaveLoad().loadAll().forEach(this::loadGameObject);
		
	}
	
	public void loadGameObject(GameObject obj) {
		var listItem = new GameObjectListItem();
		listItem.setGameObject(obj);
		listItem.setBgColor(Color.web(SystemConfiguration.OBJECT_LIST_BG.getConfiguration()));
		listItem.setHoverColor(Color.web(SystemConfiguration.OBJECT_LIST_HOVER.getConfiguration()));
		listItem.setOnAction(this::createNewObject);
		container.getChildren().add(listItem);
	}
	
	public void createNewObject(GameObject object) {
		try {
			var newObject = new GameObjectSaveLoad().load(object.getName());
			mapEditor.addGameObject(newObject);
		} catch (AssetNotFoundException | AssetFormatException e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	public void setMapEditor(MapEditor mapEditor) {
		this.mapEditor = mapEditor;
	}
	
}
