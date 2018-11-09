package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_GRAPHICS_BIT;
import static org.lwjgl.vulkan.VK10.VK_TRUE;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceQueueFamilyProperties;

import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanSwapchainAndQueue {
	private int graphicsQueueFamInd;
	private int presentQueueFamInd;
	
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param surface A window surface. See {@link GLFWVulkan#glfwCreateWindowSurface}
	 */
	public void init(VulkanPhysicalDevice physicalDevice, long surface) throws VulkanException {
		var pQueueFamilyCount = memAllocInt(1);
		vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice.get(), pQueueFamilyCount, null);
		var queueFamilyCount = pQueueFamilyCount.get(0);

		var queueFamilyProperties = VkQueueFamilyProperties.calloc(queueFamilyCount);
		vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice.get(), pQueueFamilyCount, queueFamilyProperties);
		
		var supportsPresent = memAllocInt(1);
		
		graphicsQueueFamInd = Integer.MAX_VALUE;
		presentQueueFamInd = Integer.MAX_VALUE;
		
		for (int i = 0; i < queueFamilyCount; i++) {
			vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice.get(), i, surface, supportsPresent);
			
			if ((queueFamilyProperties.get(i).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
				if (supportsPresent.get(0) == VK_TRUE) {
					graphicsQueueFamInd = i;
					presentQueueFamInd = i;
					break;
				}
				if (graphicsQueueFamInd == Integer.MAX_VALUE) {
					graphicsQueueFamInd = i;
				}
			} else if (supportsPresent.get(0) == VK_TRUE && presentQueueFamInd == Integer.MAX_VALUE) {
				presentQueueFamInd = i;
			}
		}
		
		try {
			if (graphicsQueueFamInd == Integer.MAX_VALUE || presentQueueFamInd == Integer.MAX_VALUE) {
				throw new VulkanException("No graphics or presentation queue found.");
			}
		} finally {
			memFree(supportsPresent);
			queueFamilyProperties.free();
			memFree(pQueueFamilyCount);
		}
		
		var swapchainCreateInfo = VkSwapchainCreateInfoKHR.calloc()
				.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
				.surface(surface);
		
		try {
			//TODO: finish swapchain
		} finally {
			swapchainCreateInfo.free();
		}
	}
	
	public void cleanUp() {
		//TODO: cleanup
	}
	
	public int getGraphicsQueueFam() {
		return graphicsQueueFamInd;
	}
	
	public int getPresentQueueFam() {
		return presentQueueFamInd;
	}
}
