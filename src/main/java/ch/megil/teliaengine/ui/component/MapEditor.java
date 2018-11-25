package ch.megil.teliaengine.ui.component;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.ui.GameElementImageView;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MapEditor extends Pane {
	private Map map;
	private Player player;
	
	private double dx;
	private double dy;
	
	private int gridWidth;
	private int gridHeight;
	
	private DropShadow nodeSelected;
	private DropShadow nodeDeselected;
	
	public MapEditor() {
		var clip = new Rectangle();
		clip.widthProperty().bind(this.widthProperty());
		clip.heightProperty().bind(this.heightProperty());
		setClip(clip);
		setOnMousePressed(this::onMapDragStart);
		setOnMouseDragged(this::onDragMap);
		getChildren().addListener((ListChangeListener<Node>) c -> {
			while(c.next()) {
				c.getAddedSubList().forEach(n -> {
					n.setOnMousePressed(this::onDragStart);
					n.setOnMouseDragged(this::onDragNode);
					n.setOnMouseReleased(this::onReleaseNode);
					});
			}});
		
		gridWidth = Integer.parseInt(SystemConfiguration.MAP_GRID_WIDTH.getConfiguration());
		gridHeight = Integer.parseInt(SystemConfiguration.MAP_GRID_HEIGHT.getConfiguration());
		
		nodeSelected = new DropShadow();
		nodeSelected.setColor(Color.DARKVIOLET);
		nodeSelected.setOffsetX(0f);
		nodeSelected.setOffsetY(0f);
		nodeSelected.setWidth(50);
		nodeSelected.setHeight(50);
		
		nodeDeselected = new DropShadow();
		nodeDeselected.setColor(Color.TRANSPARENT);
		
	}
	
	public void addGameObject(GameObject obj) {
		if (map != null) {
			map.addObject(obj);
			getChildren().add(new GameElementImageView(obj));
		}
	}
	
	public void onDragStart(MouseEvent event) {
		if (event.isPrimaryButtonDown()) {
			var source = (GameElementImageView) event.getSource();
			source.setIsSelected(true);
			source.setEffect(nodeSelected);
			dx = source.getLayoutX() - event.getSceneX();
			dy = source.getLayoutY() - event.getSceneY();
		}
	}
	
	public void onMapDragStart(MouseEvent event) {
		if (event.isSecondaryButtonDown()) {
			dx = getTranslateX() - event.getSceneX();
			dy = getTranslateY() - event.getSceneY();
		}
	}
	
	private void moveNode(MouseEvent event, GameElementImageView source) {
		source.setImageViewLayoutX(
				roundToNearest((event.getSceneX() + dx), gridWidth));
		source.setImageViewLayoutY(
				roundToNearest((event.getSceneY() + dy), gridHeight));
		checkBoundries(source);
	}
	
	private void moveMap(MouseEvent event) {
		var clip = (Rectangle) getClip();
		setTranslateX(event.getSceneX() + dx);
		setTranslateY(event.getSceneY() + dy);
		checkMapBoundries(clip);
		clip.setX(0 - getTranslateX());
		clip.setY(0 - getTranslateY());
	}
	
	private double roundToNearest(double value, int roundFactor) {
		return Math.round(value/roundFactor) * roundFactor;
	}
	
	private void checkBoundries(GameElementImageView imageView) {
		var sourceWidth = imageView.getImage().getWidth();
		var sourceHeight = imageView.getImage().getHeight();
		
		if(imageView.getLayoutX() < 0) {
			imageView.setImageViewLayoutX(0);
		} else if(imageView.getLayoutX() + sourceWidth > map.getWidth()) {
			imageView.setImageViewLayoutX(map.getWidth() - sourceWidth);
		}
		if(imageView.getLayoutY() < 0) {
			imageView.setImageViewLayoutY(0);
		} else if(imageView.getLayoutY() + sourceHeight > map.getHeight()) {
			imageView.setImageViewLayoutY(map.getHeight() - sourceHeight);
		}
	}
	
	private void checkMapBoundries(Rectangle rectangle) {
		var offsetX = rectangle.getWidth() - map.getWidth();
		var offsetY = rectangle.getHeight() - map.getHeight();
		
		if(getTranslateX() < offsetX) {
			setTranslateX(offsetX);
		} else if (getTranslateX() > 0) {
			setTranslateX(0);
		}
		
		if(getTranslateY() < offsetY) {
			setTranslateY(offsetY);
		} else if (getTranslateY() > 0) {
			setTranslateY(0);
		}
	}
	
	public void onDragNode(MouseEvent event) {
		if(event.isPrimaryButtonDown()) {
			var source = (GameElementImageView) event.getSource();
			moveNode(event, source);
			event.consume();
		}
	}
	
	public void onDragMap(MouseEvent event) {
		if(event.isSecondaryButtonDown()) {
			moveMap(event);
			event.consume();
		}
	}
	
	public void onReleaseNode(MouseEvent event) {
			var source = (GameElementImageView) event.getSource();
			source.setEffect(nodeDeselected);
			source.setIsSelected(false);
			event.consume();
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
		setTranslateX(0);
		setTranslateY(0);
		var clip = (Rectangle) getClip();
		clip.setX(0);
		clip.setY(0);
	}
}
