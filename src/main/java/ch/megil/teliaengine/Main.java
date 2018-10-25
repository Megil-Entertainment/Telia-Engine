package ch.megil.teliaengine;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.ui.FXMLConfiguration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
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
