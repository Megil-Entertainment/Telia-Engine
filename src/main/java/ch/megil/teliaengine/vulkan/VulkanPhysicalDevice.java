package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkEnumeratePhysicalDevices;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceProperties;

import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanPhysicalDevice {
	private VkPhysicalDevice physicalDevice;
	private VkPhysicalDeviceProperties physicalDeviceProperties;
	
	/**
	 * @param instance An initialized {@link VulkanInstance}
	 * @param deviceType Prefered device type. See: {@link VK10#VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU}.
	 */
	public void init(VulkanInstance instance, int deviceType) throws VulkanException {
		physicalDevice = createPhysicalDevice(instance.get(), deviceType);
		
		physicalDeviceProperties = VkPhysicalDeviceProperties.calloc();
		vkGetPhysicalDeviceProperties(physicalDevice, physicalDeviceProperties);
	}
	
	private VkPhysicalDevice createPhysicalDevice(VkInstance instance, int deviceType) throws VulkanException {
		var gpuCount = memAllocInt(1);
		var res = vkEnumeratePhysicalDevices(instance, gpuCount, null);
		
		if (res != VK_SUCCESS) {
			memFree(gpuCount);
			throw new VulkanException(res);
		}
		
		var gpus = memAllocPointer(gpuCount.get(0));
		res = vkEnumeratePhysicalDevices(instance, gpuCount, gpus);
		
		var deviceProperties = VkPhysicalDeviceProperties.calloc();
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			// get first, if not requested type check if there is one, otherwise use first device
			var physicalDevice = new VkPhysicalDevice(gpus.get(0), instance);
			
			vkGetPhysicalDeviceProperties(physicalDevice, deviceProperties);
			
			if (deviceProperties.deviceType() != deviceType) {
				for (var i = 1; i < gpuCount.get(0); i++) {
					var tempDevice = new VkPhysicalDevice(gpus.get(i), instance);
					vkGetPhysicalDeviceProperties(tempDevice, deviceProperties);
					
					if (deviceProperties.deviceType() == deviceType) {
						physicalDevice = tempDevice;
						break;
					}
				}
			}
			
			return physicalDevice;
		} finally {
			deviceProperties.free();
			memFree(gpus);
			memFree(gpuCount);
		}
	}
	
	public void cleanUp() {
		if (physicalDeviceProperties != null) {
			physicalDeviceProperties.free();
			physicalDeviceProperties = null;
		}
		
		physicalDevice = null;
	}
	
	public VkPhysicalDevice get() {
		return physicalDevice;
	}
	
	public VkPhysicalDeviceProperties getProperties() {
		return physicalDeviceProperties;
	}
}
