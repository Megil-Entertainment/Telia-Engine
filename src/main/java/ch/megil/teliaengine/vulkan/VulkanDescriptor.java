package ch.megil.teliaengine.vulkan;

import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanDescriptor {
	
	
	public void init() {
		VkDescriptorSetLayoutBinding.calloc();
	}
	
	public void cleanUp() {
		
	}
}
