package ch.megil.teliaengine.configuration;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;

import ch.megil.teliaengine.logging.LogHandler;

public enum FileConfiguration {
	FILE_EXT_MAP("fileExtMap"),
	FILE_EXT_OBJECT("fileExtObject"),
	FILE_EXT_TEXTURE("fileExtTexture"),
	FILE_EXT_PROJECT("fileExtProject"),
	SEPARATOR_ENTRY("separatorEntry"),
	SEPERATOR_PROPERTY("seperatorProperty");
	
private static Properties fileProp;
	
	static {
		fileProp = new XProperties();

		try (var in = new FileInputStream(ConfigurationContstants.FILE_CONFIGURATION)) {
			fileProp.load(in);
		} catch (Exception e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	private String key;
	
	private FileConfiguration(String key) {
		this.key = key;
	}
	
	public String getConfiguration() {
		return fileProp.getProperty(key);
	}
}
