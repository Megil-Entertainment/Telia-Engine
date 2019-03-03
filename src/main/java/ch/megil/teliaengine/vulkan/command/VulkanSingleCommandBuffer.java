package ch.megil.teliaengine.vulkan.command;

import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;

import org.lwjgl.vulkan.VkCommandBuffer;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

public class VulkanSingleCommandBuffer extends VulkanCommandBuffer {
	public VulkanSingleCommandBuffer(VkCommandBuffer commandBuffer) {
		super(commandBuffer);
	}

	@Override
	public void begin() throws VulkanException {
		super.begin(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
	}
}
