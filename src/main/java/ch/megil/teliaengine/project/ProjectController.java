package ch.megil.teliaengine.project;

import java.io.File;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.configuration.PhysicsConstants;
import ch.megil.teliaengine.file.TextureFileManager;
import ch.megil.teliaengine.file.exception.AssetLoadException;

public class ProjectController {
	private final String DOT_DIRECTORY = ".";
	private static ProjectController instance;
	
	private Project project;
	
	private ProjectController() {
		project = new Project("", new File(DOT_DIRECTORY));
	}
	
	public static ProjectController get() {
		if(instance == null) {
			instance = new ProjectController();
		}
		return instance;
	}
	
	public void openProject(Project project) throws AssetLoadException {
		this.project = project;
		refresh();
	}
	
	private void refresh() throws AssetLoadException {
		PhysicsConstants.reload();
		GameConfiguration.reload();
		TextureFileManager.get().clearCache();
	}
	
	public String getProjectPath() {
		return project.getLocationPath();
	}
}
