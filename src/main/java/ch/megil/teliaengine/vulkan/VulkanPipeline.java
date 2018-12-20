package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.*;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanPipeline {
	private long pipelineLayout;
	private long graphicsPipeline;
	
	/**
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param swapchain An initialized {@link VulkanSwapchain}
	 * @param shader An initialized {@link VulkanShader}
	 * @param renderPass An initialized {@link VulkanRenderPass}
	 * @param vertexBuffer An {@link VulkanVertexBuffer} (not necessarly initialized)
	 */
	public void init(VulkanLogicalDevice logicalDevice, VulkanSwapchain swapchain, VulkanShader shader, VulkanRenderPass renderPass, VulkanVertexBuffer vertexBuffer) throws VulkanException {
		pipelineLayout = createPipelineLayout(logicalDevice.get());
		
		var vertexShader = callocShaderStage(shader.getVertShader(), VK_SHADER_STAGE_VERTEX_BIT);
		var fragShader = callocShaderStage(shader.getFragShader(), VK_SHADER_STAGE_FRAGMENT_BIT);
		
		var shaderStageInfoBuffer = VkPipelineShaderStageCreateInfo.calloc(2);
		shaderStageInfoBuffer.put(0, vertexShader);
		shaderStageInfoBuffer.put(1, fragShader);
		
		var vertexBinding = vertexBuffer.callocBinding();
		var vertexAttribute = vertexBuffer.callocAttribute();
		
		var vertexInputInfo = VkPipelineVertexInputStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
				.pVertexBindingDescriptions(vertexBinding)
				.pVertexAttributeDescriptions(vertexAttribute);
		VkPipelineVertexInputStateCreateInfo.nvertexBindingDescriptionCount(vertexInputInfo.address(), vertexBinding.capacity());
		VkPipelineVertexInputStateCreateInfo.nvertexAttributeDescriptionCount(vertexInputInfo.address(), vertexAttribute.capacity());

		var inputAssemblyInfo = VkPipelineInputAssemblyStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
				.topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP)
				.primitiveRestartEnable(true);
		
		var viewport = callocViewport(swapchain.getExtent());
		var scissor = callocScissor(swapchain.getExtent());
		
		var viewportInfo = VkPipelineViewportStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
				.viewportCount(1)
				.pViewports(viewport)
				.scissorCount(1)
				.pScissors(scissor);
		
		var rasterizationInfo = VkPipelineRasterizationStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
				.depthClampEnable(false)
				.rasterizerDiscardEnable(false)
				.polygonMode(VK_POLYGON_MODE_FILL)
				.cullMode(VK_CULL_MODE_NONE)
				.frontFace(VK_FRONT_FACE_CLOCKWISE)
				.depthBiasEnable(false);
		
		var multisamplingInfo = VkPipelineMultisampleStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
				.sampleShadingEnable(false)
				.rasterizationSamples(VK_SAMPLE_COUNT_1_BIT);
		
		var colorBlendAttachment = VkPipelineColorBlendAttachmentState.calloc(1)
				.colorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT)
				.blendEnable(false);
		
		var colorBlendInfo = VkPipelineColorBlendStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
				.pAttachments(colorBlendAttachment);
		VkPipelineColorBlendStateCreateInfo.nattachmentCount(colorBlendInfo.address(), colorBlendAttachment.capacity());
		
		var dynamicStates = memAllocInt(2)
				.put(VK_DYNAMIC_STATE_VIEWPORT)
				.put(VK_DYNAMIC_STATE_SCISSOR)
				.flip();
        var dynamicStateInfo = VkPipelineDynamicStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO)
                .pDynamicStates(dynamicStates);
        VkPipelineDynamicStateCreateInfo.ndynamicStateCount(dynamicStateInfo.address(), dynamicStates.capacity());
		
		var graphicsPipelineInfo = VkGraphicsPipelineCreateInfo.calloc(1)
				.sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
				.pStages(shaderStageInfoBuffer)
				.pVertexInputState(vertexInputInfo)
				.pInputAssemblyState(inputAssemblyInfo)
				.pViewportState(viewportInfo)
				.pRasterizationState(rasterizationInfo)
				.pMultisampleState(multisamplingInfo)
				.pColorBlendState(colorBlendInfo)
				.pDynamicState(dynamicStateInfo)
				.layout(pipelineLayout)
				.renderPass(renderPass.get())
				.subpass(VulkanRenderPass.BASE_SUBPASS_INDEX);
		VkGraphicsPipelineCreateInfo.nstageCount(graphicsPipelineInfo.address(), shaderStageInfoBuffer.capacity());
		
		var pGraphicsPipeline = memAllocLong(1);
		var res = vkCreateGraphicsPipelines(logicalDevice.get(), VK_NULL_HANDLE, graphicsPipelineInfo, null, pGraphicsPipeline);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			graphicsPipeline = pGraphicsPipeline.get(0);
		} finally {
			memFree(pGraphicsPipeline);
			graphicsPipelineInfo.free();
			
			dynamicStateInfo.free();
			memFree(dynamicStates);
			
			colorBlendInfo.free();
			colorBlendAttachment.free();
			
			multisamplingInfo.free();
			rasterizationInfo.free();
			
			viewportInfo.free();
			scissor.free();
			viewport.free();
			
			inputAssemblyInfo.free();
			vertexInputInfo.free();
			vertexAttribute.free();
			vertexBinding.free();
			
			shaderStageInfoBuffer.free();
			memFree(fragShader.pName());
			fragShader.free();
			memFree(vertexShader.pName());
			vertexShader.free();
		}
	}
	
	private long createPipelineLayout(VkDevice device) throws VulkanException {
		var pipelineLayoutInfo = VkPipelineLayoutCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO);
		VkPipelineLayoutCreateInfo.nsetLayoutCount(pipelineLayoutInfo.address(), 0);
		VkPipelineLayoutCreateInfo.npushConstantRangeCount(pipelineLayoutInfo.address(), 0);
		
		LongBuffer pPipelineLayout = memAllocLong(1);
        var res = vkCreatePipelineLayout(device, pipelineLayoutInfo, null, pPipelineLayout);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
	        var pipelineLayout = pPipelineLayout.get(0);
	        return pipelineLayout;
		} finally {
			memFree(pPipelineLayout);
			pipelineLayoutInfo.free();
		}
	}
	
	private VkPipelineShaderStageCreateInfo callocShaderStage(long module, int stage) {
		var name = memUTF8("main");
		var shaderStageCreateInfo = VkPipelineShaderStageCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
				.stage(stage)
				.module(module)
				.pName(name);
		
		return shaderStageCreateInfo;
	}
	
	private VkViewport.Buffer callocViewport(VkExtent2D curExt) {
		var viewport = VkViewport.calloc(1)
				.x(0.0f)
				.y(0.0f)
				.width(curExt.width())
				.height(curExt.height())
				.minDepth(0.0f)
				.maxDepth(1.0f);
		
		return viewport;
	}
	
	private VkRect2D.Buffer callocScissor(VkExtent2D curExt) {
		var scissor = VkRect2D.calloc(1)
				.offset(off -> off.x(0).y(0))
				.extent(curExt);
		
		return scissor;
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		if (graphicsPipeline != VK_NULL_HANDLE) {
			vkDestroyPipeline(logicalDevice.get(), graphicsPipeline, null);
			graphicsPipeline = VK_NULL_HANDLE;
		}
		
		if (pipelineLayout != VK_NULL_HANDLE) {
			vkDestroyPipelineLayout(logicalDevice.get(), pipelineLayout, null);
			pipelineLayout = VK_NULL_HANDLE;
		}
	}
	
	public long getGraphicsPipeline() {
		return graphicsPipeline;
	}
}
