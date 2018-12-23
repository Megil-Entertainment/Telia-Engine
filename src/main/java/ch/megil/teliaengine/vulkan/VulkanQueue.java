package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_GRAPHICS_BIT;
import static org.lwjgl.vulkan.VK10.VK_TRUE;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceQueueFamilyProperties;

import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanQueue {
	private int graphicsQueueFamInd;
	private int graphicsQueueCount;
	private VkQueue graphicsQueue;
	private int presentQueueFamInd;
	private int presentQueueCount;
	private VkQueue presentQueue;
	
	public VulkanQueue() {
		graphicsQueueFamInd = Integer.MAX_VALUE;
		graphicsQueueCount = 0;
		presentQueueFamInd = Integer.MAX_VALUE;
		presentQueueCount = 0;
	}
	
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
			
			graphicsQueueCount = queueFamilyProperties.get(graphicsQueueFamInd).queueCount();
			presentQueueCount = queueFamilyProperties.get(presentQueueFamInd).queueCount();
		} finally {
			memFree(supportsPresent);
			queueFamilyProperties.free();
			memFree(pQueueFamilyCount);
		}
	}
	
	public void cleanUp() {
		graphicsQueueFamInd = Integer.MAX_VALUE;
		graphicsQueueCount = 0;
		graphicsQueue = null;
		presentQueueFamInd = Integer.MAX_VALUE;
		presentQueueCount = 0;
		presentQueue = null;
	}
	
	public int getGraphicsFamily() {
		return graphicsQueueFamInd;
	}
	
	public int getGraphicsQueueCount() {
		return graphicsQueueCount;
	}
	
	public VkQueue getGraphicsQueue() {
		return graphicsQueue;
	}
	
	protected void setGraphicsQueue(VkQueue graphicsQueue) {
		this.graphicsQueue = graphicsQueue;
	}
	
	public int getPresentFamily() {
		return presentQueueFamInd;
	}
	
	public int getPresentQueueCount() {
		return presentQueueCount;
	}
	
	public VkQueue getPresentQueue() {
		return presentQueue;
	}
	
	protected void setPresentQueue(VkQueue presentQueue) {
		this.presentQueue = presentQueue;
	}
}
