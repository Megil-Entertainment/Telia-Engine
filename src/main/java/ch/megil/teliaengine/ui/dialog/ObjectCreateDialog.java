package ch.megil.teliaengine.ui.dialog;
import javafx.event.ActionEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.configuration.ProjectFolderConfiguration;
import ch.megil.teliaengine.file.TextureFileManager;
import ch.megil.teliaengine.file.exception.AssetCreationException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.game.Hitbox;
import ch.megil.teliaengine.game.Vector;
import ch.megil.teliaengine.logging.LogHandler;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;

public class ObjectCreateDialog extends Dialog<GameObject>{
	private static final int PADDING = 15;
	
	private Node createBtn;
	private TextField objectName;
	private TextField objectWidth;
	private TextField objectHeight;
	private TextField texturePath;
	private Image depiction;
	
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
		objectWidth.textProperty().addListener(this::checkInt);
		objectHeight = new TextField();
		objectHeight.textProperty().addListener(this::enableCreate);
		objectHeight.textProperty().addListener(this::checkInt);
		grid.add(objectWidth, 1, 1);
		grid.add(objectHeight, 2, 1);
		
		grid.add(new Label("Texture"), 0, 2);
		texturePath = new TextField();
		texturePath.textProperty().addListener(this::enableCreate);
		grid.add(texturePath, 1, 2);
		var searchBtn = new Button("...");
		searchBtn.setOnAction(this::searchTexture);
		grid.add(searchBtn, 2, 2);
		
		
		getDialogPane().setContent(grid);
		
		
		setResultConverter(b -> b.equals(createType)
				? createGameObject(objectName.getText(), objectName.getText(), 
				  new Hitbox(new Vector(0, 0), Double.parseDouble(objectWidth.getText()), Double.parseDouble(objectHeight.getText())))
				: null);
	}
	
	private GameObject createGameObject(String name, String depictionName, Hitbox hitbox){
		try {
			TextureFileManager.get().importTexture(depictionName, new File(texturePath.getText()));
			depiction = TextureFileManager.get().load(depictionName, hitbox.getVectorSize().getX(), hitbox.getVectorSize().getY());
		} catch (AssetNotFoundException  | AssetCreationException e) {
			LogHandler.log(e, Level.SEVERE);
		}
		GameObject obj = new GameObject(name, depictionName, depiction, hitbox, new Color(0, 0, 0, 0));
		addObjectToAssetsFolder(obj);
		return obj;
	}
	
	private void checkInt(ObservableValue<? extends String> obs, String oldVal, String newVal) {
		if (!newVal.matches("\\d*")) {
			((StringProperty)obs).set(newVal.replaceAll("[^\\d]", ""));
		}
	}
	
	private <T> void enableCreate(ObservableValue<? extends T> obs, T oldVal, T newVal) {
		createBtn.setDisable(objectName.getText().trim().isEmpty() || objectWidth.getText().trim().isEmpty() || objectHeight.getText().trim().isEmpty()
				|| texturePath.getText().trim().isEmpty());
	}
	
	private void searchTexture(ActionEvent ae) {
		var chooser = new FileChooser();
		ArrayList<String> extensions = new ArrayList<>();
		extensions.add("*" + FileConfiguration.FILE_EXT_TEXTURE.getConfiguration());
		extensions.add("*" + ".jpg");
		chooser.getExtensionFilters().add(new ExtensionFilter("Texture", extensions));
		chooser.setInitialDirectory(new File("assets/texture"));
		var dir = chooser.showOpenDialog(texturePath.getScene().getWindow());
		if (dir != null) {
			texturePath.setText(dir.getAbsolutePath());
		}
	}
	
	private void addObjectToAssetsFolder(GameObject obj) {
		var fileName = ProjectFolderConfiguration.ASSETS_OBJECTS.getConfigurationWithProjectPath() + "/" + obj.getName() + FileConfiguration.FILE_EXT_OBJECT.getConfiguration();
		
		var propSeperator = FileConfiguration.SEPERATOR_PROPERTY.getConfiguration();
		
		try (var writer = new BufferedWriter(new FileWriter(fileName))) {
			writer.write(obj.getDepiction().getWidth() + propSeperator + obj.getDepiction().getHeight() + propSeperator +
					obj.getDepictionName() + propSeperator + obj.getColor());
		} catch (IOException e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	private Label createLabelWithTooltip(String text) {
		var label = new Label(text);
		label.setTooltip(new Tooltip(text));
		return label;
	}
}
