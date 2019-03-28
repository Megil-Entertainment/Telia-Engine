package ch.megil.teliaengine.vulkan.buffer;

import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT;

import ch.megil.teliaengine.vulkan.VulkanLogicalDevice;
import ch.megil.teliaengine.vulkan.VulkanPhysicalDevice;
import ch.megil.teliaengine.vulkan.exception.VulkanException;
import ch.megil.teliaengine.vulkan.obj.VulkanObject;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanIndexBuffer extends VulkanBuffer {
	public static final int INDEX_SIZE = 2;
	private static final boolean BIND_MEMORY = true;
	
	private int maxIndicies;
	
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param queueFamilyIndecies of the queues the buffer will be used on
	 */
	public void init(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice, int maxIndicies, int[] queueFamilyIndecies) throws VulkanException {
		super.init(physicalDevice, logicalDevice, INDEX_SIZE*maxIndicies, VK_BUFFER_USAGE_INDEX_BUFFER_BIT, queueFamilyIndecies, (VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT));
		this.maxIndicies = maxIndicies;
	}
	
	public void writeIndicies(VulkanLogicalDevice logicalDevice, VulkanObject vulkanObject) throws VulkanException {
		super.write(logicalDevice, vulkanObject.getIndiciesAddress(), vulkanObject.getIndiciesSize(), BIND_MEMORY);
	}
	
	public void writeIndicies(VulkanLogicalDevice logicalDevice, VulkanObject vulkanObject, int indexOffset) throws VulkanException {
		super.write(logicalDevice, vulkanObject.getIndiciesAddress(), vulkanObject.getIndiciesSize(), INDEX_SIZE*indexOffset, BIND_MEMORY);
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		super.cleanUp(logicalDevice);
	}
	
	public int getMaxIndicies() {
		return maxIndicies;
	}
}
