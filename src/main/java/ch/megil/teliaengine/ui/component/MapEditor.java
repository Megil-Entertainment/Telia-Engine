package ch.megil.teliaengine.ui.component;

import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.game.player.Player;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MapEditor extends Pane{
	private Map map;
	
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
	}
	
	public void addGameObject(GameObject obj) {
		if (map != null) {
			map.addObject(obj);
			getChildren().add(obj.getDepiction());
		}
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
	
	public void setMap(Map map) {
		getChildren().clear();
		
		this.map = map;
		map.getMapObjects().forEach(o -> getChildren().add(o.getDepiction()));
		getChildren().add(Player.get().getDepiction());
	}
}
