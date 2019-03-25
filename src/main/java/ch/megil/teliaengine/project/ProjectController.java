package ch.megil.teliaengine.project;

import java.io.File;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.configuration.PhysicsConstants;

public class ProjectController {
	private final String DOT_DIRECTORY = ".";
	private static ProjectController instance;
	
	private File project;
	
	private ProjectController() {
		project = new File(DOT_DIRECTORY);
	}
	
	public static ProjectController get() {
		if(instance == null) {
			instance = new ProjectController();
		}
		return instance;
	}
	
	public void openProject(File projectFolder) {
		project = projectFolder;
		refresh();
	}
	
	private void refresh() {
		PhysicsConstants.reload();
		GameConfiguration.reload();
	}
	
	public String getProjectPath() {
		return project.getAbsolutePath();
	}
}