package ch.megil.teliaengine.ui.dialog;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.file.GameObjectFileManager;
import ch.megil.teliaengine.file.TextureFileManager;
import ch.megil.teliaengine.file.exception.AssetCreationException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.logging.LogHandler;
import ch.megil.teliaengine.ui.component.ColliderEditor;
import ch.megil.teliaengine.ui.component.ColliderType;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ObjectCreateDialog extends Dialog<GameObject>{
	private static final int PADDING = 15;
	private static final double WIDTH = 480;
	private static final double HEIGHT = 300;
	
	private static final double PERCENT_WIDTH_COL0 = 20;
	private static final double FACTOR_WIDTH_COL0 = 0.2;
	private static final double PERCENT_WIDTH_COL1 = 15;
	private static final double FACTOR_WIDTH_COL1 = 0.15;
	private static final double PERCENT_WIDTH_COL2 = 15;
	private static final double FACTOR_WIDTH_COL2 = 0.15;
	private static final double PERCENT_WIDTH_COL3 = 50;
	private static final double FACTOR_WIDTH_COL3 = 0.5;
	
	private Node createBtn;
	private TextField objectName;
	private TextField objectWidth;
	private TextField objectHeight;
	private TextField texturePath;
	private ColliderEditor colliderEditor;

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
		
		grid.add(createLabelWithTooltip("Object Width / Height"), 0, 1);
		objectWidth = new TextField();
		objectWidth.textProperty().addListener(this::enableCreate);
		grid.add(objectWidth, 1, 1);
		objectHeight = new TextField();
		objectHeight.textProperty().addListener(this::enableCreate);
		grid.add(objectHeight, 2, 1);
		
		grid.add(createLabelWithTooltip("Texture"), 0, 2);
		texturePath = new TextField();
		texturePath.textProperty().addListener(this::enableCreate);
		var searchBtn = new Button("...");
		searchBtn.setOnAction(this::searchTexture);
		var textureInput = new HBox(texturePath, searchBtn);
		textureInput.setSpacing(PADDING);
		grid.add(textureInput, 1, 2, 2, 1);
		
		grid.add(createLabelWithTooltip("Collider Type"), 0, 3);
		var colliderSelect = new ComboBox<ColliderType>();
		colliderSelect.getItems().addAll(ColliderType.values());
		colliderSelect.setOnAction(e -> colliderEditor.setColliderType(colliderSelect.getValue()));
		colliderSelect.setValue(ColliderType.NONE);
		var colliderColorPicker = new ColorPicker(Color.BLACK);
		colliderColorPicker.setStyle("-fx-color-label-visible: false;");
		colliderColorPicker.getStyleClass().add("button");
		colliderColorPicker.setOnAction(ae -> colliderEditor.setColliderColor(colliderColorPicker.getValue()));
		var colliderInput = new HBox(colliderSelect, colliderColorPicker);
		colliderInput.setSpacing(PADDING);
		grid.add(colliderInput, 1, 3, 2, 1);

		colliderEditor = new ColliderEditor(Color.BLACK);
		grid.add(colliderEditor, 3, 0, 1, 4);

		var col0 = new ColumnConstraints(WIDTH*FACTOR_WIDTH_COL0);
		col0.setPercentWidth(PERCENT_WIDTH_COL0);
		var col1 = new ColumnConstraints(WIDTH*FACTOR_WIDTH_COL1);
		col1.setPercentWidth(PERCENT_WIDTH_COL1);
		var col2 = new ColumnConstraints(WIDTH*FACTOR_WIDTH_COL2);
		col2.setPercentWidth(PERCENT_WIDTH_COL2);
		var col3 = new ColumnConstraints(WIDTH*FACTOR_WIDTH_COL3);
		col3.setPercentWidth(PERCENT_WIDTH_COL3);
		grid.getColumnConstraints().addAll(col0, col1, col2, col3);
		
		getDialogPane().setContent(grid);
		
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setResizable(true);

		setResultConverter(b -> b.equals(createType)
				? createGameObject() : null);
	}
	
	private GameObject createGameObject() {
		var name = objectName.getText();
		var width = Double.parseDouble(objectWidth.getText());
		var height = Double.parseDouble(objectWidth.getText());
		var hitbox = colliderEditor.getCollider();
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
			
			if(!texturePath.getText().trim().isEmpty()) {
				colliderEditor.setObjectImage(TextureFileManager.get().loadExternal(texturePath.getText(), width, height));
				if (!objectName.getText().trim().isEmpty()) {
					createBtn.setDisable(false);
					return;
				}
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
