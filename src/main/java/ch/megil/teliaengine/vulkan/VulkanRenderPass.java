package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.*;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanRenderPass {
	public static final int BASE_SUBPASS_INDEX = 0;
	
	private long renderPass;
	
	/**
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param color An initialized {@link VulkanColor}
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
		
		var subpassDependency = VkSubpassDependency.calloc(1)
				.srcSubpass(VK_SUBPASS_EXTERNAL)
				.dstSubpass(BASE_SUBPASS_INDEX)
				.srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
				.srcAccessMask(0)
				.dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
				.dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT);
		
		var renderPassInfo = VkRenderPassCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
				.pAttachments(colorAttachment)
				.pSubpasses(subpass)
				.pDependencies(subpassDependency);
		VkRenderPassCreateInfo.nattachmentCount(renderPassInfo.address(), colorAttachment.capacity());
		VkRenderPassCreateInfo.nsubpassCount(renderPassInfo.address(), subpass.capacity());
		VkRenderPassCreateInfo.ndependencyCount(renderPassInfo.address(), subpassDependency.capacity());
		
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
	
	public void linkRender(VulkanSwapchain swapchain, VulkanVertexBuffer vertexBuffer, VulkanPipeline pipeline, VulkanFramebuffers framebuffers, VulkanCommandPool commandPool, VkClearValue.Buffer clearColor) throws VulkanException {
		var beginInfo = VkRenderPassBeginInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
				.renderPass(renderPass)
				.renderArea(a -> a
						.offset(o -> o.x(0).y(0))
						.extent(swapchain.getExtent()))
				.pClearValues(clearColor);
		VkRenderPassBeginInfo.nclearValueCount(beginInfo.address(), clearColor.capacity());
		
		var pBuffer = memAllocLong(1);
		pBuffer.put(0, vertexBuffer.get());
		var offsets = memAllocLong(1);
		offsets.put(0, 0L);
		
		try {
			for (var i = 0; i < swapchain.getImageCount(); i++) {
				var framebuffer = framebuffers.get()[i];
				var cmdbuffer = commandPool.getCommandBuffer(i);
				
				beginInfo.framebuffer(framebuffer);
				
				cmdbuffer.begin();
				vkCmdBeginRenderPass(cmdbuffer.get(), beginInfo, VK_SUBPASS_CONTENTS_INLINE);
				
				vkCmdBindPipeline(cmdbuffer.get(), VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline.getGraphicsPipeline());
				vkCmdBindVertexBuffers(cmdbuffer.get(), 0, pBuffer, offsets);
				vkCmdDraw(cmdbuffer.get(), 3, 1, 0, 0);
				
				vkCmdEndRenderPass(cmdbuffer.get());
				cmdbuffer.end();
			}
		} finally {
			beginInfo.clear();
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
