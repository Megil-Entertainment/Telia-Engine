package ch.megil.teliaengine.vulkan.command;

import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO;
import static org.lwjgl.vulkan.VK10.vkQueueSubmit;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;

import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkSubmitInfo;

import ch.megil.teliaengine.vulkan.VulkanQueue;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

public class VulkanSingleCommandBuffer extends VulkanCommandBuffer {
	public VulkanSingleCommandBuffer(VkCommandBuffer commandBuffer) {
		super(commandBuffer);
	}

	@Override
	public void begin() throws VulkanException {
		super.begin(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
	}
	
	/**
	 * Ends command buffer and submits it.
	 */
	public void end(VulkanQueue queue) throws VulkanException {
		super.end();
		
		var cmdBuffer = memAllocPointer(1).put(get());
		var submitInfo = VkSubmitInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
				.pCommandBuffers(cmdBuffer);
		VkSubmitInfo.ncommandBufferCount(submitInfo.address(), cmdBuffer.capacity());
		
		try {
			vkQueueSubmit(queue.getGraphicsQueue(), submitInfo, VK_NULL_HANDLE);
			vkQueueWaitIdle(queue.getGraphicsQueue());
		} finally {
			submitInfo.free();
			memFree(cmdBuffer);
		}
	}
}
