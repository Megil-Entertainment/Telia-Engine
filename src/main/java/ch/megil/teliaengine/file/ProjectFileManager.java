package ch.megil.teliaengine.file;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

import ch.megil.teliaengine.configuration.ConfigurationContstants;
import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.configuration.ProjectFolderConfiguration;
import ch.megil.teliaengine.file.exception.AssetCreationException;
import ch.megil.teliaengine.file.exception.AssetLoadException;
import ch.megil.teliaengine.project.Project;

public class ProjectFileManager {
	private static final String LAST_PROJECT = "project.last";
	
	private static final String KEY_PROJECT_NAME = "pName";
	
	public File initProject(Project project) throws AssetCreationException {
		var root = project.getLocationPath();
		new File(root).mkdirs();
		if (!new File(root + ProjectFolderConfiguration.ASSETS_MAPS.getConfigurationWithoutProjectPath()).mkdirs() ||
				!new File(root + ProjectFolderConfiguration.ASSETS_OBJECTS.getConfigurationWithoutProjectPath()).mkdirs() ||
				!new File(root + ProjectFolderConfiguration.ASSETS_TEXTURES.getConfigurationWithoutProjectPath()).mkdirs() ||
				!new File(root + "/" + ConfigurationContstants.CONFIGURATION_DIR).mkdirs() ||
				!new File(root + "/" + ConfigurationContstants.CONSTANT_DIR).mkdirs()) {
			throw new AssetCreationException("Directory structure not creatable.");
		}
		
		var properties = new Properties();
		var projectInfo = new File(root + "/" + project.getName().replaceAll("\\s", "") + FileConfiguration.FILE_EXT_PROJECT.getConfiguration());
		
		try (var projectOut = new FileOutputStream(projectInfo);
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
			
			return projectInfo;
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
	
	public void updateLastOpenedProject(File projectInfo) throws AssetCreationException {
		try (var writer = new BufferedWriter(new FileWriter(LAST_PROJECT))) {
			writer.write(projectInfo.getAbsolutePath());
		} catch (IOException e) {
			throw new AssetCreationException(e);
		}
	}
	
	public String getLastOpenedProject() throws AssetLoadException {
		var lastProjectInfo = new File(LAST_PROJECT);
		if (!lastProjectInfo.exists()) {
			return null;
		}
		try (var scanner = new Scanner(lastProjectInfo)) {
			scanner.useDelimiter("\n");
			var lastProject = scanner.next();
			if (new File(lastProject).exists()) {
				return lastProject;
			}
		} catch (IOException e) {
			throw new AssetLoadException(e);
		}
		return null;
	}
}
