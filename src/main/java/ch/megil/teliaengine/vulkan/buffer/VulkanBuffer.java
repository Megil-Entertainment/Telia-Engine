package ch.megil.teliaengine.vulkan.buffer;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memCopy;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.*;

import ch.megil.teliaengine.vulkan.VulkanLogicalDevice;
import ch.megil.teliaengine.vulkan.VulkanMemory;
import ch.megil.teliaengine.vulkan.VulkanPhysicalDevice;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class is a parent class for different buffers and
 * needs setup first with {@link #init} and needs to be cleaned
 * up before destruction with {@link #cleanUp}.
 */
public abstract class VulkanBuffer extends VulkanMemory {
	private static final long MEMORY_OFFSET = 0;
	private static final long MAP_OFFSET = 0;
	private static final int NO_FLAGS = 0;
	
	protected long buffer;
	protected long bufferSize;
	protected long memory;
	
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param size bytesize of the buffer
	 * @param usage of the buffer (see {@link VK10#VK_BUFFER_USAGE_INDEX_BUFFER_BIT})
	 * @param sharingMode of the buffer (see {@link VK10#VK_SHARING_MODE_EXCLUSIVE})
	 * @param properties of the buffer (see {@link VK10#VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT})
	 */
	protected void init(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice, long size, int usage, int sharingMode, int properties) throws VulkanException {
		var bufferInfo = VkBufferCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
				.size(size)
				.usage(usage)
				.sharingMode(sharingMode);
		
        var memoryRequirements = VkMemoryRequirements.calloc();
		
		var pBuffer = memAllocLong(1);
		var pMemory = memAllocLong(1);
		var res = vkCreateBuffer(logicalDevice.get(), bufferInfo, null, pBuffer);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			buffer = pBuffer.get(0);
			
			vkGetBufferMemoryRequirements(logicalDevice.get(), buffer, memoryRequirements);
			bufferSize = memoryRequirements.size();
			allocateMemory(physicalDevice, logicalDevice, memoryRequirements, properties, pMemory);
			memory = pMemory.get(0);
			
			res = vkBindBufferMemory(logicalDevice.get(), buffer, memory, MEMORY_OFFSET);
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
		} finally {
			memFree(pMemory);
			memFree(pBuffer);
			memoryRequirements.free();
			bufferInfo.free();
		}
	}
	
	protected void write(VulkanLogicalDevice logicalDevice, long address, long size) throws VulkanException {
		write(logicalDevice, address, size, MAP_OFFSET);
	}
	
	protected void write(VulkanLogicalDevice logicalDevice, long address, long size, long offset) throws VulkanException {
		var pData = memAllocPointer(1);
		var res = vkMapMemory(logicalDevice.get(), memory, offset, bufferSize, NO_FLAGS, pData);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			memCopy(address, pData.get(), size);
			vkUnmapMemory(logicalDevice.get(), memory);
			
			res = vkBindBufferMemory(logicalDevice.get(), buffer, memory, MEMORY_OFFSET);
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
		} finally {
			memFree(pData);
		}
	}
	
	protected void cleanUp(VulkanLogicalDevice logicalDevice) {
		if (buffer != VK_NULL_HANDLE) {
			vkDestroyBuffer(logicalDevice.get(), buffer, null);
			buffer = VK_NULL_HANDLE;
			bufferSize = 0;
		}
		
		if (memory != VK_NULL_HANDLE) {
			vkFreeMemory(logicalDevice.get(), memory, null);
			memory = VK_NULL_HANDLE;
		}
	}
	
	public long get() {
		return buffer;
	}
}
