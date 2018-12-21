package ch.megil.teliaengine.vulkan.obj;

import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.ByteBuffer;
import java.util.Arrays;

import ch.megil.teliaengine.vulkan.VulkanIndexBuffer;
import ch.megil.teliaengine.vulkan.VulkanVertexBuffer;

public class VulkanPolygon2 extends VulkanPolygon {
	private static final short RESET = (short) 0xFFFF;
	
	private static final short[] INDEX_INIT = new short[15];
	
	static {
		Arrays.fill(INDEX_INIT, RESET);
	}
	
	private ByteBuffer vertecies;
	private ByteBuffer indexes;
	
	public VulkanPolygon2() {
		vertecies = memAlloc(VulkanVertexBuffer.VERTEX_SIZE * 1);
		var fb = vertecies.asFloatBuffer();
		fb.put(-0.10f).put(-0.10f).put(0.0f).put(0.0f).put(0.0f);
		
		indexes = memAlloc(VulkanIndexBuffer.INDEX_SIZE * 0);
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
