package ch.megil.teliaengine.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import ch.megil.teliaengine.configuration.ConfigurationContstants;
import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.file.exception.AssetCreationException;
import ch.megil.teliaengine.file.exception.AssetLoadException;
import ch.megil.teliaengine.project.Project;

public class ProjecFileManager {
	private static final String KEY_PROJECT_NAME = "pName";
	
	public void initProject(Project project) throws AssetCreationException {
		var root = project.getLocationPath();
		new File(root).mkdirs();
		if (!new File(root + "/assets/maps").mkdirs() ||
				!new File(root + "/assets/object").mkdirs() ||
				!new File(root + "/assets/texture").mkdirs() ||
				!new File(root + "/config").mkdirs() ||
				!new File(root + "/const").mkdirs()) {
			throw new AssetCreationException("Directory structure not creatable.");
		}
		
		var properties = new Properties();
		
		try (var projectOut = new FileOutputStream(root + "/" + project.getName().replaceAll("\\s", "") + FileConfiguration.FILE_EXT_PROJECT.getConfiguration());
				var constPhysicsIn = new FileInputStream("." + ConfigurationContstants.PHYSIC_CONSTANTS);
				var constPhysicsOut = new FileOutputStream(root + ConfigurationContstants.PHYSIC_CONSTANTS);
				var configGameIn = new FileInputStream("." + ConfigurationContstants.GAME_CONFIGURATION);
				var configGameOut = new FileOutputStream(root + ConfigurationContstants.GAME_CONFIGURATION)) {

			//project info
			properties.setProperty(KEY_PROJECT_NAME, project.getName());
			properties.store(projectOut, null);
			properties.clear();
			
			//physics constants
			properties.load(constPhysicsIn);
			properties.store(constPhysicsOut, null);
			properties.clear();
			
			//game conifiguration
			properties.load(configGameIn);
			properties.store(configGameOut, null);
			properties.clear();
		} catch (IOException e) {
			throw new AssetCreationException("Default project properties not created.", e);
		}
	}
	
	public Project loadProject(File projectInfo) throws AssetLoadException {
		var projectProps = new Properties();
		try (var projectInfoIn = new FileInputStream(projectInfo)) {
			projectProps.load(projectInfoIn);
			
			return new Project(projectProps.getProperty(KEY_PROJECT_NAME), projectInfo.getParentFile());	
		} catch (IOException e) {
			throw new AssetLoadException("Project could not be loaded.", e);
		}
	}
}
