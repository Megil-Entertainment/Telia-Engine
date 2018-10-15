package ch.megil.teliaengine.ui;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.file.MapSaveLoad;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.logging.LogHandler;
import ch.megil.teliaengine.ui.component.MapEditor;
import ch.megil.teliaengine.ui.component.ObjectExplorer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

public class EngineUIController {
	@FXML
	private MapEditor mapEditor;
	
	private MapSaveLoad mapSaveLoad;

	@FXML
	private void initialize() {
		mapSaveLoad = new MapSaveLoad();
	}
	
	@FXML
	private void fileSaveMap() {
		var dialog = new TextInputDialog();
		dialog.setTitle("Map Name");
		dialog.setHeaderText("Select a map name to save.");
		
		var result = dialog.showAndWait();
		result.ifPresent(n -> mapSaveLoad.save(mapEditor.getMap(), n));
	}
	
	@FXML
	private void fileLoadMap() {
		var mapDir = new File(GameConfiguration.ASSETS_MAPS.getConfiguration());
		var mapNames = Arrays.stream(mapDir.listFiles())
				.map(m -> m.getName().replace(GameConfiguration.FILE_EXT_MAP.getConfiguration(), "")).sorted()
				.collect(Collectors.toList());

		if (mapNames.size() == 0) {
			var alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("No maps found.");
			alert.setHeaderText(null);
			alert.setContentText("There were no saved maps found.");
			alert.showAndWait();
		} else {
			var dialog = new ChoiceDialog<>(mapNames.get(0), mapNames);
			dialog.setTitle("Map Load");
			dialog.setHeaderText("Select a map to load.");

			var result = dialog.showAndWait();

			if (result.isPresent()) {
				try {
					mapEditor.setMap(mapSaveLoad.load(result.get(), false));
				} catch (AssetNotFoundException | AssetFormatException e) {
					var alert = new Alert(AlertType.WARNING);
					alert.setTitle("Map Load Error");
					alert.setHeaderText(null);
					alert.setContentText("There was an error while loading the map. Do you wanna try and recover the map?");
					alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
					var res = alert.showAndWait();
					if (res.isPresent() && res.get().equals(ButtonType.YES)) {
						try {
							mapEditor.setMap(mapSaveLoad.load(result.get(), true));
						} catch (AssetNotFoundException | AssetFormatException e2) {
							LogHandler.log(e2, Level.SEVERE);
						}
					} else {
						LogHandler.log(e, Level.SEVERE);
					}
				}
			}
		}
	}
	
	@FXML
	private void gameRun() {
		// TODO: implement run
	}

	@FXML
	private void helpAbout() {
		new AboutDialog().showAndWait();
	}
}
