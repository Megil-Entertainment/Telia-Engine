package ch.megil.teliaengine.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.configuration.IconConfiguration;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import javafx.scene.image.Image;

public class IconFileManager {
	private static IconFileManager instance;
	
	private Map<String, Image> cache;
	
	private IconFileManager() {
		cache = new HashMap<String, Image>();
	}
	
	public static IconFileManager get() {
		if (instance == null) {
			instance = new IconFileManager();
		}
		return instance;
	}
	
	public Image load(String name, double width, double height) throws AssetNotFoundException {
		if (cache.containsKey(name)) {
			var img = cache.get(name);
			if (img.getWidth() >= width && img.getHeight() >= height) {
				return img;
			}
		}
		
		var fileName = IconConfiguration.ICONS.getConfiguration() + "/" + name + FileConfiguration.FILE_EXT_TEXTURE.getConfiguration();
		var file = new File(fileName);
		
		try (var is = new FileInputStream(file)) {
			var obj = new Image(is, width, height, false, false);
			
			cache.put(name, obj);
			return obj;
		} catch (IOException e) {
			throw new AssetNotFoundException("Icon not found: " + name, e);
		}
	}
}
