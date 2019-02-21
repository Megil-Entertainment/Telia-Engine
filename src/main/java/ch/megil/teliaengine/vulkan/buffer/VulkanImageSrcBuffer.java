package ch.megil.teliaengine.vulkan.buffer;

import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;

import java.nio.ByteBuffer;

import ch.megil.teliaengine.vulkan.VulkanLogicalDevice;
import ch.megil.teliaengine.vulkan.VulkanPhysicalDevice;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

public class VulkanImageSrcBuffer extends VulkanBuffer {
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param size of the image (width * height * desired_channels)
	 */
	public void init(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice, int size) throws VulkanException {
		super.init(physicalDevice, logicalDevice, size, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VK_SHARING_MODE_EXCLUSIVE, (VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT));
	}
	
	public void writeImage(VulkanLogicalDevice logicalDevice, ByteBuffer pixels) throws VulkanException {
		super.write(logicalDevice, memAddress(pixels), pixels.capacity());
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		super.cleanUp(logicalDevice);
	}
}
