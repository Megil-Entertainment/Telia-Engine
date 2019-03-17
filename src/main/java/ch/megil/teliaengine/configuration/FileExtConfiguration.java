package ch.megil.teliaengine.configuration;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;

import ch.megil.teliaengine.logging.LogHandler;

public enum FileExtConfiguration {
	FILE_EXT_MAP("fileExtMap"),
	FILE_EXT_OBJECT("fileExtObject"),
	FILE_EXT_TEXTURE("fileExtTexture");
	
private static Properties fileExtProp;
	
	static {
		fileExtProp = new XProperties();

		try (var in = new FileInputStream(ConfigurationContstants.FILE_EXT_CONFIGURATION)) {
			fileExtProp.load(in);
		} catch (Exception e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	private String key;
	
	private FileExtConfiguration(String key) {
		this.key = key;
	}
	
	public String getConfiguration() {
		return fileExtProp.getProperty(key);
	}
}
