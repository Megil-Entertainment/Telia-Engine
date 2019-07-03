package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.*;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanDescriptor {
	public static final int SAMPLER_COUNT = 1;
	public static final int IMAGE_COUNT = 1024;
	public static final int BINDING_COUNT = 2;

	public static final int SAMPLER_BINDING = 0;
	public static final int IMAGE_BINDING = 1;
	
	private long pool;
	private LongBuffer layout;
	private LongBuffer pSet;
	private long set;
	
	/**
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 */
	public void init(VulkanLogicalDevice logicalDevice) throws VulkanException {
		initPool(logicalDevice.get());
		layout = initLayout(logicalDevice.get());
		pSet = initSet(logicalDevice.get());
		set = pSet.get(0);
	}
	
	private void initPool(VkDevice device) throws VulkanException {
		var pPool = memAllocLong(1);

		var poolSizes = VkDescriptorPoolSize.calloc(BINDING_COUNT);
		poolSizes.get(SAMPLER_BINDING)
			.type(VK_DESCRIPTOR_TYPE_SAMPLER)
			.descriptorCount(SAMPLER_COUNT);
		
		poolSizes.get(IMAGE_BINDING)
			.type(VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE)
			.descriptorCount(IMAGE_COUNT);
		
		var createInfo = VkDescriptorPoolCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO)
				.pPoolSizes(poolSizes)
				.maxSets(SAMPLER_COUNT + IMAGE_COUNT);
		VkDescriptorPoolCreateInfo.npoolSizeCount(createInfo.address(), BINDING_COUNT);
		
		var res = vkCreateDescriptorPool(device, createInfo, null, pPool);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			pool = pPool.get(0);
		} finally {
			createInfo.free();
			poolSizes.free();
			memFree(pPool);
		}
	}
	
	private LongBuffer initLayout(VkDevice device) throws VulkanException {
		var pLayout = memAllocLong(1);
		
		var layoutBindings = VkDescriptorSetLayoutBinding.calloc(BINDING_COUNT);
		layoutBindings.get(SAMPLER_BINDING)
			.binding(SAMPLER_BINDING)
			.descriptorCount(SAMPLER_COUNT)
			.stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT)
			.descriptorType(VK_DESCRIPTOR_TYPE_SAMPLER);
		
		layoutBindings.get(IMAGE_BINDING)
			.binding(IMAGE_BINDING)
			.descriptorCount(IMAGE_COUNT)
			.stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT)
			.descriptorType(VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE);
		
		var createInfo = VkDescriptorSetLayoutCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
				.pBindings(layoutBindings);
		VkDescriptorSetLayoutCreateInfo.nbindingCount(createInfo.address(), BINDING_COUNT);

		var res = vkCreateDescriptorSetLayout(device, createInfo, null, pLayout);
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			return pLayout;
		} catch (Exception e) {
			memFree(pLayout);
			throw e;
		} finally {
			createInfo.free();
			layoutBindings.free();
		}
	}
	
	private LongBuffer initSet(VkDevice device) throws VulkanException {
		var pSet = memAllocLong(1);
		
		var allocInfo = VkDescriptorSetAllocateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO)
				.descriptorPool(pool)
				.pSetLayouts(layout);
		VkDescriptorSetAllocateInfo.ndescriptorSetCount(allocInfo.address(), 1);
		
		try {
			var res = vkAllocateDescriptorSets(device, allocInfo, pSet);
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			return pSet;
		} finally {
			allocInfo.free();
		}
	}
	
	public LongBuffer getLayout() {
		return layout;
	}
	
	public LongBuffer getSetPointer() {
		return pSet;
	}
	
	public long getSet() {
		return set;
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		if (set != VK_NULL_HANDLE) {
			vkFreeDescriptorSets(logicalDevice.get(), pool, set);
			set = VK_NULL_HANDLE;
		}
		
		if (pSet != null) {
			memFree(pSet);
			pSet = null;
		}
		
		if (layout != null) {
			vkDestroyDescriptorSetLayout(logicalDevice.get(), layout.get(0), null);
			memFree(layout);
			layout = null;
		}
		
		if (pool != VK_NULL_HANDLE) {
			vkDestroyDescriptorPool(logicalDevice.get(), pool, null);
			pool = VK_NULL_HANDLE;
		}
	}
}
