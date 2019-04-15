package ch.megil.teliaengine.ui.dialog;

import ch.megil.teliaengine.project.Project;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ProjectCreateDialog extends Dialog<Project> {
	private static final int PADDING = 15;

	private Node createBtn;
	private TextField projectName;
	private TextField location;
	
	public ProjectCreateDialog() {
		var createType = new ButtonType("Create", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().setAll(createType, ButtonType.CANCEL);
		createBtn = getDialogPane().lookupButton(createType);
		createBtn.setDisable(true);
		
		var grid = new GridPane();
		grid.setPadding(new Insets(PADDING));
		grid.setHgap(PADDING);
		grid.setVgap(PADDING);
		
		grid.add(new Label("Project Name"), 0, 0);
		projectName = new TextField();
		//projectName.textProperty().addListener(this::enableCreate);
		Platform.runLater(() -> projectName.requestFocus());
		grid.add(projectName, 1, 0, 2, 1);
		
		grid.add(new Label("Location"), 0, 1);
		location = new TextField();
//		width.textProperty().addListener(this::enableCreate);
		grid.add(location, 1, 1);
		
		grid.add(new Button("..."), 2, 1);
		
		getDialogPane().setContent(grid);
		
//		setResultConverter(b -> b.equals(createType)
//				? new Map(mapName.getText(), Integer.parseInt(width.getText()), Integer.parseInt(height.getText()))
//				: null);
	}
}
