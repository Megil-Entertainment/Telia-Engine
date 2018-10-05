package ch.megil.teliaengine.configuration;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;

import ch.megil.teliaengine.logging.LogHandler;

public enum SystemConfiguration {
	APP_NAME("appName"),
	APP_ABOUT("appAbout");
	
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
	
	private SystemConfiguration(String key) {
		this.key = key;
	}
	
	public String getConfiguration() {
		return sysProp.getProperty(key);
	}
}
