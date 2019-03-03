package ch.megil.teliaengine.vulkan.command;

import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;

import org.lwjgl.vulkan.VkCommandBuffer;

import ch.megil.teliaengine.vulkan.VulkanLogicalDevice;
import ch.megil.teliaengine.vulkan.VulkanQueue;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

public class VulkanSingleCommandPool extends VulkanCommandPool {
	private static final int SINGLE_BUFFER = 1;
	
	public void init(VulkanLogicalDevice logicalDevice, VulkanQueue queue) throws VulkanException {
		super.init(logicalDevice, queue);
	}
	
	@Override
	protected VulkanCommandBuffer createBuffer(VkCommandBuffer buffer) {
		return new VulkanSingleCommandBuffer(buffer);
	}
	
	public VulkanSingleCommandBuffer getCommandBuffer(VulkanLogicalDevice logicalDevice) throws VulkanException {
		var pCmdBuffer = memAllocPointer(SINGLE_BUFFER);
		
		try {
			initBuffers(logicalDevice, SINGLE_BUFFER, pCmdBuffer);
			var cmdBuffer = new VkCommandBuffer(pCmdBuffer.get(0), logicalDevice.get());
			var buffer = new VulkanSingleCommandBuffer(cmdBuffer);
			commandBuffers.add(buffer);
			return buffer;
		} finally {
			memFree(pCmdBuffer);
		}
	}
}
