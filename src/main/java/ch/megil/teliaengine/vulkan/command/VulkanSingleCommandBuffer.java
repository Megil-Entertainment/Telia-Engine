package ch.megil.teliaengine.vulkan.command;

import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

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
	 * @param queue An initialized {@link VulkanQueue} to submit the commands to
	 * Ends the command buffer and submits it.
	 */
	public void submit(VulkanQueue queue) throws VulkanException {
		this.end();
		
		var pCmdBuffer = memAllocPointer(1);
		pCmdBuffer.put(0, get());
		var submitInfo = VkSubmitInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
				.pCommandBuffers(pCmdBuffer);
		VkSubmitInfo.ncommandBufferCount(submitInfo.address(), pCmdBuffer.capacity());
		
		try {
			var res = vkQueueSubmit(queue.getGraphicsQueue(), submitInfo, VK_NULL_HANDLE);
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			vkQueueWaitIdle(queue.getGraphicsQueue());
		} finally {
			submitInfo.free();
			memFree(pCmdBuffer);
		}
	}
}
