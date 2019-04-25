package ch.megil.teliaengine.ui;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;

import ch.megil.teliaengine.GameMain;
import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.configuration.ProjectFolderConfiguration;
import ch.megil.teliaengine.file.MapFileManager;
import ch.megil.teliaengine.file.ProjecFileManager;
import ch.megil.teliaengine.file.exception.AssetCreationException;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetLoadException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.logging.LogHandler;
import ch.megil.teliaengine.project.Project;
import ch.megil.teliaengine.project.ProjectController;
import ch.megil.teliaengine.ui.component.AssetExplorer;
import ch.megil.teliaengine.ui.component.MapEditor;
import ch.megil.teliaengine.ui.component.ObjectExplorer;
import ch.megil.teliaengine.ui.dialog.AboutDialog;
import ch.megil.teliaengine.ui.dialog.MapCreateDialog;
import ch.megil.teliaengine.ui.dialog.ProjectCreateDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class EngineUIController {	
	@FXML
	private MapEditor mapEditor;
	@FXML
	private ObjectExplorer objectExplorer;
	@FXML
	private AssetExplorer assetExplorer;
	
	private MapFileManager mapSaveLoad;
	private ProjecFileManager projecCreateLoad;

	@FXML
	private void initialize() {
		mapSaveLoad = new MapFileManager();
		projecCreateLoad = new ProjecFileManager();
		
		objectExplorer.setMapEditor(mapEditor);
		objectExplorer.setMaxWidth(300);
		try {
			assetExplorer.initialize(ProjectFolderConfiguration.ASSETS.getConfiguration(), this::loadMap);
			assetExplorer.setMaxWidth(300);
		} catch (AssetNotFoundException e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	@FXML
	private void fileNewProject() {
		new ProjectCreateDialog().showAndWait().ifPresent(this::initNewProject);
	}
	
	private void initNewProject(Project project) {
		try {
			projecCreateLoad.initProject(project);
			openProject(project);
		} catch (AssetCreationException e) {
			LogHandler.log(e, Level.SEVERE);
			showErrorAlert("Create Error", "There was an error while creating a new project.");
		}
	}
	
	@FXML
	private void fileOpenProject() throws IOException {
		var chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("Project", "*" + FileConfiguration.FILE_EXT_PROJECT.getConfiguration()));
		var projectInfo = chooser.showOpenDialog(mapEditor.getScene().getWindow());
		if (projectInfo != null) {
			try {
				var project = projecCreateLoad.loadProject(projectInfo);
				openProject(project);
			} catch (AssetLoadException e) {
				LogHandler.log(e, Level.SEVERE);
				showErrorAlert("Load Error", "The specified project could not been loaded.");
			}
		}
	}
	
	private void openProject(Project project) {
		ProjectController.get().openProject(project);
		//TODO: reload editors
	}
	
	@FXML
	private void fileNewMap() {
		new MapCreateDialog().showAndWait().ifPresent(mapEditor::setMap);
	}
	
	@FXML
	private void fileSaveMap() {
		var map = mapEditor.getMap();
		
		if (map.getName() == null) {
			var dialog = new TextInputDialog();
			dialog.setTitle("Map Name");
			dialog.setHeaderText("Select a map name to save.");
			
			var result = dialog.showAndWait();
			result.ifPresent(map::setName);
		}
		
		mapSaveLoad.save(map, mapEditor.getPlayer());
		try {
			assetExplorer.initialize(ProjectFolderConfiguration.ASSETS.getConfiguration(), this::loadMap);
		} catch(AssetNotFoundException e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	@FXML
	private void fileLoadMap() {
		var mapDir = new File(ProjectFolderConfiguration.ASSETS_MAPS.getConfiguration());
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
			mapEditor.setMap(mapSaveLoad.load(mapName, false));
		} catch (AssetNotFoundException | AssetFormatException e) {
			var alert = new Alert(AlertType.WARNING);
			alert.setTitle("Map Load Error");
			alert.setHeaderText(null);
			alert.setContentText("There was an error while loading the map. Do you wanna try and recover the map?");
			alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
			var res = alert.showAndWait();
			if (res.isPresent() && res.get().equals(ButtonType.YES)) {
				try {
					mapEditor.setMap(mapSaveLoad.load(mapName, true));
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
		if(mapEditor.getMap() == null) {
			var alert = new Alert(AlertType.ERROR);
			alert.setTitle("Run Error");
			alert.setHeaderText(null);
			alert.setContentText("Game can not be runned without an active map");
			alert.showAndWait();
		}else {
			fileSaveMap();
			var stage = new Stage();
			try {
				var main = new GameMain(mapEditor.getMap().getName());
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
	
	private void showErrorAlert(String title, String message) {
		var alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
