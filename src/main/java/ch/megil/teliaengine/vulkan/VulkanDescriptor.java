package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanDescriptor {
	public void init(VulkanLogicalDevice logicalDevice) throws VulkanException {
		var descriptorLayout = VkDescriptorSetLayoutBinding.calloc(2);
		descriptorLayout.get(0)
			.binding(0)
			.descriptorCount(1)
			.stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT)
			.descriptorType(VK_DESCRIPTOR_TYPE_SAMPLER);
		
		descriptorLayout.get(1)
			.binding(1)
			.descriptorCount(8)
			.stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT)
			.descriptorType(VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE);
		
		var createInfo = VkDescriptorSetLayoutCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
				.pBindings(descriptorLayout);
		VkDescriptorSetLayoutCreateInfo.nbindingCount(createInfo.address(), 2);
		
		var layout = memAllocLong(1);
		var res = vkCreateDescriptorSetLayout(logicalDevice.get(), createInfo, null, layout);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
		} finally {
			createInfo.free();
			descriptorLayout.free();
		}
	}
	
	public void cleanUp() {
		
	}
}
