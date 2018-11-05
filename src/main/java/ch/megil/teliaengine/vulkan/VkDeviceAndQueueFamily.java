package ch.megil.teliaengine.vulkan;

import org.lwjgl.vulkan.VkDevice;

public class VkDeviceAndQueueFamily {
	private VkDevice device;
	private int queueFamilyIndex;

	public VkDeviceAndQueueFamily(VkDevice device, int queueFamilyIndex) {
		this.device = device;
		this.queueFamilyIndex = queueFamilyIndex;
	}

	public VkDevice getDevice() {
		return device;
	}

	public int getQueueFamilyIndex() {
		return queueFamilyIndex;
	}
}
