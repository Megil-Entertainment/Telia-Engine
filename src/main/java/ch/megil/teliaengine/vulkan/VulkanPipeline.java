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
	 */
	public void init(VulkanLogicalDevice logicalDevice, VulkanSwapchain swapchain, VulkanShader shader, VulkanRenderPass renderPass) throws VulkanException {
		pipelineLayout = createPipelineLayout(logicalDevice.get());
		
		var vertexShader = callocShaderStage(shader.getVertShader(), VK_SHADER_STAGE_VERTEX_BIT);
		var fragShader = callocShaderStage(shader.getFragShader(), VK_SHADER_STAGE_FRAGMENT_BIT);
		
		var shaderStageInfoBuffer = VkPipelineShaderStageCreateInfo.calloc(2);
		shaderStageInfoBuffer.put(0, vertexShader);
		shaderStageInfoBuffer.put(1, fragShader);
		
		//TODO: update
		var vertexBinding = VkVertexInputBindingDescription.calloc(1)
				.binding(0)
				.stride((2+3)*4)
				.inputRate(VK_VERTEX_INPUT_RATE_VERTEX);
		
		var vertexAttribute = VkVertexInputAttributeDescription.calloc(2);
		vertexAttribute.get(0)
				.binding(0)
				.location(0)
				.format(VK_FORMAT_R32G32_SFLOAT) //2 32 bit float values
				.offset(0);
		vertexAttribute.get(1)
				.binding(0)
				.location(1)
				.format(VK_FORMAT_R32G32B32_SFLOAT)
				.offset(2*4);
		
		var vertexInputInfo = VkPipelineVertexInputStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
				.pVertexBindingDescriptions(vertexBinding)
				.pVertexAttributeDescriptions(vertexAttribute);
		VkPipelineVertexInputStateCreateInfo.nvertexBindingDescriptionCount(vertexInputInfo.address(), vertexBinding.capacity());
		VkPipelineVertexInputStateCreateInfo.nvertexAttributeDescriptionCount(vertexInputInfo.address(), vertexAttribute.capacity());
		
		var inputAssemblyInfo = VkPipelineInputAssemblyStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
                .topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST) //TODO: update topology
                .primitiveRestartEnable(false);
		
		var viewportInfo = VkPipelineViewportStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
                .viewportCount(1)
                .pViewports(callocViewport(swapchain.getExtent()))
                .scissorCount(1)
                .pScissors(callocScissor(swapchain.getExtent()));
		
		var rasterizationInfo = VkPipelineRasterizationStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
				.depthClampEnable(false)
				.rasterizerDiscardEnable(false)
				.polygonMode(VK_POLYGON_MODE_FILL)
				.lineWidth(1.0f)
				.cullMode(VK_CULL_MODE_NONE) //TODO: maybe add culling
				.frontFace(VK_FRONT_FACE_COUNTER_CLOCKWISE)
				.depthBiasEnable(false);
		
		var multisamplingInfo = VkPipelineMultisampleStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
				.sampleShadingEnable(false)
				.rasterizationSamples(VK_SAMPLE_COUNT_1_BIT); //TODO: enable multisampling
		
		var colorBlendAttachment = VkPipelineColorBlendAttachmentState.calloc(1)
				.colorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT)
				.blendEnable(false);
		
		var colorBlendInfo = VkPipelineColorBlendStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
				.logicOp(VK_FALSE)
				.pAttachments(colorBlendAttachment);
		// set attachmentCount explicitly to have the correct number if not done attachmentCount is always zero
		VkPipelineColorBlendStateCreateInfo.nattachmentCount(colorBlendInfo.address(), colorBlendAttachment.capacity());
		
		var dynamicStates = memAllocInt(2)
				.put(VK_DYNAMIC_STATE_VIEWPORT)
				.put(VK_DYNAMIC_STATE_SCISSOR)
				.flip();
		
		var dynamicStateInfo = VkPipelineDynamicStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO)
				.pDynamicStates(dynamicStates);
		// set dynamicStateCount explicitly to have the correct number if not done dynamicStateCount is always zero
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
		// set stageCount explicitly to have the correct number if not done stageCount is always zero
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
			
			viewportInfo.pScissors().free();
			viewportInfo.pViewports().free();
			viewportInfo.free();
			
			inputAssemblyInfo.free();
			vertexInputInfo.free();
			
			shaderStageInfoBuffer.free();
			memFree(fragShader.pName());
			fragShader.free();
			memFree(vertexShader.pName());
			vertexShader.free();
		}
	}
	
	private long createPipelineLayout(VkDevice device) throws VulkanException {
		var pipelineLayoutInfo = VkPipelineLayoutCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                .pSetLayouts(null);
		
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
}
