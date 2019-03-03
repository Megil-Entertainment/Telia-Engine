package ch.megil.teliaengine.file;

import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkSubmitInfo;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.vulkan.VulkanLogicalDevice;
import ch.megil.teliaengine.vulkan.VulkanPhysicalDevice;
import ch.megil.teliaengine.vulkan.VulkanQueue;
import ch.megil.teliaengine.vulkan.buffer.VulkanImageSrcBuffer;
import ch.megil.teliaengine.vulkan.command.VulkanSingleCommandBuffer;
import ch.megil.teliaengine.vulkan.exception.VulkanException;
import ch.megil.teliaengine.vulkan.image.VulkanTexture;

public class VulkanTextureLoader {
	public void load(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice, VulkanQueue queue, VulkanSingleCommandBuffer cmdBuffer, String name, double width, double height) throws AssetNotFoundException, VulkanException {
		var fileName = GameConfiguration.ASSETS_TEXTURES.getConfiguration() + "/" + name + GameConfiguration.FILE_EXT_TEXTURE.getConfiguration();
		
		var pTexWidth = memAllocInt(1);
		var pTexHeight = memAllocInt(1);
		var pTexChannels = memAllocInt(1);
		var pixels = stbi_load(fileName, pTexWidth, pTexHeight, pTexChannels, STBI_rgb_alpha);
		
		var texWidth = pTexWidth.get(0);
		var texHeight = pTexHeight.get(0);
		
		var size = texWidth * texHeight * STBI_rgb_alpha;
		//TODO: add resizing
		
		var format = VK_FORMAT_R8G8B8A8_UNORM;

		var buffer = new VulkanImageSrcBuffer();
		var barrier = VkImageMemoryBarrier.calloc(1);
		
		try {
			buffer.init(physicalDevice, logicalDevice, size);
			buffer.writeImage(logicalDevice, pixels);
			
			cmdBuffer.begin();
			
			barrier
				.sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
				.oldLayout(VK_IMAGE_LAYOUT_UNDEFINED)
				.newLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
				.srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
				.dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
				.image(0) //TODO:
				.subresourceRange(srr -> srr
						.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
						.baseMipLevel(0)
						.levelCount(1)
						.baseArrayLayer(0)
						.layerCount(1))
				.srcAccessMask(0) //TODO:
				.dstAccessMask(0); //TODO:
			
			//TODO:
			vkCmdPipelineBarrier(cmdBuffer.get(), 0, 0, 0, null, null, barrier);
			
			cmdBuffer.end();
			submit(queue, cmdBuffer);
		} finally {
			barrier.free();
			buffer.cleanUp(logicalDevice);
			stbi_image_free(pixels);
			memFree(pTexChannels);
			memFree(pTexHeight);
			memFree(pTexWidth);
		}
		
		var image = new VulkanTexture();
		image.init(physicalDevice, logicalDevice, texWidth, texHeight, format);
	}
	
	private void transition() {
		
	}
	
	private void submit(VulkanQueue queue, VulkanSingleCommandBuffer cmdBuffer) throws VulkanException {
		var pCmdBuffer = memAllocPointer(1);
		pCmdBuffer.put(0, cmdBuffer.get());
		var submitInfo = VkSubmitInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
				.pCommandBuffers(pCmdBuffer);
		VkSubmitInfo.ncommandBufferCount(submitInfo.address(), pCmdBuffer.capacity());
		
		try {
			var res = vkQueueSubmit(queue.getGraphicsQueue(), submitInfo, VK_NULL_HANDLE);
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			vkQueueWaitIdle(queue.getGraphicsQueue());
		} finally {
			submitInfo.free();
			memFree(pCmdBuffer);
		}
	}
}
