package ch.megil.teliaengine.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.configuration.ProjectFolderConfiguration;
import ch.megil.teliaengine.file.exception.AssetCreationException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import javafx.scene.image.Image;

public class TextureFileManager {
	private static TextureFileManager instance;
	
	private Map<String, Image> cache;
	
	private TextureFileManager() {
		cache = new HashMap<String, Image>();
	}
	
	public static TextureFileManager get() {
		if (instance == null) {
			instance = new TextureFileManager();
		}
		return instance;
	}
	
	public void clearCache() {
		cache.clear();
	}
	
	public Image load(String name, double width, double height) throws AssetNotFoundException {
		if (cache.containsKey(name)) {
			var tex = cache.get(name);
			if (tex.getWidth() >= width && tex.getHeight() >= height) {
				return tex;
			}
		}
		
		var fileName = ProjectFolderConfiguration.ASSETS_TEXTURES.getConfigurationWithProjectPath() + "/" + name + FileConfiguration.FILE_EXT_TEXTURE.getConfiguration();
		var file = new File(fileName);
		
		try (var is = new FileInputStream(file)) {
			var obj = new Image(is, width, height, false, false);
			
			cache.put(name, obj);
			return obj;
		} catch (IOException e) {
			throw new AssetNotFoundException("Texture not found: " + name, e);
		}
	}
	
	public void importTexture(String name, File original) throws AssetCreationException {
		var origPath = original.toPath();
		var destPath = new File(ProjectFolderConfiguration.ASSETS_TEXTURES.getConfigurationWithProjectPath() + "/" + name + FileConfiguration.FILE_EXT_TEXTURE.getConfiguration()).toPath();
		
		try {
			Files.copy(origPath, destPath);
		} catch (IOException e) {
			throw new AssetCreationException("Texture not created: " + name, e);
		}
	}
	
	public void importTextureToOtherProject(File projectDir, String name, File original) throws AssetCreationException {
		var origPath = original.toPath();
		var destPath = new File(projectDir.getAbsolutePath() + ProjectFolderConfiguration.ASSETS_TEXTURES.getConfigurationWithoutProjectPath() + "/" + name + FileConfiguration.FILE_EXT_TEXTURE.getConfiguration()).toPath();
		
		try {
			Files.copy(origPath, destPath);
		} catch (IOException e) {
			throw new AssetCreationException("Texture not created: " + name, e);
		}
	}
}
