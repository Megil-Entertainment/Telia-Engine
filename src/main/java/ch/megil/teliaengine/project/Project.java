package ch.megil.teliaengine.project;

import java.io.File;

public class Project {
	private String name;
	private File location;
	
	public Project(String name, File location) {
		super();
		this.name = name;
		this.location = location;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLocationPath() {
		return location.getAbsolutePath();
	}
}
