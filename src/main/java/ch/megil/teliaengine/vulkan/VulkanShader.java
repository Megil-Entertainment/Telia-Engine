package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateShaderModule;
import static org.lwjgl.vulkan.VK10.vkDestroyShaderModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanShader {
	private static final String VERT_SHADER = "shader/vert.spv";
	private static final String FRAG_SHADER = "shader/frag.spv";
	
	private long vertShader;
	private long fragShader;
	
	/**
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 */
	public void init(VulkanLogicalDevice logicalDevice) throws VulkanException {
		vertShader = loadModule(logicalDevice.get(), VERT_SHADER);
		fragShader = loadModule(logicalDevice.get(), FRAG_SHADER);
	}
	
	private ByteBuffer loadShaderCode(String shader) throws VulkanException {
		var file = new File(shader);
		try (var fis = new FileInputStream(file); var fc = fis.getChannel()) {
			var buffer = fc.map(MapMode.READ_ONLY, 0, fc.size());
			return buffer;
		} catch (IOException e) {
			throw new VulkanException("Shader " + shader + " could not be loaded.", e);
		}
	}
	
	private long loadModule(VkDevice logicalDevice, String shader) throws VulkanException {
		var code = loadShaderCode(shader);
		var shaderModuleCreateInfo = VkShaderModuleCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
				.flags(0)
				.pCode(code);
		VkShaderModuleCreateInfo.ncodeSize(shaderModuleCreateInfo.address(), code.capacity());
		var pShaderModule = memAllocLong(1);
		
		var res = vkCreateShaderModule(logicalDevice, shaderModuleCreateInfo, null, pShaderModule);
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			var shaderModule = pShaderModule.get(0);
			return shaderModule;
		} finally {
			memFree(pShaderModule);
			shaderModuleCreateInfo.free();
		}
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		if (fragShader != NULL) {
			vkDestroyShaderModule(logicalDevice.get(), fragShader, null);
			fragShader = NULL;
		}

		if (vertShader != NULL) {
			vkDestroyShaderModule(logicalDevice.get(), vertShader, null);
			vertShader = NULL;
		}
	}
	
	public long getVertShader() {
		return vertShader;
	}
	
	public long getFragShader() {
		return fragShader;
	}
}
