package ch.megil.teliaengine.ui;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import ch.megil.teliaengine.GameMain;
import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.file.MapSaveLoad;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.logging.LogHandler;
import ch.megil.teliaengine.ui.component.AssetExplorer;
import ch.megil.teliaengine.ui.component.MapEditor;
import ch.megil.teliaengine.ui.component.ObjectExplorer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class EngineUIController {	
	@FXML
	private ObjectExplorer objectExplorer;
	@FXML
	private AssetExplorer assetExplorer;
	@FXML
	private TabPane tabPane;
	
	private MapSaveLoad mapSaveLoad;
	
	private MapEditor currentMapEditor;
	
	private Map<String, Tab> openTabs = new HashMap<>();
	@FXML
	private void initialize() {
		mapSaveLoad = new MapSaveLoad();
		objectExplorer.setMaxWidth(300);
		try {
			assetExplorer.initialize(GameConfiguration.ASSETS.getConfiguration(), this::loadMap);
			assetExplorer.setMaxWidth(300);
		} catch (AssetNotFoundException e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	@FXML
	private void fileNewMap() {
		Tab mapEditorTab = new Tab();
		MapEditor mapEditor = new MapEditor();
		mapEditorTab.setContent(mapEditor);
		tabPane.getTabs().add(mapEditorTab);
		mapEditorTab.setOnSelectionChanged(event -> updateTab(mapEditor));
		mapEditorTab.setText(mapEditor.getMap().getName());
		objectExplorer.setMapEditor(mapEditor);
		tabPane.getSelectionModel().select(mapEditorTab);
		openTabs.put(mapEditor.getMap().getName(), mapEditorTab);
		mapEditorTab.setOnClosed(event -> openTabs.remove(mapEditor.getMap().getName()));
		new MapCreateDialog().showAndWait().ifPresent(mapEditor::setMap);
		currentMapEditor = mapEditor;
	}
	
	@FXML
	private void fileSaveMap() {
		var map = currentMapEditor.getMap();
		
		if (map.getName() == null) {
			var dialog = new TextInputDialog();
			dialog.setTitle("Map Name");
			dialog.setHeaderText("Select a map name to save.");
			
			var result = dialog.showAndWait();
			result.ifPresent(map::setName);
		}
		
		mapSaveLoad.save(map, currentMapEditor.getPlayer());
		try {
			assetExplorer.initialize(GameConfiguration.ASSETS.getConfiguration(), this::loadMap);
		} catch(AssetNotFoundException e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	@FXML
	private void fileLoadMap() {
		var mapDir = new File(GameConfiguration.ASSETS_MAPS.getConfiguration());
		var mapNames = Arrays.stream(mapDir.listFiles())
				.map(m -> m.getName().replace(FileConfiguration.FILE_EXT_MAP.getConfiguration(), "")).sorted()
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
				loadMap(result.get());
			}
		}
	}
	
	private void loadMap(String mapName) {
		try {
			if(openTabs.containsKey(mapName)) {
				tabPane.getSelectionModel().select(openTabs.get(mapName));
			}else {
				openNewTab(mapName, false);
			}
		} catch (AssetNotFoundException | AssetFormatException e) {
			var alert = new Alert(AlertType.WARNING);
			alert.setTitle("Map Load Error");
			alert.setHeaderText(null);
			alert.setContentText("There was an error while loading the map. Do you wanna try and recover the map?");
			alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
			var res = alert.showAndWait();
			if (res.isPresent() && res.get().equals(ButtonType.YES)) {
				try {
					openNewTab(mapName, true);
				} catch (AssetNotFoundException | AssetFormatException e2) {
					LogHandler.log(e2, Level.SEVERE);
				}
			} else {
				LogHandler.log(e, Level.SEVERE);
			}
		}
	}
	
	@FXML
	private void gameRun() {
		if(currentMapEditor.getMap() == null) {
			var alert = new Alert(AlertType.ERROR);
			alert.setTitle("Run Error");
			alert.setHeaderText(null);
			alert.setContentText("Game can not be runned without an active map");
			alert.showAndWait();
		}else {
			fileSaveMap();
			var stage = new Stage();
			try {
				var main = new GameMain(currentMapEditor.getMap().getName());
				main.run();
			} catch (Exception e) {
				stage.hide();
				LogHandler.log(e, Level.SEVERE);
			
				var alert = new Alert(AlertType.ERROR);
				alert.setTitle("Load Error");
				alert.setHeaderText(null);
				alert.setContentText("There was an error while loading the game.");
				alert.showAndWait();
			}
		}
	}

	@FXML
	private void helpAbout() {
		new AboutDialog().showAndWait();
	}
	
	private void updateTab(MapEditor mapEditor) {
		objectExplorer.setMapEditor(mapEditor);
		currentMapEditor = mapEditor;
	}
	
	private void openNewTab(String mapName, boolean safeMode) throws AssetNotFoundException, AssetFormatException {
		Tab mapEditorTab = new Tab();
		MapEditor mapEditor = new MapEditor();
		mapEditorTab.setContent(mapEditor);
		tabPane.getTabs().add(mapEditorTab);
		currentMapEditor = mapEditor;
		currentMapEditor.setMap(mapSaveLoad.load(mapName, safeMode));
		mapEditorTab.setOnSelectionChanged(event -> updateTab(mapEditor));
		mapEditorTab.setText(mapEditor.getMap().getName());
		objectExplorer.setMapEditor(currentMapEditor);
		tabPane.getSelectionModel().select(mapEditorTab);
		openTabs.put(mapEditor.getMap().getName(), mapEditorTab);
		mapEditorTab.setOnClosed(event -> openTabs.remove(mapEditor.getMap().getName()));
	}
}
