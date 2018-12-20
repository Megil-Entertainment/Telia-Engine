package ch.megil.teliaengine.vulkan;

import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32B32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_VERTEX_INPUT_RATE_VERTEX;

import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

import ch.megil.teliaengine.vulkan.exception.VulkanException;
import ch.megil.teliaengine.vulkan.obj.VulkanPolygon;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 * 
 * Exception to this rule are {@link #callocBinding()} and {@link #callocAttribute()}.
 */
public class VulkanVertexBuffer extends VulkanBuffer {
	private static final int VALUE_SIZE = 4;
	public static final int VERTEX_SIZE = (2+3)*VALUE_SIZE; //2 cords + 3 colors
	public static final int MAX_VERTECIES = 6;
	private static final int COLOR_OFFSET = 2*VALUE_SIZE;
	
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 */
	public void init(VulkanPhysicalDevice physicalDevice, VulkanLogicalDevice logicalDevice) throws VulkanException {
		super.init(physicalDevice, logicalDevice, VERTEX_SIZE*MAX_VERTECIES);
	}
	
	public void writeVertecies(VulkanLogicalDevice logicalDevice, VulkanPolygon polygon) throws VulkanException {
		super.write(logicalDevice, polygon.getAddress(), polygon.getSize());
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
}
