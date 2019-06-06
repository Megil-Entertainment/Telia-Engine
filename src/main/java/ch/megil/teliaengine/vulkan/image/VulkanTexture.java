package ch.megil.teliaengine.vulkan.image;

import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VK10;

import ch.megil.teliaengine.vulkan.VulkanLogicalDevice;
import ch.megil.teliaengine.vulkan.VulkanPhysicalDevice;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanTexture extends VulkanImage {
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param width of the image
	 * @param height of the image
	 * @param imageFormat format of the image (see {@link VK10#VK_FORMAT_R8G8B8A8_UNORM})
	 */
	public void init(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice, int width, int height, int imageFormat) throws VulkanException {
		super.init(physicalDevice, logicalDevice, width, height, imageFormat, VK_IMAGE_TILING_OPTIMAL, (VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT), VK_SHARING_MODE_EXCLUSIVE, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
	}
}
