package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VkAttachmentDescription;
import org.lwjgl.vulkan.VkAttachmentReference;
import org.lwjgl.vulkan.VkRenderPassCreateInfo;
import org.lwjgl.vulkan.VkSubpassDescription;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanRenderPass {
	private long renderPass;
	
	/**
	 *TODO: update
	 */
	public void init(VulkanLogicalDevice logicalDevice, VulkanColor color) throws VulkanException {
		var colorAttachment = VkAttachmentDescription.calloc(1)
				.format(color.getFormat())
				.samples(VK_SAMPLE_COUNT_1_BIT) //TODO: add multisampling
				.loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
				.storeOp(VK_ATTACHMENT_STORE_OP_STORE)
				.stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
				.stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
				.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
				.finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);
		
		var colorAttachmentReference = VkAttachmentReference.calloc(1)
				.attachment(0)
				.layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
		
		var subpass = VkSubpassDescription.calloc(1)
				.pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
				.colorAttachmentCount(colorAttachmentReference.capacity())
				.pColorAttachments(colorAttachmentReference);
		
		var renderPassInfo = VkRenderPassCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
				.pAttachments(colorAttachment)
				.pSubpasses(subpass);
		VkRenderPassCreateInfo.nattachmentCount(renderPassInfo.address(), colorAttachment.capacity());
		VkRenderPassCreateInfo.nsubpassCount(renderPassInfo.address(), subpass.capacity());
		
		var pRenderPass = memAllocLong(1);
		var res = vkCreateRenderPass(logicalDevice.get(), renderPassInfo, null, pRenderPass);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			renderPass = pRenderPass.get(0);
		} finally {
			memFree(pRenderPass);
			renderPassInfo.free();
			subpass.free();
			colorAttachmentReference.free();
			colorAttachment.free();
		}
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		if (renderPass != VK_NULL_HANDLE) {
			vkDestroyRenderPass(logicalDevice.get(), renderPass, null);
			renderPass = VK_NULL_HANDLE;
		}
	}
	
	public long get() {
		return renderPass;
	}
}
