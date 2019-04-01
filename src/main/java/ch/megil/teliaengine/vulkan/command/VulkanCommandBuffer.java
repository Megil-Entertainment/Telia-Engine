package ch.megil.teliaengine.vulkan.command;

import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkSubmitInfo;

import ch.megil.teliaengine.vulkan.VulkanLogicalDevice;
import ch.megil.teliaengine.vulkan.VulkanQueue;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

public class VulkanCommandBuffer {
	private VkCommandBuffer commandBuffer;
	private int flags;
	private long commandPool;

	public VulkanCommandBuffer(VkCommandBuffer commandBuffer, int flags, long commandPool) {
		this.commandBuffer = commandBuffer;
		this.flags = flags;
		this.commandPool = commandPool;
	}
	
	public void begin() throws VulkanException {
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
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		if (commandBuffer != null) {
			vkFreeCommandBuffers(logicalDevice.get(), commandPool, commandBuffer);
			commandBuffer = null;
		}
	}
	
	/**
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param queue An initialized {@link VulkanQueue} to submit the commands to
	 * Ends the command buffer, submits it and cleans its memory up.
	 */
	public void submit(VulkanLogicalDevice logicalDevice, VulkanQueue queue) throws VulkanException {
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
			
			cleanUp(logicalDevice);
		} finally {
			submitInfo.free();
			memFree(pCmdBuffer);
		}
	}
	
	public VkCommandBuffer get() {
		return commandBuffer;
	}
}
