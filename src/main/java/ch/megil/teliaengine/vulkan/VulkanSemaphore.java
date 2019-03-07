package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateSemaphore;
import static org.lwjgl.vulkan.VK10.vkDestroySemaphore;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanSemaphore {
	private LongBuffer[] semaphore;
	
	/**
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param numOfSem number of semaphores wanted
	 */
	public void init(VulkanLogicalDevice logicalDevice, int numOfSem) throws VulkanException {
		semaphore = new LongBuffer[numOfSem];
		
		//Vulkan needs the struct, even thought it is technically empty
		var semaphoreInfo = VkSemaphoreCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);
		
		try {
			for (var i = 0; i < numOfSem; i++) {
				var pSemaphore = memAllocLong(1);
				var res = vkCreateSemaphore(logicalDevice.get(), semaphoreInfo, null, pSemaphore);
				if (res != VK_SUCCESS) {
					throw new VulkanException(res);
				}
				semaphore[i] = pSemaphore;
			}
		} finally {
			semaphoreInfo.free();
		}
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		if (semaphore != null) {
			for (var s : semaphore) {
				vkDestroySemaphore(logicalDevice.get(), s.get(0), null);
				memFree(s);
			}
			semaphore = null;
		}
	}
	
	public long get(int index) {
		return semaphore[index].get(0);
	}
	
	public LongBuffer getPointer(int index) {
		return semaphore[index];
	}
}
