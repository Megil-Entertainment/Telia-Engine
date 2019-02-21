package ch.megil.teliaengine.file;

import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VkImageCreateInfo;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.vulkan.VulkanLogicalDevice;
import ch.megil.teliaengine.vulkan.VulkanPhysicalDevice;
import ch.megil.teliaengine.vulkan.buffer.VulkanImageSrcBuffer;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

public class VulkanTextureLoader {
	private static final int DEPTH_2D = 1;
	private static final int NO_MIPMAP = 1;
	private static final int NO_ARRAY = 1;
	private static final int NO_FLAGS = 0;
	
	public void load(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice device, String name, double width, double height) throws AssetNotFoundException, VulkanException {
		var fileName = GameConfiguration.ASSETS_TEXTURES.getConfiguration() + "/" + name + GameConfiguration.FILE_EXT_TEXTURE.getConfiguration();
		
		var pTexWidth = memAllocInt(1);
		var pTexHeight = memAllocInt(1);
		var pTexChannels = memAllocInt(1);
		var pixels = stbi_load(fileName, pTexWidth, pTexHeight, pTexChannels, STBI_rgb_alpha);
		
		var texWidth = pTexWidth.get(0);
		var texHeight = pTexHeight.get(0);
		
		var size = texWidth * texHeight * STBI_rgb_alpha;
		//TODO: add resizing

		var buffer = new VulkanImageSrcBuffer();
		
		try {
			buffer.init(physicalDevice, device, size);
			buffer.writeImage(device, pixels);
		} finally {
			stbi_image_free(pixels);
			memFree(pTexChannels);
			memFree(pTexHeight);
			memFree(pTexWidth);
		}
		
		var imgCreateInfo = VkImageCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
				.imageType(VK_IMAGE_TYPE_2D)
				.extent(e -> e
						.width(texWidth)
						.height(texHeight)
						.depth(DEPTH_2D))
				.mipLevels(NO_MIPMAP)
				.arrayLayers(NO_ARRAY)
				.format(VK_FORMAT_R8G8B8A8_UNORM)
				.tiling(VK_IMAGE_TILING_OPTIMAL)
				.usage(VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT)
				.sharingMode(VK_SHARING_MODE_EXCLUSIVE)
				.samples(VK_SAMPLE_COUNT_1_BIT)
				.flags(NO_FLAGS);
		
		var pImage = memAllocLong(1);
		
		var res = vkCreateImage(device.get(), imgCreateInfo, null, pImage);
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
		} finally {
			memFree(pImage);
			imgCreateInfo.free();
		}
	}
}
