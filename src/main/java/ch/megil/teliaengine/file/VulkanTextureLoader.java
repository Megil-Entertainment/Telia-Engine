package ch.megil.teliaengine.file;

import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.vulkan.VulkanLogicalDevice;
import ch.megil.teliaengine.vulkan.VulkanPhysicalDevice;
import ch.megil.teliaengine.vulkan.buffer.VulkanImageSrcBuffer;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

public class VulkanTextureLoader {
	public void load(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice, String name, double width, double height) throws AssetNotFoundException, VulkanException {
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
			buffer.init(physicalDevice, logicalDevice, size);
			buffer.writeImage(logicalDevice, pixels);
		} finally {
			stbi_image_free(pixels);
			memFree(pTexChannels);
			memFree(pTexHeight);
			memFree(pTexWidth);
		}
		
		//TODO: create image
	}
}
