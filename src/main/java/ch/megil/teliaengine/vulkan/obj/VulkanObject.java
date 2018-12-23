package ch.megil.teliaengine.vulkan.obj;

import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.ByteBuffer;

public abstract class VulkanObject {
	protected ByteBuffer vertecies;
	protected ByteBuffer indicies;
	
	public VulkanObject(int verteciesSize, int indeciesSize) {
		vertecies = memAlloc(verteciesSize);
		indicies = memAlloc(indeciesSize);
	}
	
	public long getVerteciesAddress() {
		return memAddress(vertecies);
	}
	
	public long getVerteciesSize() {
		return vertecies.capacity();
	}
	
	public long getIndiciesAddress() {
		return memAddress(indicies);
	}
	
	public long getIndiciesSize() {
		return indicies.capacity();
	}
	
	public void free() {
		memFree(vertecies);
		memFree(indicies);
	}
}
