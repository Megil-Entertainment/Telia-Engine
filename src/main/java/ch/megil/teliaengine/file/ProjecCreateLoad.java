package ch.megil.teliaengine.file;

import java.io.File;

import ch.megil.teliaengine.project.Project;

public class ProjecCreateLoad {
	public void initProject(Project project) {
		
	}
	
	public Project loadProject(File projectLocation) {
		return new Project("", projectLocation);
	}
}
