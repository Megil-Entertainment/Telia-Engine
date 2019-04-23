package ch.megil.teliaengine.vulkan.file;

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

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.configuration.ProjectFolderConfiguration;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.GameElement;
import ch.megil.teliaengine.helper.ValuePair;
import ch.megil.teliaengine.vulkan.VulkanDescriptorUpdater;
import ch.megil.teliaengine.vulkan.VulkanLogicalDevice;
import ch.megil.teliaengine.vulkan.VulkanPhysicalDevice;
import ch.megil.teliaengine.vulkan.VulkanQueue;
import ch.megil.teliaengine.vulkan.buffer.VulkanImageSrcBuffer;
import ch.megil.teliaengine.vulkan.command.VulkanCommandBuffer;
import ch.megil.teliaengine.vulkan.command.VulkanCommandPool;
import ch.megil.teliaengine.vulkan.exception.VulkanException;
import ch.megil.teliaengine.vulkan.image.VulkanImage;
import ch.megil.teliaengine.vulkan.image.VulkanTexture;

/**
 * This class needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanTextureLoader {
	private Map<String, ValuePair<Integer, VulkanImage>> cache;
	
	public VulkanTextureLoader() {
		cache = new HashMap<String, ValuePair<Integer, VulkanImage>>();
	}
	
	/**
	 * Loads and caches a {@link VulkanTexture}.
	 * 
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param queue An initialized {@link VulkanQueue}
	 * @param commandPool An initialized {@link VulkanCommandPool}
	 * @param descriptorUpdater An initialized {@link VulkanDescriptorUpdater}
	 * @param name The name of the texture to load
	 * @return {@link ValuePair} of descriptor index and {@link VulkanTexture}
	 */
	public ValuePair<Integer, VulkanImage> load(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice, VulkanQueue queue, VulkanCommandPool commandPool, VulkanDescriptorUpdater descriptorUpdater, String name) throws AssetNotFoundException, VulkanException {
		if (cache.containsKey(name)) {
			return cache.get(name);
		}
		
		var fileName = ProjectFolderConfiguration.ASSETS_TEXTURES.getConfiguration() + "/" + name + FileConfiguration.FILE_EXT_TEXTURE.getConfiguration();
		
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
		
		var cmdBuffers = new VulkanCommandBuffer[0];
		
		try {
			buffer.init(physicalDevice, logicalDevice, size, new int[] {queue.getGraphicsFamily()});
			buffer.writeImage(logicalDevice, pixels);
			
			cmdBuffers = new VulkanCommandBuffer[] {
					commandPool.getSingleUseBuffer(logicalDevice),
					commandPool.getSingleUseBuffer(logicalDevice),
					commandPool.getSingleUseBuffer(logicalDevice)
				};
			
			image.init(physicalDevice, logicalDevice, texWidth, texHeight, format);
			image.transition(logicalDevice, queue, cmdBuffers[0], VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL);
			image.copyBufferToImage(buffer, logicalDevice, queue, cmdBuffers[1]);
			image.transition(logicalDevice, queue, cmdBuffers[2], VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
			
			image.createView(logicalDevice);
			
			var descriptorIndex = descriptorUpdater.addImage(image);
			var cachePair = new ValuePair<Integer, VulkanImage>(descriptorIndex, image);
			cache.put(name, cachePair);
			
			return cachePair;
		} catch (Exception e) {
			image.cleanUp(logicalDevice);
			throw e;
		} finally {
			commandPool.removeBuffer(logicalDevice, cmdBuffers);
			buffer.cleanUp(logicalDevice);
			stbi_image_free(pixels);
			memFree(pTexChannels);
			memFree(pTexHeight);
			memFree(pTexWidth);
		}
	}
	
	/**
	 * Loads and caches a {@link VulkanTexture}. Also updates the descriptor index of the given {@link GameElement}
	 * 
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param queue An initialized {@link VulkanQueue}
	 * @param commandPool An initialized {@link VulkanCommandPool}
	 * @param descriptorUpdater An initialized {@link VulkanDescriptorUpdater}
	 * @param element The {@link GameElement} to load the texture of and update.
	 */
	public void loadAndUpdateGameElementTexture(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice, VulkanQueue queue, VulkanCommandPool commandPool, VulkanDescriptorUpdater descriptorUpdater, GameElement element) throws AssetNotFoundException, VulkanException {
		var texInfo = load(physicalDevice, logicalDevice, queue, commandPool, descriptorUpdater, element.getDepictionName());
		element.setDepictionIndex(texInfo.getA());
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		for (var entry : cache.entrySet()) {
			entry.getValue().getB().cleanUp(logicalDevice);
		}
	}
}
