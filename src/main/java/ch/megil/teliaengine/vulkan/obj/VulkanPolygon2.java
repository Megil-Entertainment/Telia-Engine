package ch.megil.teliaengine.vulkan.obj;

import ch.megil.teliaengine.vulkan.VulkanVertexBuffer;

public class VulkanPolygon2 extends VulkanObject {
	public VulkanPolygon2() {
		super(VulkanVertexBuffer.VERTEX_SIZE, 0);
		
		var fb = vertecies.asFloatBuffer();
		fb.put(-0.10f).put(-0.10f).put(0.0f).put(0.0f).put(0.0f);
	}
}
