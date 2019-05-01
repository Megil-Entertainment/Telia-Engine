package ch.megil.teliaengine;

import java.io.File;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.file.ProjectFileManager;
import ch.megil.teliaengine.project.ProjectController;
import ch.megil.teliaengine.ui.FXMLConfiguration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
		}
		
		Pane root = FXMLLoader.load(FXMLConfiguration.ENGINE_UI);

		var scene = new Scene(root);

		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.setTitle(SystemConfiguration.APP_NAME.getConfiguration());
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
