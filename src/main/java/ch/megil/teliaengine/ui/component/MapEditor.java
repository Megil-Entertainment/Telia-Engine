package ch.megil.teliaengine.ui.component;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.ui.GameElementImageView;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MapEditor extends Pane {
	private static final double INNER_SHADOW_RADIUS = 4.0;
	private static final double INNER_SHADOW_CHOKE = 1.0;
	private static final double KEY_INPUT_HIDDEN_SIZE = 50;
	private static final double KEY_INPUT_HIDDEN_OFFSET = -50;
	private static final Color INNER_SHADOW_COLOR = Color.CORNFLOWERBLUE;
	private static final Color MAP_BACKGROUND = Color.WHITE;
	private static final Color MAP_BACKGROUND_STROKE = Color.BLACK;
	
	private Map map;
	private Player player;
	
	private double dx;
	private double dy;
	
	private double gridWidth;
	private double gridHeight;
	
	private InnerShadow nodeSelected;
	private InnerShadow nodeDeselected;
	
	private GameElementImageView selected;
	
	private TextArea hiddenKeyInput;
	private Rectangle mapBackground;
	
	private boolean saved;
	
	public MapEditor() {
		hiddenKeyInput = new TextArea();
		getChildren().add(hiddenKeyInput);
		hiddenKeyInput.setEditable(false);
		hiddenKeyInput.setMaxSize(KEY_INPUT_HIDDEN_SIZE,KEY_INPUT_HIDDEN_SIZE);
		hiddenKeyInput.setLayoutX(KEY_INPUT_HIDDEN_OFFSET);
		hiddenKeyInput.setLayoutY(KEY_INPUT_HIDDEN_OFFSET);
		hiddenKeyInput.setOnKeyReleased(this::onKeyPressed);
		mapBackground = new Rectangle();
		mapBackground.setFill(MAP_BACKGROUND);
		mapBackground.setStroke(MAP_BACKGROUND_STROKE);
		var clip = new Rectangle();
		clip.widthProperty().bind(this.widthProperty());
		clip.heightProperty().bind(this.heightProperty());
		setClip(clip);
		setOnMousePressed(this::onClickMap);
		setOnMouseDragged(this::onMoveMap);
		getChildren().addListener((ListChangeListener<Node>) c -> {
			while(c.next()) {
				c.getAddedSubList().forEach(n -> {
					n.setOnMousePressed(this::onClickNode);
					n.setOnMouseDragged(this::onMoveNode);
					});
			}});
		
		gridWidth = GameConfiguration.MAP_GRID_WIDTH.getConfiguration();
		gridHeight = GameConfiguration.MAP_GRID_HEIGHT.getConfiguration();
		
		nodeSelected = new InnerShadow(INNER_SHADOW_RADIUS, INNER_SHADOW_COLOR);
		nodeSelected.setChoke(INNER_SHADOW_CHOKE);
		
		nodeDeselected = new InnerShadow();
		nodeDeselected.setColor(Color.TRANSPARENT);
		
		saved = true;
	}
	
	public void addGameObject(GameObject obj) {
		if (map != null) {
			map.addObject(obj);
			getChildren().add(new GameElementImageView(obj));
			saved = false;
		}
	}
	
	public void onClickNode(MouseEvent event) {
		hiddenKeyInput.requestFocus();
		if (event.isPrimaryButtonDown() && event.getSource() != mapBackground) {
			var source = (GameElementImageView) event.getSource();
			if(selected != null) {
				selected.setEffect(nodeDeselected);
			}
			selected = source;
			selected.setEffect(nodeSelected);
			dx = source.getLayoutX() - event.getSceneX();
			dy = source.getLayoutY() - event.getSceneY();
			event.consume();
		}
	}
	
	public void onClickMap(MouseEvent event) {
		if(event.isPrimaryButtonDown() && selected != null) {
			selected.setEffect(nodeDeselected);
			selected = null;
		}
		if (event.isSecondaryButtonDown()) {
			dx = getTranslateX() - event.getSceneX();
			dy = getTranslateY() - event.getSceneY();
		}
	}
	
	private void removeNode(KeyEvent event) {
		map.removeObject((GameObject)selected.getGameElement());
		getChildren().remove(selected);
		selected = null;
		saved = false;
	}
	
	private double roundToNearest(double value, double roundFactor) {
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
		
		if (offsetX >= 0 || getTranslateX() > 0) {
			setTranslateX(0);
		} else if(getTranslateX() < offsetX) {
			setTranslateX(offsetX);
		}
		
		if(offsetY >= 0 || getTranslateY() > 0) {
			setTranslateY(0);
		} else if(getTranslateY() < offsetY) {
			setTranslateY(offsetY);
		}
	}
	
	public void onMoveNode(MouseEvent event) {
		if(event.isPrimaryButtonDown() && event.getSource() != mapBackground) {
			var source = (GameElementImageView) event.getSource();
			source.setImageViewLayoutX(
					roundToNearest((event.getSceneX() + dx), gridWidth));
			source.setImageViewLayoutY(
					roundToNearest((event.getSceneY() + dy), gridHeight));
			checkBoundries(source);
			saved = false;
			event.consume();
		}
	}
	
	public void onMoveMap(MouseEvent event) {
		if(event.isSecondaryButtonDown()) {
			var clip = (Rectangle) getClip();
			setTranslateX(event.getSceneX() + dx);
			setTranslateY(event.getSceneY() + dy);
			checkMapBoundries(clip);
			clip.setX(0 - getTranslateX());
			clip.setY(0 - getTranslateY());
			event.consume();
		}
	}
	
	public void onKeyPressed(KeyEvent event) {
		KeyCode code = event.getCode();
		if(code.equals(KeyCode.D)) {
			if(selected.getGameElement() instanceof Player) {
			 event.consume();
			}
			else {
			 removeNode(event);
			 event.consume();
			}
		}
	}
	
	public Map getMap() {
		return map;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void updateMap(Map map, Player player) {
		getChildren().clear();
		getChildren().add(hiddenKeyInput);
		
		this.map = map;
		this.player = player;
		
		mapBackground.setWidth(map.getWidth());
		mapBackground.setHeight(map.getHeight());
		getChildren().add(mapBackground);
		
		map.getMapObjects().forEach(o -> getChildren().add(new GameElementImageView(o)));
		getChildren().add(new GameElementImageView(player));
		setTranslateX(0);
		setTranslateY(0);
		var clip = (Rectangle) getClip();
		clip.setX(0);
		clip.setY(0);
	}
	
	public void setSaved(boolean saved) {
		this.saved = saved;
	}
	
	public boolean getSaved() {
		return saved;
	}

}
