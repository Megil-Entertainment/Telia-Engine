package ch.megil.teliaengine.ui.dialog;
import javafx.event.ActionEvent;

import java.io.File;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.game.GameObject;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ObjectCreateDialog extends Dialog<GameObject>{
	private static final int PADDING = 15;
	
	private Node createBtn;
	private TextField objectName;
	private TextField objectWidth;
	private TextField objectHeight;
	private TextField hitboxWidth;
	private TextField hitboxHeight;
	private TextField texture;
	
	public ObjectCreateDialog() {
		var createType = new ButtonType("Create Object", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().setAll(createType, ButtonType.CANCEL);
		createBtn = getDialogPane().lookupButton(createType);
		createBtn.setDisable(true);
		
		var grid = new GridPane();
		grid.setPadding(new Insets(PADDING));
		grid.setHgap(PADDING);
		grid.setVgap(PADDING);
		
		grid.add(new Label("Name"), 0, 0);
		objectName = new TextField();
		objectName.textProperty().addListener(this::enableCreate);
		Platform.runLater(() -> objectName.requestFocus());
		grid.add(objectName, 1, 0);
		
		grid.add(new Label("Object Width"), 0, 1);
		objectWidth = new TextField();
		objectWidth.textProperty().addListener(this::enableCreate);
		objectWidth.textProperty().addListener(this::checkInt);
		grid.add(objectWidth, 1, 1);
		
		grid.add(new Label("Object Height"), 0, 2);
		objectHeight = new TextField();
		objectHeight.textProperty().addListener(this::enableCreate);
		objectHeight.textProperty().addListener(this::checkInt);
		grid.add(objectHeight, 1, 2);
		
		grid.add(new Label("Hitbox Width"), 0, 3);
		hitboxWidth = new TextField();
		hitboxWidth.textProperty().addListener(this::enableCreate);
		hitboxWidth.textProperty().addListener(this::checkInt);
		grid.add(hitboxWidth, 1, 3);
		
		grid.add(new Label("Hitbox Heigth"), 0, 4);
		hitboxHeight = new TextField();
		hitboxHeight.textProperty().addListener(this::enableCreate);
		hitboxHeight.textProperty().addListener(this::checkInt);
		grid.add(hitboxHeight, 1, 4);
		
		grid.add(new Label("Texture"), 0, 5);
		texture = new TextField();
		grid.add(texture, 1, 5);
		var searchBtn = new Button("...");
		searchBtn.setOnAction(this::searchTexture);
		grid.add(searchBtn, 2, 5);
		
		
		getDialogPane().setContent(grid);
	}
	
	private void checkInt(ObservableValue<? extends String> obs, String oldVal, String newVal) {
		if (!newVal.matches("\\d*")) {
			((StringProperty)obs).set(newVal.replaceAll("[^\\d]", ""));
		}
	}
	
	private <T> void enableCreate(ObservableValue<? extends T> obs, T oldVal, T newVal) {
		createBtn.setDisable(objectName.getText().trim().isEmpty() || objectWidth.getText().trim().isEmpty() || objectHeight.getText().trim().isEmpty()
				|| hitboxWidth.getText().trim().isEmpty() || hitboxHeight.getText().trim().isEmpty());
	}
	
	private void searchTexture(ActionEvent ae) {
		var chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("Texture", "*" + FileConfiguration.FILE_EXT_TEXTURE.getConfiguration()));
		chooser.setInitialDirectory(new File("assets/texture"));
		var dir = chooser.showOpenDialog(texture.getScene().getWindow());
		if (dir != null) {
			texture.setText(dir.getAbsolutePath());
		}
	}
}
