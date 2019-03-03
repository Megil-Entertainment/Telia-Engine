package ch.megil.teliaengine.ui.component;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AssetTestMain extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Tree View Sample");        
		var root = new StackPane();
		root.getChildren().add(new AssetTest("assets"));
		primaryStage.setScene(new Scene(root, 300, 250));
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
