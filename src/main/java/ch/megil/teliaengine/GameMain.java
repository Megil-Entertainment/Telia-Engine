package ch.megil.teliaengine;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.file.MapSaveLoad;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.gamelogic.GameState;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

public class GameMain {
	private static final int VK_VERSION = VK_MAKE_VERSION(1, 0, 2);
	
	private VkInstance instance;
	
	public GameMain() {}
	
	public GameMain(String mapName) throws AssetNotFoundException, AssetFormatException {
		GameState.get().setMap(new MapSaveLoad().load(mapName, false));
	}

	public void run() throws VulkanException {
		init();
		
		vkDestroyInstance(instance, null);
		instance = null;
		
		//GameLoop.get().start();
		//primaryStage.setOnHidden(e -> GameLoop.get().stop());
	}
	
	private void init() throws VulkanException {
		if (!glfwInit()) {
			throw new RuntimeException("Unable to initialize GLFW.");
		}
		if (!glfwVulkanSupported()) {
			throw new RuntimeException("Vulkan loader could not be found.");
		}
		
		instance = createInstance();
	}
	
	private VkInstance createInstance() throws VulkanException {
		var appInfo = VkApplicationInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
				.pApplicationName(memUTF8(SystemConfiguration.GAME_NAME.getConfiguration()))
				.pEngineName(memUTF8(SystemConfiguration.APP_NAME.getConfiguration()))
				.apiVersion(VK_VERSION);
		
		var instInfo = VkInstanceCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
				.pNext(NULL)
				.pApplicationInfo(appInfo);
		
		var pInst = memAllocPointer(1);
		var res = vkCreateInstance(instInfo, null, pInst);
		
		try {
			errorCheck(res);
			var instance = new VkInstance(pInst.get(0), instInfo);
			return instance;
		} finally {
			pInst.free();
			instInfo.free();
			memFree(appInfo.pApplicationName());
			memFree(appInfo.pEngineName());
			appInfo.free();
		}
	}
	
	private void errorCheck(int resultCode) throws VulkanException {
		if (resultCode != VK_SUCCESS) {
			throw new VulkanException(resultCode);
		}
	}

	public static void main(String[] args) throws Exception {
		new GameMain().run();
	}
}
