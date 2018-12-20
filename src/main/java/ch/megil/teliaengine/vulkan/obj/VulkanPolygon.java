package ch.megil.teliaengine.vulkan.obj;

import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import ch.megil.teliaengine.vulkan.VulkanIndexBuffer;
import ch.megil.teliaengine.vulkan.VulkanVertexBuffer;

public class VulkanPolygon {
	private ByteBuffer vertecies;
	private ByteBuffer indexes;
	
	public VulkanPolygon() {
		vertecies = memAlloc(VulkanVertexBuffer.VERTEX_SIZE * VulkanVertexBuffer.MAX_VERTECIES);
		var fb = vertecies.asFloatBuffer();
		fb.put( 0.5f).put( 0.5f).put(0.0f).put(0.0f).put(1.0f);
		fb.put(-0.5f).put( 0.5f).put(0.0f).put(1.0f).put(0.0f);
		fb.put( 0.5f).put(-0.5f).put(1.0f).put(1.0f).put(1.0f);
		fb.put( 0.5f).put(-0.5f).put(1.0f).put(0.0f).put(0.0f);
		fb.put(-1.0f).put(-0.5f).put(1.0f).put(1.0f).put(1.0f);
		fb.put(-0.5f).put(-1.0f).put(0.0f).put(0.0f).put(0.0f);
		
		indexes = memAlloc(VulkanIndexBuffer.INDEX_SIZE * VulkanIndexBuffer.MAX_INDEX);
		var ib = indexes.asShortBuffer();
		ib.put((short) 0).put((short) 1).put((short) 2).put((short) 0xFFFF);
		ib.put((short) 3).put((short) 4).put((short) 5);
	}
	
	public long indexBuff() {
		return memAddress(indexes);
	}
	
	public long indexSize() {
		return indexes.capacity();
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
