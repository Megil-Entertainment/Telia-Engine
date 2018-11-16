package ch.megil.teliaengine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_MAKE_VERSION;
import static org.lwjgl.vulkan.VK10.VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

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
	private VulkanQueue queue;
	private VulkanLogicalDevice logicalDevice;
	private VulkanColor color;
	private VulkanSwapchain swapchain;
	private VulkanCommandPoolAndBuffer commandPoolAndBuffer;
	
	public GameMain() {
		instance = new VulkanInstance();
		physicalDevice = new VulkanPhysicalDevice();
		queue = new VulkanQueue();
		logicalDevice = new VulkanLogicalDevice();
		color = new VulkanColor();
		swapchain = new VulkanSwapchain();
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
			loop();
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
		
		System.out.println("Using GPU: " + physicalDevice.getProperties().deviceNameString());
		
		window = createGlfwWindow();
		windowSurface = createGlfwWindowSurface();
		
		queue.init(physicalDevice, windowSurface);
		logicalDevice.init(physicalDevice, queue);
		color.init(physicalDevice, windowSurface, VK_FORMAT_B8G8R8A8_UNORM);
		swapchain.init(physicalDevice, windowSurface, queue, logicalDevice, color);
		
		commandPoolAndBuffer.init(logicalDevice, queue);
		
		glfwShowWindow(window);
	}
	
	private void loop() {
		while(!glfwWindowShouldClose(window)) {
			glfwPollEvents();
			//TODO: render loop
		}
	}
	
	public void cleanUp() {
		// Destroy bottom up
		commandPoolAndBuffer.cleanUp(logicalDevice);
		
		swapchain.cleanUp(logicalDevice);
		color.cleanUp();
		logicalDevice.cleanUp();
		queue.cleanUp();
		
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
		//TODO fullscreen?
//		var window = glfwCreateWindow(1920, 1080, SystemConfiguration.GAME_NAME.getConfiguration(), glfwGetPrimaryMonitor(), NULL);
		var window = glfwCreateWindow(1280, 720, SystemConfiguration.GAME_NAME.getConfiguration(), NULL, NULL);

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
