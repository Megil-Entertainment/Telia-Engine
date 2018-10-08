package ch.megil.teliaengine.ui;

import ch.megil.teliaengine.ui.component.MapEditor;
import ch.megil.teliaengine.ui.component.ObjectExplorer;
import javafx.fxml.FXML;

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
	private void gameRun() {
		// TODO: implement run
	}

	@FXML
	private void helpAbout() {
		new AboutDialog().showAndWait();
	}
}
