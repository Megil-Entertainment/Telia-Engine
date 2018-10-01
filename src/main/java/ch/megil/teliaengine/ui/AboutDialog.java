package ch.megil.teliaengine.ui;

import ch.megil.teliaengine.TeliaConfiguration;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public class AboutDialog extends Dialog<Void> {
	private static final String ABOUT_TITLE = "About " + TeliaConfiguration.APP_NAME;
	private static final String ABOUT_TEXT = TeliaConfiguration.APP_NAME +
			"\n\nVersion:\t" + TeliaConfiguration.APP_VERSION +
			"\n\nMade by:\t" + TeliaConfiguration.APP_COMPANY + 
			"\n\t\t\t" + TeliaConfiguration.APP_AUTHORS;
	
	public AboutDialog() {
		initModality(Modality.APPLICATION_MODAL);
		
		var root = new VBox();
		
		var aboutText = new TextArea(ABOUT_TEXT);
		aboutText.setEditable(false);
		aboutText.setWrapText(true);
		aboutText.setFocusTraversable(false);
		root.getChildren().add(aboutText);
		
		setTitle(ABOUT_TITLE);
		getDialogPane().setContent(root);
		getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
	}
}
