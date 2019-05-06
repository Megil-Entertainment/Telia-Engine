package ch.megil.teliaengine.ui.dialog;

import java.io.File;
import java.util.logging.Level;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.file.PlayerFileManager;
import ch.megil.teliaengine.file.ProjectFileManager;
import ch.megil.teliaengine.file.exception.AssetCreationException;
import ch.megil.teliaengine.logging.LogHandler;
import ch.megil.teliaengine.project.Project;
import ch.megil.teliaengine.ui.dialog.wizard.Wizard;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Dialog to create and initialize a new project. Returning the project information file.
 */
public class ProjectCreateDialog extends Wizard<File> {
	private static final int PADDING = 15;
	
	private ProjectFileManager projectFileManager;
	
	private TextField projectName;
	private TextField location;
	
	private TextField playerWidth;
	private TextField playerHeight;
	private TextField playerTexture;
	
	public ProjectCreateDialog(ProjectFileManager projectFileManager) {
		this.projectFileManager = projectFileManager;
		
		addPage(createProjectInfoPage(), this::checkProjectInfoDisable);
		addPage(createPlayerCreationPage(), this::checkPlayerCreationDisable);
		
		setResultConverter(b -> b.equals(ButtonType.FINISH)
				? createProject(projectName.getText(), location.getText(),
						Double.parseDouble(playerWidth.getText()), Double.parseDouble(playerHeight.getText()), playerTexture.getText())
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
		playerTexture.setEditable(false);
		playerTexture.textProperty().addListener(super::doNextPageCheckListener);
		playerCreationGrid.add(playerTexture, 1, 1);
		var searchBtn = new Button("...");
		searchBtn.setOnAction(this::searchTexture);
		playerCreationGrid.add(searchBtn, 2, 1);
		
		return playerCreationGrid;
	}
	
	private File createProject(String projectName, String location, double playerWidth, double playerHeight, String playerTexture) {
		var projectDir = new File(location + "/" + projectName.replaceAll("\\s", ""));
		var project = new Project(projectName, projectDir);
		
		try {
			var projectInfo = projectFileManager.initProject(project);
			new PlayerFileManager().createPlayer(projectDir, playerWidth, playerHeight, new File(playerTexture));
			
			return projectInfo;
		} catch (AssetCreationException e) {
			LogHandler.log(e, Level.SEVERE);
			showErrorAlert("Create Error", "There was an error while creating a new project.");
		}
		return null;
	}
	
	private void showErrorAlert(String title, String message) {
		var alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
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
	
	private void searchTexture(ActionEvent ae) {
		var chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("Texture Image", "*" + FileConfiguration.FILE_EXT_TEXTURE.getConfiguration()));
		var texture = chooser.showOpenDialog(playerTexture.getScene().getWindow());
		if (texture != null) {
			playerTexture.setText(texture.getAbsolutePath());
		}
		
	}
	
	private boolean checkProjectInfoDisable() {
		return projectName.getText().trim().isEmpty() || location.getText().trim().isEmpty();
	}
	
	private boolean checkPlayerCreationDisable() {
		try {
			var width = Double.parseDouble(playerWidth.getText());
			var height = Double.parseDouble(playerHeight.getText());

			return width <= 0.0 || height <= 0.0 || playerTexture.getText().trim().isEmpty();
		} catch (NumberFormatException e) {
			return true;
		}
	}
}
