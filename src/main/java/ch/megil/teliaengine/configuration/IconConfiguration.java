package ch.megil.teliaengine.configuration;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;

import ch.megil.teliaengine.logging.LogHandler;

public enum IconConfiguration {
	ICONS("icons"),
	FILE_ICON("fileIcon"),
	FOLDER_ICON("folderIcon");
	
	private static Properties iconProp;
	
	static {
		iconProp = new XProperties();

		try (var in = new FileInputStream(ConfigurationContstants.ICON_CONFIGURATION)) {
			iconProp.load(in);
		} catch (Exception e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	private String key;
	
	private IconConfiguration(String key) {
		this.key = key;
	}
	
	public String getConfiguration() {
		return iconProp.getProperty(key);
	}
}
