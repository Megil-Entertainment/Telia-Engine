package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanLogicalDevice {
	private VkDevice logicalDevice;
	
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param swapchainAndQueue An initialized {@link VulkanSwapchainAndQueue}
	 */
	public void init(VulkanPhysicalDevice physicalDevice, VulkanSwapchainAndQueue swapchainAndQueue) throws VulkanException {
		var queueFamilyCount = memAllocInt(1);
		vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice.get(), queueFamilyCount, null);

		var queueFamilyProperties = VkQueueFamilyProperties.calloc(queueFamilyCount.get(0));
		vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice.get(), queueFamilyCount, queueFamilyProperties);

		var queueCount = queueFamilyProperties.get(swapchainAndQueue.getGraphicsQueueFam()).queueCount();
		var queuePriorities = memAllocFloat(queueCount).put(new float[queueCount]);
		var queueCreateInfo = VkDeviceQueueCreateInfo.calloc(1)
				.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
				.pQueuePriorities(queuePriorities);
		// set queueCount explicitly to have the correct number -> lwjgl bug?
		// if not done queueCount is always zero
		VkDeviceQueueCreateInfo.nqueueCount(queueCreateInfo.get(0).address(), queueCount);
		
		var vkKhrSwapchainExtension = memUTF8(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
		var enabledExtensionNames = memAllocPointer(1)
				.put(vkKhrSwapchainExtension).flip();

		var deviceCreateInfo = VkDeviceCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
				.pQueueCreateInfos(queueCreateInfo)
				.ppEnabledExtensionNames(enabledExtensionNames);
		// set queueCreateInfoCount explicitly to have the correct number -> lwjgl bug?
		// if not done queueCreateInfoCount is always zero
		VkDeviceCreateInfo.nqueueCreateInfoCount(deviceCreateInfo.address(), 1);

		var pDevice = memAllocPointer(1);
		var res = vkCreateDevice(physicalDevice.get(), deviceCreateInfo, null, pDevice);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			logicalDevice = new VkDevice(pDevice.get(0), physicalDevice.get(), deviceCreateInfo);
		} finally {
			memFree(pDevice);
			deviceCreateInfo.free();
			memFree(enabledExtensionNames);
			memFree(vkKhrSwapchainExtension);
			queueCreateInfo.free();
			memFree(queuePriorities);
			queueFamilyProperties.free();
			memFree(queueFamilyCount);
		}
	}
	
	public void cleanUp() {
		if (logicalDevice != null) {
			vkDestroyDevice(logicalDevice, null);
			logicalDevice = null;
		}
	}
	
	public VkDevice get() {
		return logicalDevice;
	}
}
