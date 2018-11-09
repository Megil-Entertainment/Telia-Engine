package ch.megil.teliaengine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.*;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.file.MapSaveLoad;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.gamelogic.GameState;
import ch.megil.teliaengine.vulkan.*;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

public class GameMain {
	private static final int VK_VERSION = VK_MAKE_VERSION(1, 0, 2);
	
	private long window;
	private long windowSurface;
	
	private VulkanInstance instance;
	private VulkanPhysicalDevice physicalDevice;
	private VulkanSwapchainAndQueue swapchainAndQueue;
	private VulkanLogicalDevice logicalDevice;
	private VulkanCommandPoolAndBuffer commandPoolAndBuffer;
	
	public GameMain() {
		instance = new VulkanInstance();
		physicalDevice = new VulkanPhysicalDevice();
		swapchainAndQueue = new VulkanSwapchainAndQueue();
		logicalDevice = new VulkanLogicalDevice();
		commandPoolAndBuffer = new VulkanCommandPoolAndBuffer();
	}
	
	public GameMain(String mapName) throws AssetNotFoundException, AssetFormatException {
		GameState.get().setMap(new MapSaveLoad().load(mapName, false));
	}

	public void run() throws IllegalStateException, VulkanException {
		if (instance.get() != null) {
			throw new IllegalStateException("Vulkan is already completly or partialy initialized. Use cleanUp first.");
		}
		
		try {
			init();
//			loop();
		} finally {
			cleanUp();
		}
		
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
		
		instance.init(VK_VERSION);
		physicalDevice.init(instance, VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU);
		
		//TODO: remove later
		var deviceProperties = VkPhysicalDeviceProperties.calloc();
		vkGetPhysicalDeviceProperties(physicalDevice.get(), deviceProperties);
		System.out.println("Using GPU: " + deviceProperties.deviceNameString());
		deviceProperties.free();
		
		window = createGlfwWindow();
		windowSurface = createGlfwWindowSurface();
		
		swapchainAndQueue.init(physicalDevice, windowSurface);
		logicalDevice.init(physicalDevice, swapchainAndQueue);
		commandPoolAndBuffer.init(logicalDevice, swapchainAndQueue);
		
		glfwShowWindow(window);
	}
	
	private void loop() {
		while(!glfwWindowShouldClose(window)) {
			//TODO: render loop
		}
	}
	
	public void cleanUp() {
		// Destroy bottom up
		commandPoolAndBuffer.cleanUp(logicalDevice);
		logicalDevice.cleanUp();
		swapchainAndQueue.cleanUp();
		
		//TODO: check if there is a possibility to destroy surface
		glfwDestroyWindow(window);
		
		physicalDevice.cleanUp();
		instance.cleanUp();
		
		glfwTerminate();
	}
	
	private long createGlfwWindow() {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
//		var window = glfwCreateWindow(1920, 1080, SystemConfiguration.GAME_NAME.getConfiguration(), glfwGetPrimaryMonitor(), NULL);
		var window = glfwCreateWindow(800, 600, SystemConfiguration.GAME_NAME.getConfiguration(), NULL, NULL);

		return window;
	}
	
	private long createGlfwWindowSurface() throws VulkanException {
		var pSurface = memAllocLong(1);
		var res = glfwCreateWindowSurface(instance.get(), window, null, pSurface);

		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			var surface = pSurface.get(0);
			return surface;
		} finally {
			memFree(pSurface);
		}
	}

	public static void main(String[] args) throws Exception {
		new GameMain().run();
	}
}
