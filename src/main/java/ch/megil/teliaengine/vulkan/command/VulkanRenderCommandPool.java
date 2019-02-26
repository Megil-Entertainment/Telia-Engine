package ch.megil.teliaengine.vulkan.command;

import org.lwjgl.vulkan.VkCommandBuffer;

import ch.megil.teliaengine.vulkan.VulkanLogicalDevice;
import ch.megil.teliaengine.vulkan.VulkanQueue;
import ch.megil.teliaengine.vulkan.VulkanSwapchain;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

public class VulkanRenderCommandPool extends VulkanCommandPool {
	public void init(VulkanLogicalDevice logicalDevice, VulkanQueue queue, VulkanSwapchain swapchain) throws VulkanException {
		init(logicalDevice, queue, swapchain.getImageCount());
	}
	
	@Override
	protected VulkanCommandBuffer createBuffer(VkCommandBuffer buffer) {
		return new VulkanRenderCommandBuffer(buffer);
	}
}
