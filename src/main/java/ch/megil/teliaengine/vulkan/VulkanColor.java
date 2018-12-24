package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanColor {
	private int colorFormat;
	private int colorSpace;
	
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param surface A window surface. See {@link GLFWVulkan#glfwCreateWindowSurface}
	 * @param prefColorFormat Prefered color format. See: {@link VK10#VK_FORMAT_B8G8R8A8_UNORM}.
	 */
	public void init(VulkanPhysicalDevice physicalDevice, long surface, int prefColorFormat) throws VulkanException {
		var pFormatCount = memAllocInt(1);
		
		var res = vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice.get(), surface, pFormatCount, null);
		
		if (res != VK_SUCCESS) {
			memFree(pFormatCount);
			throw new VulkanException(res);
		}
		var formatCount = pFormatCount.get(0);
		
		var formats = VkSurfaceFormatKHR.calloc(formatCount);
		res = vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice.get(), surface, pFormatCount, formats);
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			colorFormat = formats.get(0).format();
			colorSpace = formats.get(0).colorSpace();
			if (colorFormat != prefColorFormat) {
				if (formatCount == 1 && colorFormat == VK_FORMAT_UNDEFINED) {
					colorFormat = prefColorFormat;
				} else {
					for (int i = 1; i < formatCount; i++) {
						if (formats.get(i).format() == prefColorFormat) {
							colorFormat = prefColorFormat;
							colorSpace = formats.get(i).colorSpace();
						}
					}
				}
			}
		} finally {
			formats.free();
			memFree(pFormatCount);
		}
	}
	
	public void cleanUp() {
		colorFormat = 0;
		colorSpace = 0;
	}
	
	public int getFormat() {
		return colorFormat;
	}
	
	public int getSpace() {
		return colorSpace;
	}
}
