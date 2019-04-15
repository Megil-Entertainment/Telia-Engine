package ch.megil.teliaengine.ui.dialog;

import java.io.File;

import ch.megil.teliaengine.project.Project;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

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
		projectName.textProperty().addListener(this::enableCreate);
		Platform.runLater(() -> projectName.requestFocus());
		grid.add(projectName, 1, 0, 2, 1);
		
		grid.add(new Label("Location"), 0, 1);
		location = new TextField();
		location.textProperty().addListener(this::enableCreate);
		grid.add(location, 1, 1);
		var searchBtn = new Button("...");
		searchBtn.setOnAction(this::searchDirectory);
		grid.add(searchBtn, 2, 1);
		
		getDialogPane().setContent(grid);
		
		setResultConverter(b -> b.equals(createType)
				? createProject(projectName.getText(), location.getText())
				: null);
	}
	
	private Project createProject(String projectName, String location) {
		var projectDir = location + "/" + projectName.replaceAll("\\s", "");
		return new Project(projectName, new File(projectDir));
	}
	
	private File getLowestExistingDir(File dir) {
		if (dir.exists()) {
			return dir;
		} else {
			return getLowestExistingDir(dir.getParentFile());
		}
	}
	
	private void searchDirectory(ActionEvent ae) {
		var chooser = new DirectoryChooser();
		if (!location.getText().equals("")) {
			chooser.setInitialDirectory(getLowestExistingDir(new File(location.getText())));
		}
		
		var dir = chooser.showDialog(location.getScene().getWindow());
		if (dir != null) {
			location.setText(dir.getAbsolutePath());
		}
	}
	
	private <T> void enableCreate(ObservableValue<? extends T> obs, T oldVal, T newVal) {
		createBtn.setDisable(projectName.getText().trim().isEmpty() || location.getText().trim().isEmpty());
	}
}
