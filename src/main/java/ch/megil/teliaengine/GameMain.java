package ch.megil.teliaengine;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.ui.game.GameMap;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameMain extends Application {
	private Pane root;

	public GameMain() {
		super();
		root = new BorderPane();
	}

	public GameMain(String mapName) throws AssetNotFoundException, AssetFormatException {
		super();
		root = new GameMap(mapName);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		var scene = new Scene(root);

//		primaryStage.setMaximized(true);
		primaryStage.setFullScreen(true);
		primaryStage.setFullScreenExitHint(null);
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.setScene(scene);
		primaryStage.setTitle(SystemConfiguration.GAME_NAME.getConfiguration());
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
