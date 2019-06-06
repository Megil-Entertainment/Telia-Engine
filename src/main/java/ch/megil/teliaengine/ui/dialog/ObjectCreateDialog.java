package ch.megil.teliaengine.ui.dialog;
import javafx.event.ActionEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.file.GameObjectFileManager;
import ch.megil.teliaengine.file.TextureFileManager;
import ch.megil.teliaengine.file.exception.AssetCreationException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.physics.Vector;
import ch.megil.teliaengine.physics.collision.RectangleCollider;
import ch.megil.teliaengine.logging.LogHandler;
import ch.megil.teliaengine.ui.GameElementImageView;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;

public class ObjectCreateDialog extends Dialog<GameObject>{
	private static final int PADDING = 15;
	
	private Node createBtn;
	private TextField objectName;
	private TextField objectWidth;
	private TextField objectHeight;
	private TextField texturePath;
	private HBox objectPreview;

	
	public ObjectCreateDialog() {
		var createType = new ButtonType("Create Object", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().setAll(createType, ButtonType.CANCEL);
		createBtn = getDialogPane().lookupButton(createType);
		createBtn.setDisable(true);
		
		var grid = new GridPane();
		grid.setPadding(new Insets(PADDING));
		grid.setHgap(PADDING);
		grid.setVgap(PADDING);
		
		grid.add(createLabelWithTooltip("Object Name"), 0, 0);
		objectName = new TextField();
		objectName.textProperty().addListener(this::enableCreate);
		Platform.runLater(() -> objectName.requestFocus());
		grid.add(objectName, 1, 0, 2, 1);
		
		grid.add(createLabelWithTooltip("Object Width / Object Height"), 0, 1);
		objectWidth = new TextField();
		objectWidth.textProperty().addListener(this::enableCreate);
		objectHeight = new TextField();
		objectHeight.textProperty().addListener(this::enableCreate);
		grid.add(objectWidth, 1, 1);
		grid.add(objectHeight, 2, 1);
		
		grid.add(createLabelWithTooltip("Texture"), 0, 2);
		texturePath = new TextField();
		texturePath.textProperty().addListener(this::enableCreate);
		grid.add(texturePath, 1, 2);
		var searchBtn = new Button("...");
		searchBtn.setOnAction(this::searchTexture);
		grid.add(searchBtn, 2, 2);
		
		objectPreview = new HBox();
		objectPreview.setAlignment(Pos.CENTER);
		objectPreview.setPadding(new Insets(PADDING));
		grid.add(objectPreview, 2, 3);
		
		
		getDialogPane().setContent(grid);
		
		
		setResultConverter(b -> b.equals(createType)
				? createGameObject() : null);
	}
	
	private GameObject createGameObject() {
		var name = objectName.getText();
		var width = Double.parseDouble(objectWidth.getText());
		var height = Double.parseDouble(objectWidth.getText());
		var hitbox = new RectangleCollider(new Vector(0,0), width, height);
		var textureFile = new File(texturePath.getText());
		var textureName = name;
		
		try {
			TextureFileManager.get().importTexture(textureName, textureFile);
			var depiction = TextureFileManager.get().load(textureName, width, height);
			var obj = new GameObject(name, name, depiction, hitbox, Color.BLACK);
			new GameObjectFileManager().create(obj);
			return obj;
		} catch (AssetNotFoundException | AssetCreationException e) {
			LogHandler.log(e, Level.SEVERE);
			showErrorAlert("Creation Error", "The game object could not be created");
		}
		
		return null;
	}
	
	private <T> void enableCreate(ObservableValue<? extends T> obs, T oldVal, T newVal) {
		try{
			var width = Double.parseDouble(objectWidth.getText());
			var height = Double.parseDouble(objectHeight.getText());
			
			if(!objectName.getText().trim().isEmpty() && !texturePath.getText().trim().isEmpty()) {
				var obj = new GameObject(null, null, TextureFileManager.get().loadExternal(texturePath.getText(), width, height), null, null);
				var objImageView = new GameElementImageView(obj);
				objectPreview.getChildren().clear();
				objectPreview.setMinSize(width, height);
				objectPreview.getChildren().add(objImageView);
				getDialogPane().getScene().getWindow().sizeToScene();
				createBtn.setDisable(false);
				return;
			}
		} catch(NumberFormatException e) {	
		} catch (AssetNotFoundException e) {
			LogHandler.log(e, Level.WARNING);
		}
		createBtn.setDisable(true);
	}
	
	private void searchTexture(ActionEvent ae) {
		var chooser = new FileChooser();
		ArrayList<String> extensions = new ArrayList<>();
		extensions.add("*" + FileConfiguration.FILE_EXT_TEXTURE.getConfiguration());
		extensions.add("*" + ".jpg");
		chooser.getExtensionFilters().add(new ExtensionFilter("Texture", extensions));
		var dir = chooser.showOpenDialog(texturePath.getScene().getWindow());
		if (dir != null) {
			texturePath.setText(dir.getAbsolutePath());
		}
	}
	
	private Label createLabelWithTooltip(String text) {
		var label = new Label(text);
		label.setTooltip(new Tooltip(text));
		return label;
	}
	
	private void showErrorAlert(String title, String message) {
		var alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
