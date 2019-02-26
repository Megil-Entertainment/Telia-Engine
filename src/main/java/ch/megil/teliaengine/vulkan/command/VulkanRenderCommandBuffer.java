package ch.megil.teliaengine.vulkan.command;

import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT;

import org.lwjgl.vulkan.VkCommandBuffer;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

public class VulkanRenderCommandBuffer extends VulkanCommandBuffer {
	public VulkanRenderCommandBuffer(VkCommandBuffer commandBuffer) {
		super(commandBuffer);
	}

	public void begin() throws VulkanException {
		super.begin(VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT);
	}
}
