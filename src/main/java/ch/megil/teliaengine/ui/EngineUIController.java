package ch.megil.teliaengine.ui;

import ch.megil.teliaengine.file.MapSaveLoad;
import ch.megil.teliaengine.ui.component.MapEditor;
import ch.megil.teliaengine.ui.component.ObjectExplorer;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;

public class EngineUIController {
	@FXML
	private MapEditor mapEditor;

	@FXML
	private ObjectExplorer objectExplorer;
	
	@FXML
	private void initialize() {
		objectExplorer.setMapEditor(mapEditor);
	}

	@FXML
	private void fileSave() {
		var dialog = new TextInputDialog();
		dialog.setTitle("Map Name");
		dialog.setHeaderText("Select a map name to save");
		
		var result = dialog.showAndWait();
		result.ifPresent(n -> new MapSaveLoad().save(mapEditor.getMap(), n));
	}
	
	@FXML
	private void gameRun() {
		// TODO: implement run
	}

	@FXML
	private void helpAbout() {
		new AboutDialog().showAndWait();
	}
}
