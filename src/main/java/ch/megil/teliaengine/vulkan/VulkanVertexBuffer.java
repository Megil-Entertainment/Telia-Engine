package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32B32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_VERTEX_INPUT_RATE_VERTEX;

import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

import ch.megil.teliaengine.vulkan.exception.VulkanException;
import ch.megil.teliaengine.vulkan.obj.VulkanObject;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 * 
 * Exception to this rule are {@link #callocBinding()} and {@link #callocAttribute()}.
 */
public class VulkanVertexBuffer extends VulkanBuffer {
	private static final int VALUE_SIZE = 4;
	private static final int VERTEX_VALUES = 2+3; //2 cords + 3 colors
	public static final int VERTEX_SIZE = VERTEX_VALUES*VALUE_SIZE;
	private static final int COLOR_OFFSET = 2*VALUE_SIZE;
	
	private static final float CLEAR_VALUE = 0.0f;
	
	private int maxVertecies;
	
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 */
	public void init(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice, int maxVertecies) throws VulkanException {
		super.init(physicalDevice, logicalDevice, VERTEX_SIZE*maxVertecies);
		this.maxVertecies = maxVertecies;
		clearBuffer(logicalDevice);
	}
	
	private void clearBuffer(VulkanLogicalDevice logicalDevice) throws VulkanException {
		var buffer = memAlloc(VERTEX_SIZE*maxVertecies);
		var fb = buffer.asFloatBuffer();
		
		for(var i = 0; i < VERTEX_VALUES*maxVertecies; i++) {
			fb.put(CLEAR_VALUE);
		}
		
		write(logicalDevice, memAddress(buffer), buffer.capacity());
		
		memFree(buffer);
	}
	
	public void writeVertecies(VulkanLogicalDevice logicalDevice, VulkanObject vulkanObject) throws VulkanException {
		super.write(logicalDevice, vulkanObject.getVerteciesAddress(), vulkanObject.getVerteciesSize());
	}
	
	public void writeVertecies(VulkanLogicalDevice logicalDevice, VulkanObject vulkanObject, int vertexOffset) throws VulkanException {
		super.write(logicalDevice, vulkanObject.getVerteciesAddress(), vulkanObject.getVerteciesSize(), VERTEX_SIZE*vertexOffset);
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		super.cleanUp(logicalDevice);
	}
	
	/**
	 * Allocates memory for {@link VkVertexInputBindingDescription}.
	 * Needs to be freed outside to prevent memory leakes.
	 */
	public VkVertexInputBindingDescription.Buffer callocBinding() {
		var vertexBinding = VkVertexInputBindingDescription.calloc(1)
				.binding(0)
				.stride(VERTEX_SIZE)
				.inputRate(VK_VERTEX_INPUT_RATE_VERTEX);
		return vertexBinding;
	}
	
	/**
	 * Allocates memory for {@link VkVertexInputAttributeDescription}.
	 * Needs to be freed outside to prevent memory leakes.
	 */
	public VkVertexInputAttributeDescription.Buffer callocAttribute() {
		var vertexAttribute = VkVertexInputAttributeDescription.calloc(2);
		vertexAttribute.get(0)
			.binding(0)
			.location(0)
			.format(VK_FORMAT_R32G32_SFLOAT) //2 32 bit float values
			.offset(0);
		vertexAttribute.get(1)
				.binding(0)
				.location(1)
				.format(VK_FORMAT_R32G32B32_SFLOAT)
				.offset(COLOR_OFFSET);
		
		return vertexAttribute;
	}
	
	public int getMaxVertecies() {
		return maxVertecies;
	}
}