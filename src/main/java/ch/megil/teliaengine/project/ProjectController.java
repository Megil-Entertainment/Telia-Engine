package ch.megil.teliaengine.project;

import java.io.File;

import ch.megil.teliaengine.configuration.PhysicsConstants;

public class ProjectController {
	private static ProjectController instance;
	
	private File project;
	
	private ProjectController() {
		project = new File(".");
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
		//TODO: Refresh Configuration
		PhysicsConstants.reload();
	}
	
	public String getProjectPath() {
		return project.getAbsolutePath();
	}
}
