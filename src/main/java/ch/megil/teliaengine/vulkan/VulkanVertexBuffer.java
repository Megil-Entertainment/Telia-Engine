package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memCopy;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.*;

import ch.megil.teliaengine.vulkan.exception.VulkanException;
import ch.megil.teliaengine.vulkan.obj.VulkanPolygon;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 * 
 * Exception to this rule are {@link #callocBinding()} and {@link #callocAttribute()}.
 */
public class VulkanVertexBuffer {
	private static final int VALUE_SIZE = 4;
	public static final int VERTEX_SIZE = (2+3)*VALUE_SIZE; //2 cords + 3 colors
	public static final int MAX_VERTECIES = 4;
	private static final int COLOR_OFFSET = 2*VALUE_SIZE;
	
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
				.size(VERTEX_SIZE*MAX_VERTECIES)
				.usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT)
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
			memCopy(polygon.getAddress(), pData.get(), polygon.getSize());
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
	
	/**
	 * Allocates memory for {@link VkVertexInputBindingDescription}.
	 * Needs to be freed outside to prevent memory leakes.
	 */
	public VkVertexInputBindingDescription.Buffer callocBinding() {
		var vertexBinding = VkVertexInputBindingDescription.calloc(1)
				.binding(0)
				.stride(VERTEX_SIZE)
				.inputRate(VK_VERTEX_INPUT_RATE_VERTEX);
		return vertexBinding;
	}
	
	/**
	 * Allocates memory for {@link VkVertexInputAttributeDescription}.
	 * Needs to be freed outside to prevent memory leakes.
	 */
	public VkVertexInputAttributeDescription.Buffer callocAttribute() {
		var vertexAttribute = VkVertexInputAttributeDescription.calloc(2);
		vertexAttribute.get(0)
			.binding(0)
			.location(0)
			.format(VK_FORMAT_R32G32_SFLOAT) //2 32 bit float values
			.offset(0);
		vertexAttribute.get(1)
				.binding(0)
				.location(1)
				.format(VK_FORMAT_R32G32B32_SFLOAT)
				.offset(COLOR_OFFSET); //TODO: check
		
		return vertexAttribute;
	}
	
	public long get() {
		return buffer;
	}
}
