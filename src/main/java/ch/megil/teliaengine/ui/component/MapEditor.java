package ch.megil.teliaengine.ui.component;

import ch.megil.teliaengine.file.GameObjectSaveLoad;
import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.game.Map;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MapEditor extends Pane{
	private Map map;
	
	private double dx;
	private double dy;
	
	public MapEditor() {
		map = new Map(1920, 1080, 50, 50);
		
		getChildren().addListener((ListChangeListener<Node>) c -> {
			while(c.next()) {
				c.getAddedSubList().forEach(n -> {
					n.setOnMousePressed(this::onDragStart);
					n.setOnMouseDragged(this::onDragNode);
					});
			}});
		
		new GameObjectSaveLoad().loadAll().forEach(this::addGameObject);
	}
	
	public void addGameObject(GameObject obj) {
		map.addObject(obj);
		getChildren().add(obj.getDepiction());
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
	
	public Map getMap() {
		return map;
	}
}
