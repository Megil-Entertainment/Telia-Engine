package ch.megil.teliaengine.configuration;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;

import ch.megil.teliaengine.logging.LogHandler;

public enum ObjectListConfiguration {
	
	OBJECT_LIST_BG("objectListBg"),
	OBJECT_LIST_HOVER("objectListHover");
	
private static Properties sysProp;
	
	static {
		sysProp = new XProperties();

		try (var in = new FileInputStream(ConfigurationContstants.SYSTEM_CONFIGURATION)) {
			sysProp.load(in);
		} catch (Exception e) {
			LogHandler.log(e, Level.SEVERE);
		}
	}
	
	private String key;
	
	private ObjectListConfiguration(String key) {
		this.key = key;
	}
	
	public String getConfiguration() {
		return sysProp.getProperty(key);
	}
}
