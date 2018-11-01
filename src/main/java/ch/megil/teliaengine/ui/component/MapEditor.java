package ch.megil.teliaengine.ui.component;

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
			getChildren().add(new MyImageView(obj));
		}
	}
	
	public void onDragStart(MouseEvent event) {
		var source = (Node) event.getSource();
		dx = source.getLayoutX() - event.getSceneX();
		dy = source.getLayoutY() - event.getSceneY();
	}
	
	private void moveNode(MouseEvent event) {
		var source = (MyImageView) event.getSource();
		source.setImageViewLayoutX(event.getSceneX() + dx);
		source.setImageViewLayoutY(event.getSceneY() + dy);
		checkBoundries(source);
	}
	
	private void checkBoundries(MyImageView imageView) {
		double sourceWidth;
		double sourceHeight;
		sourceWidth = imageView.getGameElement().getHitbox().getVectorSize().getX();
		sourceHeight = imageView.getGameElement().getHitbox().getVectorSize().getY();

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
	
	public void setMap(Map map) {
		getChildren().clear();
		
		this.map = map;
		map.getMapObjects().forEach(o -> getChildren().add(new MyImageView(o)));
		getChildren().add(new MyImageView(Player.getEngine()));
		
	}
}
