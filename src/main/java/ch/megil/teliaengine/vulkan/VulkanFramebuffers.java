package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateFramebuffer;
import static org.lwjgl.vulkan.VK10.vkDestroyFramebuffer;

import org.lwjgl.vulkan.VkFramebufferCreateInfo;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanFramebuffers {
	private int framebufferCount;
	private long[] framebuffers;
	
	/**
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param swapchain An initialized {@link VulkanSwapchain}
	 * @param renderPass An initialized {@link VulkanRenderPass}
	 */
	public void init(VulkanLogicalDevice logicalDevice, VulkanSwapchain swapchain, VulkanRenderPass renderPass) throws VulkanException {
		framebufferCount = swapchain.getImageCount();
		framebuffers = new long[framebufferCount];
		
		for (var i = 0; i < framebufferCount; i++) {
			var attachments = memAllocLong(1);
			attachments.put(0, swapchain.getImageViews()[i]);
			
			var framebufferInfo = VkFramebufferCreateInfo.calloc()
					.sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
					.renderPass(renderPass.get())
					.pAttachments(attachments)
					.height(swapchain.getExtent().height())
					.width(swapchain.getExtent().width())
					.layers(1);
			VkFramebufferCreateInfo.nattachmentCount(framebufferInfo.address(), attachments.capacity());
			
			var pFramebuffer = memAllocLong(1);
			var res = vkCreateFramebuffer(logicalDevice.get(), framebufferInfo, null, pFramebuffer);
			
			try {
				if (res != VK_SUCCESS) {
					throw new VulkanException(res);
				}
				framebuffers[i] = pFramebuffer.get(0);
			} finally {
				memFree(pFramebuffer);
				framebufferInfo.free();
				memFree(attachments);
			}
		}
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		for (var i = 0; i < framebufferCount; i++) {
			if (framebuffers[i] != VK_NULL_HANDLE) {
				vkDestroyFramebuffer(logicalDevice.get(), framebuffers[i], null);
			}
		}
		framebuffers = null;
		framebufferCount = 0;
	}
	
	public long[] get() {
		return framebuffers;
	}
}
