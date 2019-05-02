package ch.megil.teliaengine;

import java.io.File;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.file.ProjectFileManager;
import ch.megil.teliaengine.project.ProjectController;
import ch.megil.teliaengine.ui.FXMLConfiguration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
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
			openCreationChooser();
		}
		
		Pane root = FXMLLoader.load(FXMLConfiguration.ENGINE_UI);

		var scene = new Scene(root);

		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.setTitle(SystemConfiguration.APP_NAME.getConfiguration());
		primaryStage.show();
	}
	
	private void openCreationChooser() {
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
			//TODO: open
		} else {
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
