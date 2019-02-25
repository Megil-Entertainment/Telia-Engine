package ch.megil.teliaengine.vulkan.command;

import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;

import ch.megil.teliaengine.vulkan.VulkanLogicalDevice;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

public class VulkanCommandBuffer {
	private VkCommandBuffer commandBuffer;

	public VulkanCommandBuffer(VkCommandBuffer commandBuffer) {
		this.commandBuffer = commandBuffer;
	}
	
	public void begin() throws VulkanException {
		var beginInfo = VkCommandBufferBeginInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
				.flags(VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT);
		
		var res = vkBeginCommandBuffer(commandBuffer, beginInfo);
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
		} finally {
			beginInfo.free();
		}
	}
	
	public void end() throws VulkanException {
		var res = vkEndCommandBuffer(commandBuffer);
		if (res != VK_SUCCESS) {
			throw new VulkanException(res);
		}
	}
	
	protected void cleanUp(VulkanLogicalDevice logicalDevice, long commandPool) {
		if (commandBuffer != null) {
			vkFreeCommandBuffers(logicalDevice.get(), commandPool, commandBuffer);
			commandBuffer = null;
		}
	}
	
	public VkCommandBuffer get() {
		return commandBuffer;
	}
}
