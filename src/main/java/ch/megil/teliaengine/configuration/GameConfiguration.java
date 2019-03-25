package ch.megil.teliaengine.configuration;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;

import ch.megil.teliaengine.logging.LogHandler;
import ch.megil.teliaengine.project.ProjectController;

public enum GameConfiguration {
	ASSETS("assets"),
	ASSETS_MAPS("assetsMaps"),
	ASSETS_OBJECTS("assetsObjects"),
	ASSETS_TEXTURES("assetsTextures"),
	ASSET_PLAYER("assetPlayer"),
	PLAYER_WIDTH("playerWidth"),
	PLAYER_HEIGHT("playerHeight");

	
	private static Properties gameProp;
	
	static {
		reload();
	}
	
	public static void reload() {
		gameProp = new XProperties();
		try (var in = new FileInputStream(ProjectController.get().getProjectPath() + ConfigurationContstants.GAME_CONFIGURATION)) {
			gameProp.load(in);
		} catch (Exception e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	private String key;
	
	private GameConfiguration(String key) {
		this.key = key;
	}
	
	public String getConfiguration() {
		return gameProp.getProperty(key);
	}
}
