package ch.megil.teliaengine.vulkan.command;

import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;

import ch.megil.teliaengine.vulkan.VulkanLogicalDevice;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

public abstract class VulkanCommandBuffer {
	private VkCommandBuffer commandBuffer;

	public VulkanCommandBuffer(VkCommandBuffer commandBuffer) {
		this.commandBuffer = commandBuffer;
	}
	
	public abstract void begin() throws VulkanException;
	
	protected void begin(int flags) throws VulkanException {
		var beginInfo = VkCommandBufferBeginInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
				.flags(flags);
		
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
