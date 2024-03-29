package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VkSamplerCreateInfo;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanSampler {
	private long sampler;

	/**
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 */
	public void init(VulkanLogicalDevice logicalDevice) throws VulkanException {
		var createInfo = VkSamplerCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
				.magFilter(VK_FILTER_LINEAR)
				.minFilter(VK_FILTER_LINEAR)
				.addressModeU(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
				.addressModeV(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
				.addressModeW(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
				.anisotropyEnable(false)
				.maxAnisotropy(0)
				.borderColor(VK_BORDER_COLOR_INT_OPAQUE_BLACK)
				.unnormalizedCoordinates(false)
				.compareEnable(false)
				.compareOp(VK_COMPARE_OP_ALWAYS)
				.mipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR)
				.mipLodBias(0)
				.minLod(0)
				.maxLod(0);
		
		var pSampler = memAllocLong(1);
		
		try {
			var res = vkCreateSampler(logicalDevice.get(), createInfo, null, pSampler);
			
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			sampler = pSampler.get();
		} finally {
			memFree(pSampler);
			createInfo.free();
		}
	}
	
	public long get() {
		return sampler;
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		if (sampler != VK_NULL_HANDLE) {
			vkDestroySampler(logicalDevice.get(), sampler, null);
			sampler = VK_NULL_HANDLE;
		}
	}
}
