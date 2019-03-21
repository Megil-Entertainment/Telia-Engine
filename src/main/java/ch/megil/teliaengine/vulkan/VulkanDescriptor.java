package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.*;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanDescriptor {
	private long pool;
	private long layout;
	private long set;
	
	public void init(VulkanLogicalDevice logicalDevice) throws VulkanException {
		initPool(logicalDevice.get());
		initLayout(logicalDevice.get());
		initSet(logicalDevice.get());
	}
	
	private void initPool(VkDevice device) throws VulkanException {
		var pPool = memAllocLong(1);
		
		var poolSizes = VkDescriptorPoolSize.calloc(2);
		poolSizes.get(0)
			.type(VK_DESCRIPTOR_TYPE_SAMPLER)
			.descriptorCount(1);
		
		poolSizes.get(1)
			.type(VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE)
			.descriptorCount(8);
		
		var createInfo = VkDescriptorPoolCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO)
				.pPoolSizes(poolSizes)
				.maxSets(9);
		VkDescriptorPoolCreateInfo.npoolSizeCount(createInfo.address(), 2);
		
		var res = vkCreateDescriptorPool(device, createInfo, null, pPool);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			pool = pPool.get(0);
		} finally {
			createInfo.free();
		}
	}
	
	private void initLayout(VkDevice device) throws VulkanException {
		var pLayout = memAllocLong(1);
		
		var layoutBindings = VkDescriptorSetLayoutBinding.calloc(2);
		layoutBindings.get(0)
			.binding(0)
			.descriptorCount(1)
			.stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT)
			.descriptorType(VK_DESCRIPTOR_TYPE_SAMPLER);
		
		layoutBindings.get(1)
			.binding(1)
			.descriptorCount(8)
			.stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT)
			.descriptorType(VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE);
		
		var createInfo = VkDescriptorSetLayoutCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
				.pBindings(layoutBindings);
		VkDescriptorSetLayoutCreateInfo.nbindingCount(createInfo.address(), 2);

		var res = vkCreateDescriptorSetLayout(device, createInfo, null, pLayout);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			layout = pLayout.get(0);
		} finally {
			createInfo.free();
			layoutBindings.free();
			memFree(pLayout);
		}
	}
	
	private void initSet(VkDevice device) throws VulkanException {
		//TODO: find bug
		var pLayout = memAllocLong(1).put(layout);
		var pSet = memAllocLong(1);
		
		var allocInfo = VkDescriptorSetAllocateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO)
				.descriptorPool(pool)
				.pSetLayouts(pLayout);
		VkDescriptorSetAllocateInfo.ndescriptorSetCount(allocInfo.address(), 2);
		
		try {
			var res = vkAllocateDescriptorSets(device, allocInfo, pSet);
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			set = pSet.get(0);
		} finally {
			allocInfo.free();
			memFree(pLayout);
			memFree(pSet);
		}
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
//		if (set != VK_NULL_HANDLE) {
//			vkFreeDescriptorSets(logicalDevice.get(), pool, set);
//			set = VK_NULL_HANDLE;
//		}
		
		if (layout != VK_NULL_HANDLE) {
			vkDestroyDescriptorSetLayout(logicalDevice.get(), layout, null);
			layout = VK_NULL_HANDLE;
		}
		
		if (pool != VK_NULL_HANDLE) {
			vkDestroyDescriptorPool(logicalDevice.get(), pool, null);
			pool = VK_NULL_HANDLE;
		}
	}
}
