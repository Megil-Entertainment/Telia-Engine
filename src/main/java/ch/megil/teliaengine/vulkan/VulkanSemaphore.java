package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateSemaphore;
import static org.lwjgl.vulkan.VK10.vkDestroySemaphore;

import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

public class VulkanSemaphore {
	private long[] semaphore;
	
	public void init(VulkanLogicalDevice logicalDevice, int numOfSem) throws VulkanException {
		semaphore = new long[numOfSem];
		
		//Vulkan needs the struct, even thought it is technically empty
		var semaphoreInfo = VkSemaphoreCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);
		
		var pSemaphore = memAllocLong(1);
		
		try {
			for (var i = 0; i < numOfSem; i++) {
				var res = vkCreateSemaphore(logicalDevice.get(), semaphoreInfo, null, pSemaphore);
				if (res != VK_SUCCESS) {
					throw new VulkanException(res);
				}
				semaphore[i] = pSemaphore.get(0);
			}
		} finally {
			memFree(pSemaphore);
			semaphoreInfo.free();
		}
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		if (semaphore != null) {
			for (var s : semaphore) {
				vkDestroySemaphore(logicalDevice.get(), s, null);
			}
		}
	}
	
	public long get(int index) {
		return semaphore[index];
	}
}
