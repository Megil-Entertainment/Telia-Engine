package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.*;

import ch.megil.teliaengine.vulkan.buffer.VulkanIndexBuffer;
import ch.megil.teliaengine.vulkan.buffer.VulkanVertexBuffer;
import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanRenderPass {
	public static final int BASE_SUBPASS_INDEX = 0;
	private static final int VERTEX_OFFSET = 0;
	private static final long INDEX_OFFSET = 0;
	private static final int BASE_BINDING = 0;
	private static final int INSTANCE_COUNT = 1;
	private static final int BASE_INDEX = 0;
	private static final int BASE_INSTANCE = 0;
	
	private long renderPass;
	
	/**
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param color An initialized {@link VulkanColor}
	 */
	public void init(VulkanLogicalDevice logicalDevice, VulkanColor color) throws VulkanException {
		var colorAttachment = VkAttachmentDescription.calloc(1)
				.format(color.getFormat())
				.samples(VK_SAMPLE_COUNT_1_BIT)
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
	
	public void linkRender(VulkanSwapchain swapchain, VulkanPipeline pipeline, VulkanFramebuffers framebuffers, VulkanCommandPool commandPool, VulkanVertexBuffer vertexBuffer, VulkanIndexBuffer indexBuffer, VkClearValue.Buffer clearColor, int width, int height) throws VulkanException {
		var beginInfo = VkRenderPassBeginInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
				.renderPass(renderPass)
				.renderArea(a -> a
						.offset(o -> o.x(0).y(0))
						.extent(e -> e.width(width).height(height)))
				.pClearValues(clearColor);
		VkRenderPassBeginInfo.nclearValueCount(beginInfo.address(), clearColor.capacity());

		VkViewport.Buffer viewport = VkViewport.calloc(1)
				.width(width)
				.height(height)
				.minDepth(0.0f)
				.maxDepth(1.0f);

		VkRect2D.Buffer scissor = VkRect2D.calloc(1)
				.extent(swapchain.getExtent())
				.offset(o -> o.x(0).y(0));

		var pVertexBuffer = memAllocLong(1);
		pVertexBuffer.put(0, vertexBuffer.get());
		var vertexOffsets = memAllocLong(1);
		vertexOffsets.put(0, VERTEX_OFFSET);
		
		try {
			for (var i = 0; i < swapchain.getImageCount(); i++) {
				var framebuffer = framebuffers.get()[i];
				var cmdbuffer = commandPool.getCommandBuffer(i);
				
				beginInfo.framebuffer(framebuffer);
				
				cmdbuffer.begin();
				vkCmdBeginRenderPass(cmdbuffer.get(), beginInfo, VK_SUBPASS_CONTENTS_INLINE);
				
				vkCmdSetViewport(cmdbuffer.get(), 0, viewport);
		        vkCmdSetScissor(cmdbuffer.get(), 0, scissor);
				
				vkCmdBindPipeline(cmdbuffer.get(), VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline.getGraphicsPipeline());
				vkCmdBindVertexBuffers(cmdbuffer.get(), BASE_BINDING, pVertexBuffer, vertexOffsets);
				vkCmdBindIndexBuffer(cmdbuffer.get(), indexBuffer.get(), INDEX_OFFSET, VK_INDEX_TYPE_UINT16);
				vkCmdDrawIndexed(cmdbuffer.get(), indexBuffer.getMaxIndicies(), INSTANCE_COUNT, BASE_INDEX, VERTEX_OFFSET, BASE_INSTANCE);
				
				vkCmdEndRenderPass(cmdbuffer.get());
				cmdbuffer.end();
			}
		} finally {
			scissor.free();
			viewport.free();
			beginInfo.free();
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
