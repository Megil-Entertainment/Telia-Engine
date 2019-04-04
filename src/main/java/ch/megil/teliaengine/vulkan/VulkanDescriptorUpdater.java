package ch.megil.teliaengine.vulkan;

import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VkDescriptorImageInfo;
import org.lwjgl.vulkan.VkWriteDescriptorSet;

import ch.megil.teliaengine.vulkan.image.VulkanImage;

public class VulkanDescriptorUpdater {
	private VkDescriptorImageInfo.Buffer samplerInfo;
	private VkDescriptorImageInfo.Buffer imageInfo;
	private VkWriteDescriptorSet.Buffer setWrites;
	
	private int nextImageIndex;
	
	public void init(VulkanSampler sampler, VulkanDescriptor descriptor) {
		nextImageIndex = 0;
		
		samplerInfo = VkDescriptorImageInfo.calloc(VulkanDescriptor.SAMPLER_COUNT);
		samplerInfo.sampler(sampler.get());
		
		imageInfo = VkDescriptorImageInfo.calloc(VulkanDescriptor.IMAGE_COUNT);
		
		setWrites = VkWriteDescriptorSet.calloc(VulkanDescriptor.BINDING_COUNT);
		setWrites.get(VulkanDescriptor.SAMPLER_BINDING)
			.sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
			.dstBinding(VulkanDescriptor.SAMPLER_BINDING)
			.dstArrayElement(0)
			.descriptorType(VK_DESCRIPTOR_TYPE_SAMPLER)
			.dstSet(descriptor.getSet())
			.pImageInfo(samplerInfo);
		VkWriteDescriptorSet.ndescriptorCount(setWrites.get(VulkanDescriptor.SAMPLER_BINDING).address(), 1);
		setWrites.get(VulkanDescriptor.IMAGE_BINDING)
			.sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
			.dstBinding(VulkanDescriptor.IMAGE_BINDING)
			.dstArrayElement(0)
			.descriptorType(VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE)
			.dstSet(descriptor.getSet())
			.pImageInfo(imageInfo);
		VkWriteDescriptorSet.ndescriptorCount(setWrites.get(VulkanDescriptor.IMAGE_BINDING).address(), nextImageIndex);
	}
	
	public int addImage(VulkanImage image) {
		var currentIndex = nextImageIndex;
		nextImageIndex++;
		
		imageInfo.get(currentIndex)
			.sampler(VK_NULL_HANDLE)
			.imageLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
			.imageView(image.getImageView());
		VkWriteDescriptorSet.ndescriptorCount(setWrites.get(VulkanDescriptor.IMAGE_BINDING).address(), nextImageIndex);
		
		return currentIndex;
	}
	
	public void updateDescriptor(VulkanLogicalDevice logicalDevice) {
		vkUpdateDescriptorSets(logicalDevice.get(), setWrites, null);
	}
	
	public void cleanUp() {
		nextImageIndex = 0;
		
		if (setWrites != null) {
			setWrites.free();
			setWrites = null;
		}
		
		if (imageInfo != null) {
			imageInfo.free();
			imageInfo = null;
		}
		
		if (samplerInfo != null) {
			samplerInfo.free();
			samplerInfo = null;
		}
	}
}
