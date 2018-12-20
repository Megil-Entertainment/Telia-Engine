package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memCopy;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.*;

import ch.megil.teliaengine.vulkan.exception.VulkanException;
import ch.megil.teliaengine.vulkan.obj.VulkanPolygon;

public class VulkanIndexBuffer {
	public static final int INDEX_SIZE = 2;
	public static final int MAX_INDEX = 7;
	
	private long buffer;
	private long bufferSize;
	private long memory;
	
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 */
	public void init(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice) throws VulkanException {
		var bufferInfo = VkBufferCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
				.size(INDEX_SIZE*MAX_INDEX)
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
			
			res = vkBindBufferMemory(logicalDevice.get(), buffer, memory, 0);
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
				if ((typeFilter & (1 << i)) != 0 &&
						(memoryProperties.memoryTypes(i).propertyFlags() & properties) == properties) {
					return i;
				}
			}
	
			throw new VulkanException("No compatible memory found.");
		} finally {
			memoryProperties.free();
		}
	}
	
	public void writeVertecies(VulkanLogicalDevice logicalDevice, VulkanPolygon polygon) throws VulkanException {
        var pData = memAllocPointer(1);
		var res = vkMapMemory(logicalDevice.get(), memory, 0, bufferSize, 0, pData);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			memCopy(polygon.indexBuff(), pData.get(), polygon.indexSize());
			vkUnmapMemory(logicalDevice.get(), memory);
			
			res = vkBindBufferMemory(logicalDevice.get(), buffer, memory, 0);
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
		} finally {
			memFree(pData);
		}
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		if (buffer != VK_NULL_HANDLE) {
			vkDestroyBuffer(logicalDevice.get(), buffer, null);
			buffer = VK_NULL_HANDLE;
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
