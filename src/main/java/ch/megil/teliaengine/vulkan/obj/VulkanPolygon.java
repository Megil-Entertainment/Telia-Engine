package ch.megil.teliaengine.vulkan.obj;

import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.ByteBuffer;
import java.util.Arrays;

import ch.megil.teliaengine.vulkan.VulkanIndexBuffer;
import ch.megil.teliaengine.vulkan.VulkanVertexBuffer;

public class VulkanPolygon {
	private static final short RESET = (short) 0xFFFF;
	
	private static final short[] INDEX_INIT = new short[15];
	
	static {
		Arrays.fill(INDEX_INIT, RESET);
	}
	
	private ByteBuffer vertecies;
	private ByteBuffer indexes;
	
	public VulkanPolygon() {
		vertecies = memAlloc(VulkanVertexBuffer.VERTEX_SIZE * 7);
		var fb = vertecies.asFloatBuffer();
//		fb.put( 0.5f).put( 0.5f).put(0.0f).put(0.0f).put(1.0f);
//		fb.put(-0.5f).put( 0.5f).put(0.0f).put(1.0f).put(0.0f);
//		fb.put( 0.5f).put(-0.5f).put(1.0f).put(1.0f).put(1.0f);
//		fb.put( 0.5f).put(-0.5f).put(1.0f).put(0.0f).put(0.0f);
//		fb.put(-1.0f).put(-0.5f).put(1.0f).put(1.0f).put(1.0f);
//		fb.put(-0.5f).put(-1.0f).put(0.0f).put(0.0f).put(0.0f);
		fb.put(-0.25f).put(-0.75f).put(1.0f).put(0.0f).put(0.0f);
		fb.put( 0.25f).put(-0.75f).put(1.0f).put(1.0f).put(0.0f);
		fb.put( 0.50f).put( 0.00f).put(0.0f).put(1.0f).put(0.0f);
		fb.put( 0.25f).put( 0.75f).put(0.0f).put(1.0f).put(1.0f);
		fb.put(-0.25f).put( 0.75f).put(0.0f).put(0.0f).put(1.0f);
		fb.put(-0.50f).put( 0.00f).put(1.0f).put(0.0f).put(1.0f);
		fb.put( 0.00f).put( 0.00f).put(1.0f).put(1.0f).put(1.0f);
		
		indexes = memAlloc(VulkanIndexBuffer.INDEX_SIZE * 15);
		var ib = indexes.asShortBuffer().put(INDEX_INIT).position(0);
//		ib.put((short) 0).put((short) 1).put((short) 2).put(RESET);
//		ib.put((short) 3).put((short) 4).put((short) 5).put(RESET);
//		ib.put((short) 1).put((short) 3).put((short) 4);
//		ib.put((short) 0).put((short) 1).put((short) 2)
//			.put((short) 3).put((short) 4).put((short) 5)
//			.put((short) 0).put((short) 1).put(RESET);
		ib.put((short) 0).put((short) 1).put((short) 6)
			.put((short) 2).put((short) 3).put(RESET);
		ib.put((short) 3).put((short) 4).put((short) 6)
			.put((short) 5).put((short) 0).put(RESET);
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
