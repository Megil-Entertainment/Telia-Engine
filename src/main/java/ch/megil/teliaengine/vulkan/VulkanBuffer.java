package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memCopy;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class is a parent class for different buffers and
 * needs setup first with {@link #init} and needs to be cleaned
 * up before destruction with {@link #cleanUp}.
 */
public abstract class VulkanBuffer {
	private static final long MEMORY_OFFSET = 0;
	private static final int BASE_MASK = 1;
	private static final int FLAG_NOT_SET = 0;
	private static final long MAP_OFFSET = 0;
	private static final int NO_FLAGS = 0;
	
	protected long buffer;
	protected long bufferSize;
	protected long memory;
	
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param size bytesize of the buffer
	 */
	protected void init(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice, long size) throws VulkanException {
		var bufferInfo = VkBufferCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
				.size(size)
				.usage(VK_BUFFER_USAGE_INDEX_BUFFER_BIT)
				.sharingMode(VK_SHARING_MODE_EXCLUSIVE);
		
        var memoryRequirements = VkMemoryRequirements.calloc();
		var memoryAllocInfo = VkMemoryAllocateInfo.calloc();
		
		var pBuffer = memAllocLong(1);
		var pMemory = memAllocLong(1);
		var res = vkCreateBuffer(logicalDevice.get(), bufferInfo, null, pBuffer);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			buffer = pBuffer.get(0);
			
			vkGetBufferMemoryRequirements(logicalDevice.get(), buffer, memoryRequirements);
			
			var properties = VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT;
			var memoryType = findMemoryType(physicalDevice.get(), memoryRequirements.memoryTypeBits(), properties);
			
			bufferSize = memoryRequirements.size();
			memoryAllocInfo.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
					.allocationSize(bufferSize)
					.memoryTypeIndex(memoryType);
			
			res = vkAllocateMemory(logicalDevice.get(), memoryAllocInfo, null, pMemory);
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			memory = pMemory.get(0);
			
			res = vkBindBufferMemory(logicalDevice.get(), buffer, memory, MEMORY_OFFSET);
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
		} finally {
			memFree(pMemory);
			memFree(pBuffer);
			memoryAllocInfo.free();
			memoryRequirements.free();
			bufferInfo.free();
		}
	}
	
	private int findMemoryType(VkPhysicalDevice physicalDevice, int typeFilter, int properties) throws VulkanException {
		var memoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
		vkGetPhysicalDeviceMemoryProperties(physicalDevice, memoryProperties);
		
		try {
			for (var i = 0; i < memoryProperties.memoryTypeCount(); i++) {
				if ((typeFilter & (BASE_MASK << i)) != FLAG_NOT_SET &&
						(memoryProperties.memoryTypes(i).propertyFlags() & properties) == properties) {
					return i;
				}
			}
	
			throw new VulkanException("No compatible memory found.");
		} finally {
			memoryProperties.free();
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
