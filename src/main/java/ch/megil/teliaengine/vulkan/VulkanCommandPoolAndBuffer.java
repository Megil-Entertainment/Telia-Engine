package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDevice;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanCommandPoolAndBuffer {
	private long commandPool;
	private VkCommandBuffer commandBuffer;
	
	/**
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param queue An initialized {@link VulkanQueue}
	 */
	public void init(VulkanLogicalDevice logicalDevice, VulkanQueue queue) throws VulkanException {
		commandPool = createCommandPool(logicalDevice.get(), queue.getGraphicsFamily());
		commandBuffer = createCommandBuffer(logicalDevice.get(), commandPool);
	}
	
	private long createCommandPool(VkDevice device, int queueFamily) throws VulkanException {
		var commandPoolCreateInfo = VkCommandPoolCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
				.queueFamilyIndex(queueFamily)
				.flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);
		
		var pCmdPool = memAllocLong(1);
		var res = vkCreateCommandPool(device, commandPoolCreateInfo, null, pCmdPool);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			var cmdPool = pCmdPool.get(0);
			return cmdPool;
		} finally {
			memFree(pCmdPool);
			commandPoolCreateInfo.free();
		}
	}
	
	private VkCommandBuffer createCommandBuffer(VkDevice device, long commandPool) throws VulkanException {
		var cmdBufferAllocInfo = VkCommandBufferAllocateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
				.commandPool(commandPool)
				.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
				.commandBufferCount(1);
		
		var pCmdBuffer = memAllocPointer(1);
		var res = vkAllocateCommandBuffers(device, cmdBufferAllocInfo, pCmdBuffer);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			var cmdBuffer = new VkCommandBuffer(pCmdBuffer.get(0), device);
			return cmdBuffer;
		} finally {
			memFree(pCmdBuffer);
			cmdBufferAllocInfo.free();
		}
	}
	
	/**
	 * @param device {@link VulkanLogicalDevice} this has been initialized on.
	 */
	public void cleanUp(VulkanLogicalDevice device) {
		if (commandBuffer != null) {
			vkFreeCommandBuffers(device.get(), commandPool, commandBuffer);
			commandBuffer = null;
		}
		
		if (commandPool != VK_NULL_HANDLE) {
			vkDestroyCommandPool(device.get(), commandPool, null);
			commandPool = VK_NULL_HANDLE;
		}
	}
	
	public long getCommandPool() {
		return commandPool;
	}
	
	public VkCommandBuffer getCommandBuffer() {
		return commandBuffer;
	}
}
