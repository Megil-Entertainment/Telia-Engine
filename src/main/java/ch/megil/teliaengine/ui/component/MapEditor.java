package ch.megil.teliaengine.ui.component;

import ch.megil.teliaengine.configuration.SystemConfiguration;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MapEditor extends Pane {
	private Map map;
	private Player player;
	
	private double dx;
	private double dy;
	
	private int gridWidth;
	private int gridHeight;
	
	private InnerShadow nodeSelected;
	private InnerShadow nodeDeselected;
	private static final double INNER_SHADOW_RADIUS = 4.0;
	private static final double INNER_SHADOW_CHOKE = 1.0;
	
	private GameElementImageView selected;
	private VBox dropdownBox;
	
	private TextArea mapTextField;
	
	public MapEditor() {
		mapTextField = new TextArea();
		getChildren().add(mapTextField);
		mapTextField.setEditable(false);
		mapTextField.setMaxSize(50,50);
		mapTextField.setLayoutX(-50);
		mapTextField.setLayoutY(-50);
		mapTextField.setOnKeyReleased(this::onKeyPressed);
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
					});
			}});
		
		gridWidth = Integer.parseInt(SystemConfiguration.MAP_GRID_WIDTH.getConfiguration());
		gridHeight = Integer.parseInt(SystemConfiguration.MAP_GRID_HEIGHT.getConfiguration());
		
		nodeSelected = new InnerShadow(INNER_SHADOW_RADIUS, Color.CORNFLOWERBLUE);
		nodeSelected.setChoke(INNER_SHADOW_CHOKE);
		
		nodeDeselected = new InnerShadow();
		nodeDeselected.setColor(Color.TRANSPARENT);
		dropdownBox = new DropdownList();
	}
	
	public void addGameObject(GameObject obj) {
		if (map != null) {
			map.addObject(obj);
			getChildren().add(new GameElementImageView(obj));
		}
	}
	
	public void onDragStart(MouseEvent event) {
		mapTextField.requestFocus();
		if (event.isPrimaryButtonDown()) {
			var source = (GameElementImageView) event.getSource();
			if(selected != null) {
				selected.setEffect(nodeDeselected);
				selected = null;
			}
			selected = source;
			selected.setEffect(nodeSelected);
			dx = source.getLayoutX() - event.getSceneX();
			dy = source.getLayoutY() - event.getSceneY();
			event.consume();
		}
		
		if(event.isSecondaryButtonDown() && selected != null) {
			showDropdown(event);
			event.consume();
		}
	}
	
	public void onMapDragStart(MouseEvent event) {
		if(event.isPrimaryButtonDown() && selected != null) {
			selected.setEffect(nodeDeselected);
			selected = null;
			getChildren().remove(dropdownBox);
		}
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
	
	private void removeNode(KeyEvent event) {
		map.removeObject((GameObject)selected.getGameElement());
		getChildren().remove(selected);
		selected = null;
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
	
	private void showDropdown(MouseEvent event) {
		dropdownBox.setLayoutX(event.getSceneX());
		dropdownBox.setLayoutY(event.getSceneY());
		getChildren().add(dropdownBox);
	}
	
	private void removeDropdown() {
		getChildren().remove(dropdownBox);
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
	
	public void onKeyPressed(KeyEvent event) {
		KeyCode code = event.getCode();
		if(code.equals(KeyCode.D)) {
			System.out.println("Remove node");
			if(selected.getGameElement() instanceof Player) {
			 System.out.println("Player can not be deleted");
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
	
	public void setMap(Map map) {
		getChildren().clear();
		getChildren().add(mapTextField);
		
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
