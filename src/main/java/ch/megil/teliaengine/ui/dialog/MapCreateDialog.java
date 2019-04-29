package ch.megil.teliaengine.ui.dialog;

import ch.megil.teliaengine.game.Map;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class MapCreateDialog extends Dialog<Map> {
	private static final int PADDING = 15;
	
	private Node createBtn;
	private TextField mapName;
	private TextField width;
	private TextField height;
	
	public MapCreateDialog() {	
		var createType = new ButtonType("Create", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().setAll(createType, ButtonType.CANCEL);
		createBtn = getDialogPane().lookupButton(createType);
		createBtn.setDisable(true);
		
		var grid = new GridPane();
		grid.setPadding(new Insets(PADDING));
		grid.setHgap(PADDING);
		grid.setVgap(PADDING);
		
		grid.add(new Label("Map Name"), 0, 0);
		mapName = new TextField();
		mapName.textProperty().addListener(this::enableCreate);
		Platform.runLater(() -> mapName.requestFocus());
		grid.add(mapName, 1, 0);
		
		grid.add(new Label("Map Width"), 0, 1);
		width = new TextField();
		width.textProperty().addListener(this::enableCreate);
		width.textProperty().addListener(this::checkInt);
		grid.add(width, 1, 1);
		
		grid.add(new Label("Map Height"), 0, 2);
		height = new TextField();
		height.textProperty().addListener(this::enableCreate);
		height.textProperty().addListener(this::checkInt);
		grid.add(height, 1, 2);
		
		getDialogPane().setContent(grid);
		
		setResultConverter(b -> b.equals(createType)
				? new Map(mapName.getText(), Integer.parseInt(width.getText()), Integer.parseInt(height.getText()))
				: null);
	}
	
	private void checkInt(ObservableValue<? extends String> obs, String oldVal, String newVal) {
		if (!newVal.matches("\\d*")) {
			((StringProperty)obs).set(newVal.replaceAll("[^\\d]", ""));
		}
	}

	private <T> void enableCreate(ObservableValue<? extends T> obs, T oldVal, T newVal) {
		createBtn.setDisable(mapName.getText().trim().isEmpty() || width.getText().trim().isEmpty() || height.getText().trim().isEmpty());
	}
}
