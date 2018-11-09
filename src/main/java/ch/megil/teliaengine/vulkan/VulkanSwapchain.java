package ch.megil.teliaengine.vulkan;

import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR;

import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanSwapchain {
	
	/**
	 * @param surface A window surface. See {@link GLFWVulkan#glfwCreateWindowSurface}
	 */
	public void init(long surface) {
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
}
