package ch.megil.teliaengine.vulkan.image;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkImageCreateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;

import ch.megil.teliaengine.vulkan.VulkanLogicalDevice;
import ch.megil.teliaengine.vulkan.VulkanMemory;
import ch.megil.teliaengine.vulkan.VulkanPhysicalDevice;
import ch.megil.teliaengine.vulkan.exception.VulkanException;
/**
 * This class is a parent class for different image types and
 * needs setup first with {@link #init} and needs to be cleaned
 * up before destruction with {@link #cleanUp}.
 */
public abstract class VulkanImage {
	private static final long MEMORY_OFFSET = 0;
	private static final int DEPTH_2D = 1;
	private static final int NO_MIPMAP = 1;
	private static final int NO_ARRAY = 1;
	private static final int NO_FLAGS = 0;
	
	protected long image;
	protected long memory;
	
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param width of the image
	 * @param height of the image
	 * @param format of the image (see {@link VK10#VK_FORMAT_R8G8B8A8_UNORM})
	 * @param tiling of the image (see {@link VK10#VK_IMAGE_TILING_OPTIMAL})
	 * @param usage of the image (see {@link VK10#VK_IMAGE_USAGE_TRANSFER_DST_BIT})
	 * @param sharingMode of the image (see {@link VK10#VK_SHARING_MODE_EXCLUSIVE})
	 * @param memProperties memory properties of the image (see {@link VK10#VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT})
	 */
	public void init(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice, int width, int height, int format, int tiling, int usage, int sharingMode, int memProperties) throws VulkanException {
		var imgCreateInfo = VkImageCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
				.imageType(VK_IMAGE_TYPE_2D)
				.extent(e -> e
						.width(width)
						.height(height)
						.depth(DEPTH_2D))
				.mipLevels(NO_MIPMAP)
				.arrayLayers(NO_ARRAY)
				.format(format)
				.tiling(tiling)
				.usage(usage)
				.sharingMode(sharingMode)
				.samples(VK_SAMPLE_COUNT_1_BIT)
				.flags(NO_FLAGS);
		
		var memoryRequirements = VkMemoryRequirements.calloc();
		
		var pImage = memAllocLong(1);
		var pMemory = memAllocLong(1);
		var res = vkCreateImage(logicalDevice.get(), imgCreateInfo, null, pImage);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			image = pImage.get(0);
			
			vkGetImageMemoryRequirements(logicalDevice.get(), image, memoryRequirements);
			new VulkanMemory().allocateMemory(physicalDevice, logicalDevice, memoryRequirements, memProperties, pMemory);
			memory = pMemory.get(0);
			
			res = vkBindImageMemory(logicalDevice.get(), image, memory, MEMORY_OFFSET);
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
		} finally {
			memFree(pMemory);
			memFree(pImage);
			memoryRequirements.free();
			imgCreateInfo.free();
		}
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		if (image != VK_NULL_HANDLE) {
			vkDestroyImage(logicalDevice.get(), image, null);
			image = VK_NULL_HANDLE;
		}
		
		if (memory != VK_NULL_HANDLE) {
			vkFreeMemory(logicalDevice.get(), memory, null);
			memory = VK_NULL_HANDLE;
		}
	}
}
