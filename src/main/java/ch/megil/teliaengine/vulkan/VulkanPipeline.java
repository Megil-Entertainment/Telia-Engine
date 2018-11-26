package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO;

import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanPipeline {
	
	/**
	 * Initializes the vulkan pipeline.
	 * 
	 * @param shader An initialized {@link VulkanShader}
	 */
	public void init(VulkanShader shader) {
		var shaderStageCreateInfos = VkPipelineShaderStageCreateInfo.calloc(2);
		shaderStageCreateInfos.put(0, callocShaderStage(shader.getVertShader(), VK_SHADER_STAGE_VERTEX_BIT));
		shaderStageCreateInfos.put(1, callocShaderStage(shader.getFragShader(), VK_SHADER_STAGE_FRAGMENT_BIT));
		
		try {
			//TODO: init pipeline
		} finally {
			memFree(shaderStageCreateInfos.get(0).pName());
			shaderStageCreateInfos.get(0).free();
			memFree(shaderStageCreateInfos.get(1).pName());
			shaderStageCreateInfos.get(1).free();
			shaderStageCreateInfos.free();
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
	
	public void cleanUp() {
		
	}
}
