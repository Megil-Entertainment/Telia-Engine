package ch.megil.teliaengine.vulkan;

import ch.megil.teliaengine.vulkan.exception.VulkanException;
import ch.megil.teliaengine.vulkan.obj.VulkanPolygon;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanIndexBuffer extends VulkanBuffer {
	public static final int INDEX_SIZE = 2;
//	public static final int MAX_INDEX = 15;
	
	private int maxIndicies;
	
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 */
	public void init(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice, int maxIndicies) throws VulkanException {
		super.init(physicalDevice, logicalDevice, INDEX_SIZE*maxIndicies);
		this.maxIndicies = maxIndicies;
	}
	
	public void writeVertecies(VulkanLogicalDevice logicalDevice, VulkanPolygon polygon) throws VulkanException {
		super.write(logicalDevice, polygon.indexBuff(), polygon.indexSize());
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		super.cleanUp(logicalDevice);
	}
	
	public int getMaxIndicies() {
		return maxIndicies;
	}
}
