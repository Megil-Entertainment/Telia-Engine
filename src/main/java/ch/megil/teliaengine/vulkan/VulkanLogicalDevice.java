package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.*;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanLogicalDevice {
	private static final float BASE_PRIORITY = 1f;
	private static final int GRAPHITCS_QUEUE_INDEX = 0;
	private static final int PRESENT_QUEUE_INDEX = 0;
	
	private VkDevice logicalDevice;
	
	/**
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param queue An initialized {@link VulkanQueue}
	 */
	public void init(VulkanPhysicalDevice physicalDevice, VulkanQueue queue) throws VulkanException {
		logicalDevice = createDevice(physicalDevice.get(), queue);
		updateQueue(queue);
	}
	
	private VkDevice createDevice(VkPhysicalDevice physicalDevice, VulkanQueue queue) throws VulkanException {
		var queuePriorities = memAllocFloat(1);
		queuePriorities.put(0, BASE_PRIORITY);
		var queueCreateInfos = VkDeviceQueueCreateInfo.calloc(2);
		queueCreateInfos.get(0)
				.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
				.queueFamilyIndex(queue.getGraphicsFamily())
				.pQueuePriorities(queuePriorities);
		VkDeviceQueueCreateInfo.nqueueCount(queueCreateInfos.get(0).address(), 1);
		queueCreateInfos.get(1)
				.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
				.queueFamilyIndex(queue.getPresentFamily())
				.pQueuePriorities(queuePriorities);
		VkDeviceQueueCreateInfo.nqueueCount(queueCreateInfos.get(1).address(), 1);
		
		var vkKhrSwapchainExtension = memUTF8(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
		var enabledExtensionNames = memAllocPointer(1)
				.put(vkKhrSwapchainExtension)
				.flip();

		var deviceCreateInfo = VkDeviceCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
				.pQueueCreateInfos(queueCreateInfos)
				.ppEnabledExtensionNames(enabledExtensionNames);
		VkDeviceCreateInfo.nqueueCreateInfoCount(deviceCreateInfo.address(), queueCreateInfos.capacity());
		VkDeviceCreateInfo.nenabledExtensionCount(deviceCreateInfo.address(), enabledExtensionNames.capacity());

		var pDevice = memAllocPointer(1);
		var res = vkCreateDevice(physicalDevice, deviceCreateInfo, null, pDevice);
		
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			var logicalDevice = new VkDevice(pDevice.get(0), physicalDevice, deviceCreateInfo);
			return logicalDevice;
		} finally {
			memFree(pDevice);
			deviceCreateInfo.free();
			memFree(enabledExtensionNames);
			memFree(vkKhrSwapchainExtension);
			queueCreateInfos.free();
			memFree(queuePriorities);
		}
	}
	
	private void updateQueue(VulkanQueue queue) {
		var pQueue = memAllocPointer(1);
		vkGetDeviceQueue(logicalDevice, queue.getGraphicsFamily(), GRAPHITCS_QUEUE_INDEX, pQueue);
		queue.setGraphicsQueue(new VkQueue(pQueue.get(0), logicalDevice));
		memFree(pQueue);
		pQueue = memAllocPointer(1);
		vkGetDeviceQueue(logicalDevice, queue.getPresentFamily(), PRESENT_QUEUE_INDEX, pQueue);
		queue.setPresentQueue(new VkQueue(pQueue.get(0), logicalDevice));
		memFree(pQueue);
	}
	
	public void cleanUp() {
		if (logicalDevice != null) {
			vkDestroyDevice(logicalDevice, null);
			logicalDevice = null;
		}
	}
	
	public VkDevice get() {
		return logicalDevice;
	}
}
