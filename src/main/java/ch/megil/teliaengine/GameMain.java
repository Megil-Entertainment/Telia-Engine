package ch.megil.teliaengine;

import ch.megil.teliaengine.file.MapSaveLoad;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.gamelogic.GameState;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.*;

public class GameMain {
	public GameMain() {}
	
	public GameMain(String mapName) throws AssetNotFoundException, AssetFormatException {
		GameState.get().setMap(new MapSaveLoad().load(mapName, false));
	}

	public void run() {
		init();
		
		//GameLoop.get().start();
		//primaryStage.setOnHidden(e -> GameLoop.get().stop());
	}
	
	public void init() {
		if (!glfwInit()) {
			throw new RuntimeException("Unable to initialize GLFW.");
		}
		if (!glfwVulkanSupported()) {
			throw new RuntimeException("Vulkan loader could not be found.");
		}
	}

	public static void main(String[] args) {
		new GameMain().run();
	}
}
