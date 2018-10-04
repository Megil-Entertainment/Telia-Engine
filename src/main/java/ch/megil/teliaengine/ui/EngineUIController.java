package ch.megil.teliaengine.ui;

import ch.megil.teliaengine.ui.component.MapEditor;
import javafx.fxml.FXML;

public class EngineUIController {
	@FXML
	private MapEditor mapEditor;

	@FXML
	private void fileSave() {
		// TODO: implement save
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
