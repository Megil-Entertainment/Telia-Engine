package ch.megil.teliaengine.vulkan;

import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateInstance;
import static org.lwjgl.vulkan.VK10.vkDestroyInstance;

import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanInstance {
	private VkInstance instance;
	
	/**
	 * @param vulkanVersion Version the instance should use. See: {@link VK10#VK_MAKE_VERSION}
	 */
	public void init(int vulkanVersion) throws VulkanException {
		var requiredExtensions = glfwGetRequiredInstanceExtensions();
        if (requiredExtensions == null) {
            throw new VulkanException("Failed to find list of required extensions");
        }
        
		var appInfo = VkApplicationInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
				.pApplicationName(memUTF8(SystemConfiguration.GAME_NAME.getConfiguration()))
				.pEngineName(memUTF8(SystemConfiguration.APP_NAME.getConfiguration()))
				.apiVersion(vulkanVersion);
		
		var enabledExtensionNames = memAllocPointer(requiredExtensions.capacity())
				.put(requiredExtensions).flip();
		
		var instInfo = VkInstanceCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
				.pApplicationInfo(appInfo)
				.ppEnabledExtensionNames(enabledExtensionNames);
		
		var pInst = memAllocPointer(1);
		var res = vkCreateInstance(instInfo, null, pInst);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			instance = new VkInstance(pInst.get(0), instInfo);
		} finally {
			memFree(pInst);
			instInfo.free();
			memFree(enabledExtensionNames);
			memFree(appInfo.pApplicationName());
			memFree(appInfo.pEngineName());
			appInfo.free();
		}
	}
	
	public void cleanUp() {
		if (instance != null) {
			vkDestroyInstance(instance, null);
			instance = null;
		}
	}
	
	public VkInstance get() {
		return instance;
	}
}
