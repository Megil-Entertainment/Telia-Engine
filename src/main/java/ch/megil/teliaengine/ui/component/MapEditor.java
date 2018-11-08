package ch.megil.teliaengine.ui.component;

import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.ui.GameElementImageView;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MapEditor extends Pane{
	private Map map;
	private Player player;
	
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
			getChildren().add(new GameElementImageView(obj));
		}
	}
	
	public void onDragStart(MouseEvent event) {
		var source = (Node) event.getSource();
		dx = source.getLayoutX() - event.getSceneX();
		dy = source.getLayoutY() - event.getSceneY();
	}
	
	private void moveNode(MouseEvent event) {
		var source = (GameElementImageView) event.getSource();
		source.setImageViewLayoutX(event.getSceneX() + dx);
		source.setImageViewLayoutY(event.getSceneY() + dy);
		checkBoundries(source);
	}
	
	private void checkBoundries(GameElementImageView imageView) {
		double sourceWidth;
		double sourceHeight;
		sourceWidth = imageView.getImage().getWidth();
		sourceHeight = imageView.getImage().getHeight();
		
		if(imageView.getLayoutX() < 0) {
		imageView.setImageViewLayoutX(0);
		}
		if(imageView.getLayoutX() + sourceWidth > map.getWidth()) {
			imageView.setImageViewLayoutX(map.getWidth() - sourceWidth);
		}
		if(imageView.getLayoutY() < 0) {
			imageView.setImageViewLayoutY(0);
		}
		if(imageView.getLayoutY() + sourceHeight > map.getHeight()) {
			imageView.setImageViewLayoutY(map.getHeight() - sourceHeight);
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
	
	public Player getPlayer() {
		return player;
	}
	
	public void setMap(Map map) {
		getChildren().clear();
		
		this.map = map;
		map.getMapObjects().forEach(o -> getChildren().add(new GameElementImageView(o)));
		this.player = Player.getEngineCopy();
		getChildren().add(new GameElementImageView(player));
		
	}
}
