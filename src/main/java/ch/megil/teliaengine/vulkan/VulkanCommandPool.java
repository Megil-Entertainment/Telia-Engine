package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanCommandPool {
	private long commandPool;
	private List<VulkanCommandBuffer> commandBuffers;
	
	public VulkanCommandPool() {
		commandBuffers = new ArrayList<>();
	}
	
	/**
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param queue An initialized {@link VulkanQueue}
	 */
	public void init(VulkanLogicalDevice logicalDevice, VulkanQueue queue) throws VulkanException {
		var commandPoolCreateInfo = VkCommandPoolCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
				.queueFamilyIndex(queue.getGraphicsFamily())
				.flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);
		
		var pCmdPool = memAllocLong(1);
		var res = vkCreateCommandPool(logicalDevice.get(), commandPoolCreateInfo, null, pCmdPool);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			commandPool = pCmdPool.get(0);
		} finally {
			memFree(pCmdPool);
			commandPoolCreateInfo.free();
		}
	}
	
	/**
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param queue An initialized {@link VulkanQueue}
	 * @param numOfBuffers number of buffers ready at initializing level
	 */
	public void init(VulkanLogicalDevice logicalDevice, VulkanQueue queue, int numOfBuffers) throws VulkanException {
		init(logicalDevice, queue);
		initBuffers(logicalDevice, numOfBuffers);
	}
	
	public void initBuffers(VulkanLogicalDevice logicalDevice, int numOfBuffers) throws VulkanException {
		var cmdBufferAllocInfo = VkCommandBufferAllocateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
				.commandPool(commandPool)
				.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
				.commandBufferCount(numOfBuffers);
		
		var pCmdBuffer = memAllocPointer(numOfBuffers);
		var res = vkAllocateCommandBuffers(logicalDevice.get(), cmdBufferAllocInfo, pCmdBuffer);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			for (int i = 0; i < numOfBuffers; i++) {
				var cmdBuffer = new VkCommandBuffer(pCmdBuffer.get(i), logicalDevice.get());
				commandBuffers.add(new VulkanCommandBuffer(cmdBuffer));
			}
		} finally {
			memFree(pCmdBuffer);
			cmdBufferAllocInfo.free();
		}
	}
	
	public void removeBuffer(VulkanLogicalDevice logicalDevice, VulkanCommandBuffer... buffers) {
		for (var b : buffers) {
			b.cleanUp(logicalDevice, commandPool);
			commandBuffers.remove(b);
		}
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		commandBuffers.forEach(b -> b.cleanUp(logicalDevice, commandPool));
		commandBuffers.clear();
		
		if (commandPool != VK_NULL_HANDLE) {
			vkDestroyCommandPool(logicalDevice.get(), commandPool, null);
			commandPool = VK_NULL_HANDLE;
		}
	}
	
	public long get() {
		return commandPool;
	}
	
	public VulkanCommandBuffer getCommandBuffer(int index) {
		return commandBuffers.get(index);
	}
}
