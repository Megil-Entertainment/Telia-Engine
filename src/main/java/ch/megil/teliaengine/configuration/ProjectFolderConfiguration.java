package ch.megil.teliaengine.configuration;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;

import ch.megil.teliaengine.logging.LogHandler;
import ch.megil.teliaengine.project.ProjectController;

public enum ProjectFolderConfiguration {
	ASSETS_MAPS("assetsMaps"),
	ASSETS_OBJECTS("assetsObjects"),
	ASSETS_TEXTURES("assetsTextures"),
	ASSET_PLAYER("assetPlayer");

	private static Properties projectfolderProp;
	
	static {
		reload();
	}
	
	public static void reload() {
		projectfolderProp = new XProperties();
		try (var in = new FileInputStream(ConfigurationContstants.PROJEC_FOLDER_CONFIGURATION)) {
			projectfolderProp.load(in);
		} catch (Exception e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	private String key;
	
	private ProjectFolderConfiguration(String key) {
		this.key = key;
	}
	
	public String getConfigurationWithoutProjectPath() {
		return projectfolderProp.getProperty(key);
	}
	
	public String getConfigurationWithProjectPath() {
		return ProjectController.get().getProjectPath() + projectfolderProp.getProperty(key);
	}
}
