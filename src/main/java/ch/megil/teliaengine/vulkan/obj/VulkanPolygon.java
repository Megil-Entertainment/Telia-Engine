package ch.megil.teliaengine.vulkan.obj;

import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.ByteBuffer;

import ch.megil.teliaengine.vulkan.VulkanVertexBuffer;

public class VulkanPolygon {
	private ByteBuffer vertecies;
	
	public VulkanPolygon() {
		vertecies = memAlloc(VulkanVertexBuffer.VERTEX_SIZE * VulkanVertexBuffer.MAX_VERTECIES);
		var fb = vertecies.asFloatBuffer();
		fb.put( 0.0f).put(-0.5f).put(1.0f).put(1.0f).put(1.0f);
		fb.put( 0.5f).put( 0.5f).put(0.0f).put(0.0f).put(1.0f);
		fb.put(-0.5f).put( 0.5f).put(0.0f).put(0.0f).put(1.0f);
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
