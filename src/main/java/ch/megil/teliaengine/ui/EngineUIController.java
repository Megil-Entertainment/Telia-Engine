package ch.megil.teliaengine.ui;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.file.MapSaveLoad;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.logging.LogHandler;
import ch.megil.teliaengine.ui.component.MapEditor;
import javafx.fxml.FXML;
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
				.map(m -> m.getName().replace(GameConfiguration.FILE_EXT_MAP.getConfiguration(), ""))
				.sorted()
				.collect(Collectors.toList());
		
		if (mapNames.size() == 0) {
			// TODO: inform of no existing maps
		} else {
			var dialog = new ChoiceDialog<>(mapNames.get(0), mapNames);
			dialog.setTitle("Map Load");
			dialog.setHeaderText("Select a map to load.");
			
			var result = dialog.showAndWait();
			
			if (result.isPresent()) {
				try {
					mapEditor.setMap(mapSaveLoad.load(result.get(), false));
				} catch (AssetNotFoundException | AssetFormatException e) {
					LogHandler.log(e, Level.SEVERE);
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
