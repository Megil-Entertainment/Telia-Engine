package ch.megil.teliaengine.file;

import java.io.File;

import ch.megil.teliaengine.project.Project;

public class ProjecCreateLoad {
	public void initProject(Project project) {
		var root = project.getLocationPath();
		if (!new File(root).mkdirs()) {
			throw new RuntimeException("directory error"); //TODO: new exception
		}
		
		new File(root + "/assets/maps").mkdirs();
		new File(root + "/assets/object").mkdirs();
		new File(root + "/assets/texture").mkdirs();
		new File(root + "/config").mkdirs();
		new File(root + "/const").mkdirs();
	}
	
	public Project loadProject(File projectLocation) {
		return new Project("", projectLocation);
	}
}
