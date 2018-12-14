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
import org.lwjgl.vulkan.VkDevice;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanCommandPool {
	private long commandPool;
	private List<VulkanCommandBuffer> commandBuffers;
//	private VkCommandBuffer commandBuffer;
	
	/**
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param queue An initialized {@link VulkanQueue}
	 */
	public void init(VulkanLogicalDevice logicalDevice, VulkanQueue queue) throws VulkanException {
		commandPool = createCommandPool(logicalDevice.get(), queue.getGraphicsFamily());
		commandBuffers = new ArrayList<>();
//		commandBuffer = createCommandBuffer(logicalDevice.get(), commandPool);
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
	
//	private VkCommandBuffer createCommandBuffer(VkDevice device, long commandPool) throws VulkanException {
//		var cmdBufferAllocInfo = VkCommandBufferAllocateInfo.calloc()
//				.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
//				.commandPool(commandPool)
//				.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
//				.commandBufferCount(1);
//		
//		var pCmdBuffer = memAllocPointer(1);
//		var res = vkAllocateCommandBuffers(device, cmdBufferAllocInfo, pCmdBuffer);
//		
//		try {
//			if (res != VK_SUCCESS) {
//				throw new VulkanException(res);
//			}
//			
//			var cmdBuffer = new VkCommandBuffer(pCmdBuffer.get(0), device);
//			return cmdBuffer;
//		} finally {
//			memFree(pCmdBuffer);
//			cmdBufferAllocInfo.free();
//		}
//	}
	
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
	
//	public void beginBuffer() throws VulkanException {
//		var beginInfo = VkCommandBufferBeginInfo.calloc()
//				.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
//				.flags(VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT);
//		
//		var res = vkBeginCommandBuffer(commandBuffer, beginInfo);
//		try {
//			if (res != VK_SUCCESS) {
//				throw new VulkanException(res);
//			}
//		} finally {
//			beginInfo.free();
//		}
//	}
//	
//	public void endBuffer() throws VulkanException {
//		var res = vkEndCommandBuffer(commandBuffer);
//		if (res != VK_SUCCESS) {
//			throw new VulkanException(res);
//		}
//	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
//		if (commandBuffer != null) {
//			vkFreeCommandBuffers(device.get(), commandPool, commandBuffer);
//			commandBuffer = null;
//		}
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
	
//	public VkCommandBuffer getBuffer() {
//		return commandBuffer;
//	}
}
