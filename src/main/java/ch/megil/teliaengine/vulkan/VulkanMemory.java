package ch.megil.teliaengine.vulkan;

import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkAllocateMemory;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceMemoryProperties;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

public abstract class VulkanMemory {
	private static final int BASE_MASK = 1;
	private static final int FLAG_NOT_SET = 0;
	
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param memoryRequirements requirements the memory needs to respect
	 * @param properties of the buffer (see {@link VK10#VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT})
	 * @param memoryPointer new pointer, will point to where the memory will have been allocated
	 */
	protected void allocateMemory(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice, VkMemoryRequirements memoryRequirements, int properties, LongBuffer memoryPointer) throws VulkanException {
		var memoryType = findMemoryType(physicalDevice.get(), memoryRequirements.memoryTypeBits(), properties);
		
		var memoryAllocInfo = VkMemoryAllocateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
				.allocationSize(memoryRequirements.size())
				.memoryTypeIndex(memoryType);
		
		var res = vkAllocateMemory(logicalDevice.get(), memoryAllocInfo, null, memoryPointer);
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
		} finally {
			
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
}
