package ch.megil.teliaengine.ui.dialog;

import java.io.File;

import ch.megil.teliaengine.project.Project;
import ch.megil.teliaengine.ui.dialog.wizard.Wizard;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

public class ProjectCreateDialog extends Wizard<Project> {
	private static final int PADDING = 15;
	
	private TextField projectName;
	private TextField location;
	
	private TextField playerWidth;
	private TextField playerHeight;
	private TextField playerTexture;
	
	public ProjectCreateDialog() {
		addPage(createProjectInfoPage(), this::checkProjectInfoDisable);
		addPage(createPlayerCreationPage(), this::checkPlayerCreationDisable);
		
		setResultConverter(b -> b.equals(ButtonType.FINISH)
				? createProject(projectName.getText(), location.getText())
				: null);
	}
	
	private GridPane createProjectInfoPage() {
		var projectInfoGrid = new GridPane();
		projectInfoGrid.setPadding(new Insets(PADDING));
		projectInfoGrid.setHgap(PADDING);
		projectInfoGrid.setVgap(PADDING);
		
		projectInfoGrid.add(new Label("Project Name"), 0, 0);
		projectName = new TextField();
		projectName.textProperty().addListener(super::doNextPageCheckListener);
		Platform.runLater(() -> projectName.requestFocus());
		projectInfoGrid.add(projectName, 1, 0, 2, 1);
		
		projectInfoGrid.add(new Label("Location"), 0, 1);
		location = new TextField();
		location.textProperty().addListener(super::doNextPageCheckListener);
		projectInfoGrid.add(location, 1, 1);
		var searchBtn = new Button("...");
		searchBtn.setOnAction(this::searchDirectory);
		projectInfoGrid.add(searchBtn, 2, 1);
		
		return projectInfoGrid;
	}
	
	private GridPane createPlayerCreationPage() {
		var playerCreationGrid = new GridPane();
		playerCreationGrid.setPadding(new Insets(PADDING));
		playerCreationGrid.setHgap(PADDING);
		playerCreationGrid.setVgap(PADDING);
		
		playerCreationGrid.add(new Label("Player Width / Height"), 0, 0);
		playerWidth = new TextField();
		playerWidth.textProperty().addListener(super::doNextPageCheckListener);
		playerCreationGrid.add(playerWidth, 1, 0);
		playerHeight = new TextField();
		playerHeight.textProperty().addListener(super::doNextPageCheckListener);
		playerCreationGrid.add(playerHeight, 2, 0);
		
		playerCreationGrid.add(new Label("Player texture"), 0, 1);
		playerTexture = new TextField();
		playerTexture.textProperty().addListener(super::doNextPageCheckListener);
		playerCreationGrid.add(playerTexture, 1, 1);
		var searchBtn = new Button("...");
		searchBtn.setOnAction(this::searchDirectory);
		playerCreationGrid.add(searchBtn, 2, 1);
		return playerCreationGrid;
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
	
	private boolean checkProjectInfoDisable() {
		return projectName.getText().trim().isEmpty() || location.getText().trim().isEmpty();
	}
	
	private boolean checkPlayerCreationDisable() {
		try {
			var width = Integer.parseInt(playerWidth.getText());
			var height = Integer.parseInt(playerHeight.getText());

			return width <= 0 || height <= 0 || playerTexture.getText().trim().isEmpty();
		} catch (NumberFormatException e) {
			return true;
		}
	}
}
