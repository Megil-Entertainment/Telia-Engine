package ch.megil.teliaengine;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.file.MapSaveLoad;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.gamelogic.GameLoop;
import ch.megil.teliaengine.gamelogic.GameState;
import ch.megil.teliaengine.ui.game.GameMap;
import javafx.application.Application;
import javafx.scene.Scene;
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
		GameState.get().setMap(new MapSaveLoad().load(mapName, false));
		root = new GameMap();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		var scene = new Scene(root);

		primaryStage.setMaximized(true);
//		primaryStage.setFullScreen(true);
//		primaryStage.setFullScreenExitHint(null);
//		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.setScene(scene);
		primaryStage.setTitle(SystemConfiguration.GAME_NAME.getConfiguration());
		primaryStage.setOnHidden(e -> GameLoop.get().stop());
		primaryStage.show();
		root.requestFocus();
		
		GameLoop.get().start();
	}

	public static void main(String[] args) throws AssetNotFoundException, AssetFormatException {
		launch(args);
	}
}
