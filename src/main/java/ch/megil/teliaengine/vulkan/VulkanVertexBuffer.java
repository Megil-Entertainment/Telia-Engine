package ch.megil.teliaengine.vulkan;

import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32B32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_VERTEX_INPUT_RATE_VERTEX;

import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanVertexBuffer {
	private static final int VALUE_SIZE = 4;
	private static final int VERTEX_SIZE = (2+3)*VALUE_SIZE; //2 cords + 3 colors
	private static final int COLOR_OFFSET = 2*VALUE_SIZE;
	
	private VkVertexInputBindingDescription.Buffer vertexBinding;
	private VkVertexInputAttributeDescription.Buffer vertexAttribute;
	
	public void init() {
		vertexBinding = VkVertexInputBindingDescription.calloc(1)
				.binding(0)
				.stride(VERTEX_SIZE)
				.inputRate(VK_VERTEX_INPUT_RATE_VERTEX);
		
		vertexAttribute = VkVertexInputAttributeDescription.calloc(2);
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
	}
	
	public void cleanUp() {
		vertexBinding.free();
		vertexAttribute.free();
	}
	
	public VkVertexInputBindingDescription.Buffer getBinding() {
		return vertexBinding;
	}
	
	public VkVertexInputAttributeDescription.Buffer getAttribute() {
		return vertexAttribute;
	}
}
