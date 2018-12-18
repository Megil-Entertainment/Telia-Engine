package ch.megil.teliaengine.vulkan.obj;

import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.FloatBuffer;

import ch.megil.teliaengine.vulkan.VulkanVertexBuffer;

public class VulkanPolygon {
	private FloatBuffer vertecies;
	
	public VulkanPolygon() {
		vertecies = memAllocFloat(VulkanVertexBuffer.VERTEX_SIZE * VulkanVertexBuffer.MAX_VERTECIES);
		vertecies.put( 0.0f).put(-0.5f).put(1.0f).put(1.0f).put(1.0f);
		vertecies.put( 0.5f).put( 0.5f).put(0.0f).put(0.0f).put(1.0f);
		vertecies.put(-0.5f).put( 0.5f).put(0.0f).put(0.0f).put(1.0f);
	}
	
	public void free() {
		memFree(vertecies);
	}
	
	public long getAddress() {
		return memAddress(vertecies);
	}
	
	public long getSize() {
		return vertecies.capacity();
	}
}
