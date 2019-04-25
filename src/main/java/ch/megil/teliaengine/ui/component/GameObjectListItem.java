package ch.megil.teliaengine.ui.component;

import java.util.function.Consumer;

import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.ui.GameElementImageView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class GameObjectListItem extends AnchorPane {
	private static final int PADDING = 5;
	private static final int SPACING = 5;
	
	private Rectangle bg;
	private HBox content;
	
	private ObjectProperty<GameObject> gameObject;
	private ObjectProperty<Paint> hoverColor;
	private ObjectProperty<Consumer<GameObject>> onAction;
	
	public GameObjectListItem() {
		gameObject = new SimpleObjectProperty<>();
		hoverColor = new SimpleObjectProperty<>();
		onAction = new SimpleObjectProperty<>(obj -> {});
		
		bg = new Rectangle();
		getChildren().add(bg);
		content = new HBox();
		content.setAlignment(Pos.CENTER_LEFT);
		content.setPadding(new Insets(PADDING));
		content.setSpacing(SPACING);
		getChildren().add(content);
		
		bg.widthProperty().bind(widthProperty());
		bg.heightProperty().bind(heightProperty());
		
		gameObject.addListener(this::gameObjectChangeListener);
		
		setOnMouseClicked(me -> onAction.get().accept(getGameObject()));
		setOnMouseEntered(me -> swapColor());
		setOnMouseExited(me -> swapColor());
	}
	
	private void gameObjectChangeListener(ObservableValue<? extends GameObject> obj, GameObject oldObj, GameObject newObj) {
		content.getChildren().clear();
		content.getChildren().add(new GameElementImageView(newObj));
		content.getChildren().add(new Label(newObj.getName()));
	}
	
	private void swapColor() {
		if(hoverColor.get() != null) {
			Paint old = bg.getFill();
			bg.setFill(getHoverColor());
			setHoverColor(old);
		}
	}
	
	
	public GameObject getGameObject() {
		return gameObject.get();
	}
	
	public void setGameObject(GameObject obj) {
		gameObject.set(obj);
	}
	
	public Paint getHoverColor() {
		return hoverColor.get();
	}
	
	public void setHoverColor(Paint hoverColor) {
		this.hoverColor.set(hoverColor);
	}
	
	public void setBgColor(Paint bgColor) {
		this.bg.setFill(bgColor);
	}
	
	public Consumer<GameObject> getOnAction() {
		return onAction.get();
	}
	
	public void setOnAction(Consumer<GameObject> obj) {
		onAction.set(obj);
	}

}
