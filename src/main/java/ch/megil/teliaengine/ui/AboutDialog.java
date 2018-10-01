package ch.megil.teliaengine.ui;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public class AboutDialog extends Dialog<Void> {
	public AboutDialog() {
		initModality(Modality.APPLICATION_MODAL);
		
		var root = new VBox();
		
		var aboutText = new TextArea();
		aboutText.setEditable(false);
		aboutText.setWrapText(true);
		aboutText.setFocusTraversable(false);
		root.getChildren().add(aboutText);
		
		getDialogPane().setContent(root);
		getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
	}
}
