package ch.megil.teliaengine.vulkan;

import ch.megil.teliaengine.vulkan.exception.VulkanException;
import ch.megil.teliaengine.vulkan.obj.VulkanObject;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanIndexBuffer extends VulkanBuffer {
	public static final int INDEX_SIZE = 2;
	
	private int maxIndicies;
	
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 */
	public void init(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice, int maxIndicies) throws VulkanException {
		super.init(physicalDevice, logicalDevice, INDEX_SIZE*maxIndicies);
		this.maxIndicies = maxIndicies;
	}
	
	public void writeIndicies(VulkanLogicalDevice logicalDevice, VulkanObject vulkanObject) throws VulkanException {
		super.write(logicalDevice, vulkanObject.getIndiciesAddress(), vulkanObject.getIndiciesSize());
	}
	
	public void writeIndicies(VulkanLogicalDevice logicalDevice, VulkanObject vulkanObject, int indexOffset) throws VulkanException {
		super.write(logicalDevice, vulkanObject.getIndiciesAddress(), vulkanObject.getIndiciesSize(), INDEX_SIZE*indexOffset);
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		super.cleanUp(logicalDevice);
	}
	
	public int getMaxIndicies() {
		return maxIndicies;
	}
}