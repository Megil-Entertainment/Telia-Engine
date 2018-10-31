package ch.megil.teliaengine.ui.component;

import java.util.HashMap;

import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.ui.MyImageView;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MapEditor extends Pane{
	private Map map;
	private HashMap<Node, Integer> gameObjectIndexes;
	private Player player;
	private double playerWidth;
	private double playerHeight;
	
	private double dx;
	private double dy;
	
	public MapEditor() {
		gameObjectIndexes = new HashMap<>();
		playerWidth = Player.getEngine().getHitbox().getVectorSize().getX();
		playerHeight = Player.getEngine().getHitbox().getVectorSize().getY();
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
			getChildren().add(new MyImageView(obj));
			gameObjectIndexes.put(new MyImageView(obj), map.getMapObjects().indexOf(obj));
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
		checkBoundries(source, map);
	}
	
	private void checkBoundries(Node source, Map map) {
		double sourceWidth;
		double sourceHeight;
		if(gameObjectIndexes.containsKey(source)) {
			int sourceIndex = gameObjectIndexes.get(source);
			sourceWidth = map.getMapObjects().get(sourceIndex).getHitbox().getVectorSize().getX();
			sourceHeight = map.getMapObjects().get(sourceIndex).getHitbox().getVectorSize().getY();
		}else {
			sourceWidth = playerWidth;
			sourceHeight = playerHeight;
		}

		if(source.getLayoutX() < 0) {
		source.setLayoutX(0);
		}
		if(source.getLayoutX() + sourceWidth > map.getWidth()) {
			source.setLayoutX(map.getWidth() - sourceWidth);
		}
		if(source.getLayoutY() < 0) {
			source.setLayoutY(0);
		}
		if(source.getLayoutY() + sourceHeight > map.getHeight()) {
			source.setLayoutY(map.getHeight() - sourceHeight);
		}
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
		map.getMapObjects().forEach(o -> getChildren().add(new MyImageView(o)));
		getChildren().add(new MyImageView(Player.getEngine()));
		player = Player.getEngine();
		
	}
}
