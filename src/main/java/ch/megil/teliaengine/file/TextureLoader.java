package ch.megil.teliaengine.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import javafx.scene.image.Image;

public class TextureLoader {
	public Image load(String name, double width, double height) throws AssetNotFoundException {
		var fileName = GameConfiguration.ASSETS_TEXTURES.getConfiguration() + "/" + name + GameConfiguration.FILE_EXT_TEXTURE.getConfiguration();
		var file = new File(fileName);
		
		try (var is = new FileInputStream(file)) {
			var obj = new Image(is, width, height, false, false);
			
			return obj;
		} catch (IOException e) {
			throw new AssetNotFoundException("Texture not found: " + name, e);
		}
	}
}
