package ch.megil.teliaengine.ui.component;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.ui.GameElementImageView;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class MapEditor extends Pane {
	private Map map;
	private Player player;
	private Pane pane;
	
	private double dx;
	private double dy;
	
	private int gridWidth;
	private int gridHeight;
	
	public MapEditor() {
//		pane = new Pane();
		pane = this;
		var clip = new Rectangle();
		clip.widthProperty().bind(this.widthProperty());
		clip.heightProperty().bind(this.heightProperty());
		pane.setClip(clip);
		pane.setOnMousePressed(this::onMapDragStart);
		pane.setOnMouseDragged(this::onDragMap);
//		this.getChildren().add(pane);
		pane.getChildren().addListener((ListChangeListener<Node>) c -> {
			while(c.next()) {
				c.getAddedSubList().forEach(n -> {
					n.setOnMousePressed(this::onDragStart);
					n.setOnMouseDragged(this::onDragNode);
					});
			}});
		
		gridWidth = Integer.parseInt(SystemConfiguration.MAP_GRID_WIDTH.getConfiguration());
		gridHeight = Integer.parseInt(SystemConfiguration.MAP_GRID_HEIGHT.getConfiguration());
	}
	
	public void addGameObject(GameObject obj) {
		if (map != null) {
			map.addObject(obj);
			pane.getChildren().add(new GameElementImageView(obj));
		}
	}
	
	public void onDragStart(MouseEvent event) {
		if (event.isPrimaryButtonDown()) {
			var source = (Node) event.getSource();
			
			dx = source.getLayoutX() - event.getSceneX();
			dy = source.getLayoutY() - event.getSceneY();
		}
	}
	
	public void onMapDragStart(MouseEvent event) {
		if (event.isSecondaryButtonDown()) {
			var clip = (Rectangle)pane.getClip();
			dx = clip.getX() - event.getSceneX();
			dy = clip.getY() - event.getSceneY();
		}
	}
	
	private void moveNode(MouseEvent event) {
		var source = (GameElementImageView) event.getSource();
		source.setImageViewLayoutX(
				roundToNearest((event.getSceneX() + dx), gridWidth));
		source.setImageViewLayoutY(
				roundToNearest((event.getSceneY() + dy), gridHeight));
		checkBoundries(source);
	}
	
	private void moveMap(MouseEvent event) {
		var clip = (Rectangle)pane.getClip();
		clip.setX(event.getSceneX() + dx);
		clip.setY(event.getSceneY() + dy);
		pane.setTranslateX(0 - clip.getX());
		pane.setTranslateY(0 - clip.getY());
	}
	
	private double roundToNearest(double value, int roundFactor) {
		return Math.round(value/roundFactor) * roundFactor;
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
	
	public void onDragMap(MouseEvent event) {
		if(event.isSecondaryButtonDown()) {
			moveMap(event);
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
		map.getMapObjects().forEach(o -> pane.getChildren().add(new GameElementImageView(o)));
		this.player = Player.getEngineCopy();
		pane.getChildren().add(new GameElementImageView(player));
//		pane.setMinWidth(map.getWidth());
//		pane.setMaxWidth(map.getWidth());
//		pane.setMinHeight(map.getHeight());
//		pane.setMaxHeight(map.getHeight());		
//		pane.resize(map.getWidth(), map.getHeight());;
		pane.setTranslateX(0);
		pane.setTranslateY(0);
		var clip = (Rectangle)pane.getClip();
		clip.setX(0);
		clip.setY(0);
	}
}
