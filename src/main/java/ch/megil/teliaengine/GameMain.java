package ch.megil.teliaengine;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.*;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.file.MapSaveLoad;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.gamelogic.GameState;
import ch.megil.teliaengine.vulkan.VkDeviceAndQueueFamily;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

public class GameMain {
	private static final int VK_VERSION = VK_MAKE_VERSION(1, 0, 2);
	
	private VkInstance instance;
	private VkPhysicalDevice physicalDevice;
	private VkDeviceAndQueueFamily deviceAndQueueFam;
	private long commandPool;
	
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
		
		deviceAndQueueFam = createLogicalDevice();
		commandPool = createCommandPool();
	}
	
	public void cleanUp() {
		// Destroy bottom up
		if (commandPool != NULL) {
			vkDestroyCommandPool(deviceAndQueueFam.getDevice(), commandPool, null);
			commandPool = NULL;
		}
		
		if (deviceAndQueueFam != null) {
			vkDestroyDevice(deviceAndQueueFam.getDevice(), null);
			deviceAndQueueFam = null;
		}
		
		physicalDevice = null;
		
		if (instance != null) {
			vkDestroyInstance(instance, null);
			instance = null;
		}
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
			deviceProperties.free();
			memFree(gpus);
			memFree(gpuCount);
		}
	}
	
	private VkDeviceAndQueueFamily createLogicalDevice() throws VulkanException {
		//TODO: extensions

		var queueFamilyCount = memAllocInt(1);
		vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, queueFamilyCount, null);

		var queueFamilyProperties = VkQueueFamilyProperties.calloc(queueFamilyCount.get(0));
		vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, queueFamilyCount, queueFamilyProperties);

		int queueFamilyIndex;
		for (queueFamilyIndex = 0; queueFamilyIndex < queueFamilyCount.get(0); queueFamilyIndex++) {
			if ((queueFamilyProperties.get(queueFamilyIndex).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
				break;
			}
		}

		var queueCount = queueFamilyProperties.get(queueFamilyIndex).queueCount();
		var queuePriorities = memAllocFloat(queueCount).put(new float[queueCount]);
		var queueCreateInfo = VkDeviceQueueCreateInfo.calloc(1)
				.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
				.pQueuePriorities(queuePriorities);
		// set queueCount explicitly to have the correct number -> lwjgl bug?
		// if not done queueCount is always zero
		VkDeviceQueueCreateInfo.nqueueCount(queueCreateInfo.get(0).address(), queueCount);

		var deviceCreateInfo = VkDeviceCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
				.pQueueCreateInfos(queueCreateInfo);
		// set queueCreateInfoCount explicitly to have the correct number -> lwjgl bug?
		// if not done queueCreateInfoCount is always zero
		VkDeviceCreateInfo.nqueueCreateInfoCount(deviceCreateInfo.address(), 1);

		var pDevice = memAllocPointer(1);
		var res = vkCreateDevice(physicalDevice, deviceCreateInfo, null, pDevice);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			var logicalDevice = new VkDevice(pDevice.get(0), physicalDevice, deviceCreateInfo);
			return new VkDeviceAndQueueFamily(logicalDevice, queueFamilyIndex);
		} finally {
			memFree(pDevice);
			deviceCreateInfo.free();
			queueCreateInfo.free();
			memFree(queuePriorities);
			queueFamilyProperties.free();
			memFree(queueFamilyCount);
		}
	}
	
	private long createCommandPool() throws VulkanException {
		var commandPoolCreateInfo = VkCommandPoolCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
				.queueFamilyIndex(deviceAndQueueFam.getQueueFamilyIndex())
				.flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);
		
		var pCmdPool = memAllocLong(1);
		var res = vkCreateCommandPool(deviceAndQueueFam.getDevice(), commandPoolCreateInfo, null, pCmdPool);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			var cmdPool = pCmdPool.get(0);
			return cmdPool;
		} finally {
			memFree(pCmdPool);
			commandPoolCreateInfo.free();
		}
	}

	public static void main(String[] args) throws Exception {
		new GameMain().run();
	}
}
