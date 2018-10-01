package ch.megil.teliaengine.ui;

import javafx.fxml.FXML;

public class EngineUIController {
	@FXML
	private void gameRun() {
		// TODO: implement run
	}

	@FXML
	private void helpAbout() {
		new AboutDialog().showAndWait();
	}
}
