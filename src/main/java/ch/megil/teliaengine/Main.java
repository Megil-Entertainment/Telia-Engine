package ch.megil.teliaengine;

import java.io.File;
import java.util.logging.Level;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.file.ProjectFileManager;
import ch.megil.teliaengine.file.exception.AssetCreationException;
import ch.megil.teliaengine.file.exception.AssetLoadException;
import ch.megil.teliaengine.logging.LogHandler;
import ch.megil.teliaengine.project.ProjectController;
import ch.megil.teliaengine.ui.FXMLConfiguration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

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
			//TODO: create
		} else if (retType == buttonOpen) {
			openOpenDialog(stage, projectFileManager);
		} else {
			System.exit(0);
		}
	}
	
	private void openOpenDialog(Stage stage, ProjectFileManager projectFileManager) {
		var chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("Project", "*" + FileConfiguration.FILE_EXT_PROJECT.getConfiguration()));
		var projectInfo = chooser.showOpenDialog(stage);
		if (projectInfo != null) {
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
		} else {
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
