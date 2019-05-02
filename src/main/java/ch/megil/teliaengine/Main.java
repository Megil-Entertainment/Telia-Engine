package ch.megil.teliaengine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.configuration.ProjectFolderConfiguration;
import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.file.ProjectFileManager;
import ch.megil.teliaengine.file.TextureFileManager;
import ch.megil.teliaengine.file.exception.AssetCreationException;
import ch.megil.teliaengine.file.exception.AssetLoadException;
import ch.megil.teliaengine.logging.LogHandler;
import ch.megil.teliaengine.project.ProjectController;
import ch.megil.teliaengine.ui.FXMLConfiguration;
import ch.megil.teliaengine.ui.dialog.ProjectCreateDialog;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		var projectFileManager = new ProjectFileManager();
		var lastProjectInfo = projectFileManager.getLastOpenedProject();
		if (lastProjectInfo != null) {
			var project = projectFileManager.loadProject(new File(lastProjectInfo));
			ProjectController.get().openProject(project);
		} else {
			openCreationOpenChooser(primaryStage, projectFileManager);
		}
		
		Pane root = FXMLLoader.load(FXMLConfiguration.ENGINE_UI);

		var scene = new Scene(root);

		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.setTitle(SystemConfiguration.APP_NAME.getConfiguration());
		primaryStage.show();
	}
	
	private void openCreationOpenChooser(Stage stage, ProjectFileManager projectFileManager) {
		Alert creationChooser = new Alert(AlertType.NONE);
		creationChooser.setTitle(SystemConfiguration.APP_NAME.getConfiguration());
		creationChooser.setContentText("Do you want to open an existing project or create a new one.");
		var buttonNew = new ButtonType("Create new");
		var buttonOpen = new ButtonType("Open Existing");
		creationChooser.getButtonTypes().setAll(buttonNew, buttonOpen, ButtonType.CANCEL);
		
		var retType = creationChooser.showAndWait().get();
		if (retType == buttonNew) {
			openCreateDialog(projectFileManager);
		} else if (retType == buttonOpen) {
			openOpenDialog(stage, projectFileManager);
		} else {
			System.exit(0);
		}
	}
	
	private void openCreateDialog(ProjectFileManager projectFileManager) {
		var project = new ProjectCreateDialog().showAndWait().get();
		
		if (project == null) {
			System.exit(0);
		} else {
			try {
				var projectInfo = projectFileManager.initProject(project);
				projectFileManager.updateLastOpenedProject(projectInfo);
				ProjectController.get().openProject(project);
				//TODO: as soon as created: open ObjectCreator to create player and remove static player creation
				TextureFileManager.get().importTexture("player", new File("assets/texture/player.png"));
				var origin = new File("assets/player.tobj").toPath();
				var dest = new File(ProjectFolderConfiguration.ASSET_PLAYER.getConfigurationWithProjectPath() + ".tobj").toPath();
				try {
					Files.copy(origin, dest);
				} catch (IOException e) {
					e.printStackTrace();
				}
				ProjectController.get().openProject(project);
			} catch (AssetCreationException e) {
				LogHandler.log(e, Level.SEVERE);
				Alert error = new Alert(AlertType.ERROR);
				error.setContentText("Could not create Project.");
				error.showAndWait();
				System.exit(-1);
			}
		}
	}
	
	private void openOpenDialog(Stage stage, ProjectFileManager projectFileManager) {
		var chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("Project", "*" + FileConfiguration.FILE_EXT_PROJECT.getConfiguration()));
		var projectInfo = chooser.showOpenDialog(stage);
		
		if (projectInfo == null) {
			System.exit(0);
		} else {
			try {
				var project = projectFileManager.loadProject(projectInfo);
				projectFileManager.updateLastOpenedProject(projectInfo);
				ProjectController.get().openProject(project);
			} catch (AssetLoadException e) {
				LogHandler.log(e, Level.SEVERE);
				Alert error = new Alert(AlertType.ERROR);
				error.setContentText("Could not open Project.");
				error.showAndWait();
				System.exit(-1);
			} catch (AssetCreationException e) {
				LogHandler.log(e, Level.WARNING);
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
