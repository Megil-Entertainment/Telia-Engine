package ch.megil.teliaengine.file;

import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;

import java.util.HashMap;
import java.util.Map;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.vulkan.VulkanLogicalDevice;
import ch.megil.teliaengine.vulkan.VulkanPhysicalDevice;
import ch.megil.teliaengine.vulkan.VulkanQueue;
import ch.megil.teliaengine.vulkan.buffer.VulkanImageSrcBuffer;
import ch.megil.teliaengine.vulkan.command.VulkanCommandPool;
import ch.megil.teliaengine.vulkan.exception.VulkanException;
import ch.megil.teliaengine.vulkan.image.VulkanImage;
import ch.megil.teliaengine.vulkan.image.VulkanTexture;

public class VulkanTextureLoader {
	private static VulkanTextureLoader instance;
	
	private Map<String, VulkanImage> cache;
	
	public static VulkanTextureLoader get() {
		if (instance == null) {
			instance = new VulkanTextureLoader();
		}
		return instance;
	}
	
	private VulkanTextureLoader() {
		cache = new HashMap<String, VulkanImage>();
	}
	
	public VulkanImage load(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice, VulkanQueue queue, VulkanCommandPool commandPool, String name) throws AssetNotFoundException, VulkanException {
		if (cache.containsKey(name)) {
			return cache.get(name);
		}
		
		var fileName = GameConfiguration.ASSETS_TEXTURES.getConfiguration() + "/" + name + GameConfiguration.FILE_EXT_TEXTURE.getConfiguration();
		
		var pTexWidth = memAllocInt(1);
		var pTexHeight = memAllocInt(1);
		var pTexChannels = memAllocInt(1);
		var pixels = stbi_load(fileName, pTexWidth, pTexHeight, pTexChannels, STBI_rgb_alpha);
		
		var texWidth = pTexWidth.get(0);
		var texHeight = pTexHeight.get(0);
		
		var size = texWidth * texHeight * STBI_rgb_alpha;
		
		var format = VK_FORMAT_R8G8B8A8_UNORM;

		var buffer = new VulkanImageSrcBuffer();
		var image = new VulkanTexture();
		
		try {
			buffer.init(physicalDevice, logicalDevice, size, new int[] {queue.getGraphicsFamily()});
			buffer.writeImage(logicalDevice, pixels);
			
			image.init(physicalDevice, logicalDevice, texWidth, texHeight, format);
			image.transition(logicalDevice, queue, commandPool.getSingleUseBuffer(logicalDevice), VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL);
			image.copyBufferToImage(buffer, logicalDevice, queue, commandPool.getSingleUseBuffer(logicalDevice));
			image.transition(logicalDevice, queue, commandPool.getSingleUseBuffer(logicalDevice), VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
			
			image.createView(logicalDevice);
			
			cache.put(name, image);
			
			return image;
		} catch (Exception e) {
			image.cleanUp(logicalDevice);
			throw e;
		} finally {
			buffer.cleanUp(logicalDevice);
			stbi_image_free(pixels);
			memFree(pTexChannels);
			memFree(pTexHeight);
			memFree(pTexWidth);
		}
	}
}
