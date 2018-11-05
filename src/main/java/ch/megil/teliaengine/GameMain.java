package ch.megil.teliaengine;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.file.MapSaveLoad;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.gamelogic.GameState;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

public class GameMain {
	private static final int VK_VERSION = VK_MAKE_VERSION(1, 0, 2);
	
	private VkInstance instance;
	private VkPhysicalDevice physicalDevice;
	
	public GameMain() {}
	
	public GameMain(String mapName) throws AssetNotFoundException, AssetFormatException {
		GameState.get().setMap(new MapSaveLoad().load(mapName, false));
	}

	public void run() throws IllegalStateException, VulkanException {
		if (instance != null) {
			throw new IllegalStateException("Vulkan is already initialized.");
		}
		
		init();
		
		cleanUp();
		
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
		physicalDevice = getPhysicalDevice();
		
		//TODO: remove later
		var deviceProperties = VkPhysicalDeviceProperties.calloc();
		vkGetPhysicalDeviceProperties(physicalDevice, deviceProperties);
		System.out.println("Using GPU: " + deviceProperties.deviceNameString());
		deviceProperties.free();
		
		
	}
	
	public void cleanUp() {
		if (instance == null) {return;};
		vkDestroyInstance(instance, null);
		instance = null;
		physicalDevice = null;
	}
	
	private VkInstance createInstance() throws VulkanException {
		//TODO: extensions
		
		var appInfo = VkApplicationInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
				.pApplicationName(memUTF8(SystemConfiguration.GAME_NAME.getConfiguration()))
				.pEngineName(memUTF8(SystemConfiguration.APP_NAME.getConfiguration()))
				.apiVersion(VK_VERSION);
		
		var instInfo = VkInstanceCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
				//TODO: delete: .pNext(NULL)
				.pApplicationInfo(appInfo);
		
		var pInst = memAllocPointer(1);
		var res = vkCreateInstance(instInfo, null, pInst);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			var instance = new VkInstance(pInst.get(0), instInfo);
			return instance;
		} finally {
			memFree(pInst);
			instInfo.free();
			memFree(appInfo.pApplicationName());
			memFree(appInfo.pEngineName());
			appInfo.free();
		}
	}
	
	private VkPhysicalDevice getPhysicalDevice() throws VulkanException {
		var gpuCount = memAllocInt(1);
		var res = vkEnumeratePhysicalDevices(instance, gpuCount, null);
		
		if (res != VK_SUCCESS) {
			memFree(gpuCount);
			throw new VulkanException(res);
		}
		
		var gpus = memAllocPointer(gpuCount.get(0));
		res = vkEnumeratePhysicalDevices(instance, gpuCount, gpus);
		
		var deviceProperties = VkPhysicalDeviceProperties.calloc();
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			// get first, if not discrete gpu check if there is one discrete, otherwise use first device
			var physicalDevice = new VkPhysicalDevice(gpus.get(0), instance);
			
			vkGetPhysicalDeviceProperties(physicalDevice, deviceProperties);
			
			if (deviceProperties.deviceType() != VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU) {
				for (var i = 1; i < gpuCount.get(0); i++) {
					var tempDevice = new VkPhysicalDevice(gpus.get(i), instance);
					vkGetPhysicalDeviceProperties(tempDevice, deviceProperties);
					
					if (deviceProperties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU) {
						physicalDevice = tempDevice;
						break;
					}
				}
			}
			
			return physicalDevice;
		} finally {
			memFree(gpuCount);
			memFree(gpus);
			deviceProperties.free();
		}
	}
	
	private void createLogicalDevice() {
		
	}

	public static void main(String[] args) throws Exception {
		new GameMain().run();
	}
}
