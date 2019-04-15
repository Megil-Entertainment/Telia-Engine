package ch.megil.teliaengine.ui.dialog;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public class AboutDialog extends Dialog<Void> {
	private static final String ABOUT_TITLE = "About ";
	
	public AboutDialog() {
		initModality(Modality.APPLICATION_MODAL);
		
		var root = new VBox();
		
		var aboutText = new TextArea(SystemConfiguration.APP_ABOUT.getConfiguration());
		aboutText.setEditable(false);
		aboutText.setWrapText(true);
		aboutText.setFocusTraversable(false);
		root.getChildren().add(aboutText);
		
		setTitle(ABOUT_TITLE + SystemConfiguration.APP_NAME.getConfiguration());
		getDialogPane().setContent(root);
		getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
	}
}
