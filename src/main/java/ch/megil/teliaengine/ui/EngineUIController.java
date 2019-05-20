package ch.megil.teliaengine.ui;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

import ch.megil.teliaengine.GameMain;
import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.configuration.ProjectFolderConfiguration;
import ch.megil.teliaengine.file.MapFileManager;
import ch.megil.teliaengine.file.PlayerFileManager;
import ch.megil.teliaengine.file.ProjectFileManager;
import ch.megil.teliaengine.file.exception.AssetCreationException;
import ch.megil.teliaengine.file.exception.AssetLoadException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.logging.LogHandler;
import ch.megil.teliaengine.project.ProjectController;
import ch.megil.teliaengine.ui.component.AssetExplorer;
import ch.megil.teliaengine.ui.component.MapEditor;
import ch.megil.teliaengine.ui.component.ObjectExplorer;
import ch.megil.teliaengine.ui.dialog.AboutDialog;
import ch.megil.teliaengine.ui.dialog.MapCreateDialog;
import ch.megil.teliaengine.ui.dialog.ProjectCreateDialog;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class EngineUIController {	
	@FXML
	private ObjectExplorer objectExplorer;
	@FXML
	private AssetExplorer assetExplorer;
	@FXML
	private TabPane tabPane;
		
	private MapEditor currentMapEditor;
	
	private java.util.Map<String, Tab> openTabs;
	
	private MapFileManager mapFileManger;
	private ProjectFileManager projectFileManager;

	@FXML
	private void initialize() {
		mapFileManger = new MapFileManager();

		projectFileManager = new ProjectFileManager();

		objectExplorer.setMaxWidth(300);
		try {
			assetExplorer.initialize(this::loadMap);
			assetExplorer.changeRoots(ProjectFolderConfiguration.ASSETS_MAPS.getConfigurationWithProjectPath());
			assetExplorer.setMaxWidth(300);
		} catch (AssetNotFoundException e) {
			LogHandler.log(e, Level.SEVERE);
		}
		
		openTabs = new HashMap<>();
	}
	
	@FXML
	private void fileNewProject() {
		new ProjectCreateDialog(projectFileManager).showAndWait().ifPresent(this::openProject);
	}
	
	@FXML
	private void fileOpenProject() throws IOException {
		var chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("Project", "*" + FileConfiguration.FILE_EXT_PROJECT.getConfiguration()));
		var projectInfo = chooser.showOpenDialog(tabPane.getScene().getWindow());
		if (projectInfo != null) {
			openProject(projectInfo);
		}
	}
	
	private void openProject(File projectInfo) {
		try {
			var project = projectFileManager.loadProject(projectInfo);
			projectFileManager.updateLastOpenedProject(projectInfo);
			ProjectController.get().openProject(project);
			assetExplorer.changeRoots(ProjectFolderConfiguration.ASSETS_MAPS.getConfigurationWithProjectPath());
			objectExplorer.reload();
			tabPane.getTabs().clear();
			openTabs.clear();
		} catch (AssetLoadException e) {
			LogHandler.log(e, Level.SEVERE);
			showErrorAlert("Load Error", "The specified project could not been loaded.");
		} catch (AssetCreationException e) {
			LogHandler.log(e, Level.WARNING);
		}
	}
	
	@FXML
	private void fileNewMap() {
		new MapCreateDialog().showAndWait().ifPresent(this::initMap);
	}
	
	private void initMap(Map map) {
		try {
			var player = new PlayerFileManager().load();
			var newMapEditor = new MapEditor();
			newMapEditor.updateMap(map, player);
			mapFileManger.save(map, newMapEditor.getPlayer());
			assetExplorer.reload();
			
			var mapEditorTab = new Tab();
			currentMapEditor = newMapEditor;
			addFunctionalityToTab(mapEditorTab, currentMapEditor);
		} catch(AssetLoadException e) {
			LogHandler.log(e, Level.SEVERE);
		}
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
			currentMapEditor.setSaved(true);
		}
		
		mapFileManger.save(map, currentMapEditor.getPlayer());
	}
	
	@FXML
	private void fileLoadMap() {
		var mapDir = new File(ProjectFolderConfiguration.ASSETS_MAPS.getConfigurationWithProjectPath());
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

		} catch (AssetLoadException e) {
			var alert = new Alert(AlertType.WARNING);
			alert.setTitle("Map Load Error");
			alert.setHeaderText(null);
			alert.setContentText("There was an error while loading the map. Do you wanna try and recover the map?");
			alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
			var res = alert.showAndWait();
			if (res.isPresent() && res.get().equals(ButtonType.YES)) {
				try {
					openNewTab(mapName, true);
				} catch (AssetLoadException e2) {
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
				showErrorAlert("Load Error", "There was an error while loading the game.");
			}
		}
	}

	@FXML
	private void helpAbout() {
		new AboutDialog().showAndWait();
	}
	
	private void onChangeTab(MapEditor mapEditor) {
		objectExplorer.setMapEditor(mapEditor);
		currentMapEditor = mapEditor;
	}
	
	private void openNewTab(String mapName, boolean saveMode) throws AssetLoadException {
		var mapEditorTab = new Tab();
		var mapEditor = new MapEditor();
		var player = new PlayerFileManager().load();
		mapEditor.updateMap(mapFileManger.load(mapName, saveMode, player), player);
		addFunctionalityToTab(mapEditorTab, mapEditor);
	}
	
	private boolean checkForMapChanges(MapEditor mapEditor) {
		return mapEditor.getSaved();
	}
	
	private void openSaveDialog(Tab mapEditorTab, Event event) {
		var alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Tab Close");
		alert.setHeaderText("There are unsaved changes");
		alert.setContentText("Do you want to save them?");
		
		var saveButton = new ButtonType("Save");
		var ignoreButton = new ButtonType("Ignore");
		var cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		
		alert.getButtonTypes().setAll(saveButton, ignoreButton, cancelButton);
		var result = alert.showAndWait().get();
		if(result == saveButton) {
			fileSaveMap();
		}else if(result == cancelButton) {
			event.consume();
		}
	}
	
	private void addFunctionalityToTab(Tab tab, MapEditor mapEditor) {
		tab.setContent(mapEditor);
		tabPane.getTabs().add(tab);
		currentMapEditor = mapEditor;
		tab.setOnSelectionChanged(event -> onChangeTab(mapEditor));
		tab.setText(mapEditor.getMap().getName());
		objectExplorer.setMapEditor(currentMapEditor);
		tabPane.getSelectionModel().select(tab);
		openTabs.put(mapEditor.getMap().getName(), tab);
		tab.setOnCloseRequest(event -> {
			var changes = checkForMapChanges(mapEditor);
			if(!changes) {
				openSaveDialog(tab, event);
			}
		});
		tab.setOnClosed(event -> openTabs.remove(mapEditor.getMap().getName()));
	}
		
	private void showErrorAlert(String title, String message) {
		var alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
