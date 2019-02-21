package ch.megil.teliaengine.vulkan.obj;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.LongBuffer;

public class VulkanTexture {
	private LongBuffer texture;
	
	public VulkanTexture() {
		texture = memAllocLong(0);
	}
	
	public void free() {
		memFree(texture);
	}
}
