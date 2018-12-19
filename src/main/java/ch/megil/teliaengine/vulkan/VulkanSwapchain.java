package ch.megil.teliaengine.vulkan;

import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_FIFO_KHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkCreateSwapchainKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkGetSwapchainImagesKHR;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.vulkan.*;

import ch.megil.teliaengine.vulkan.exception.VulkanException;

/**
 * This class needs setup first with {@link #init} and
 * needs to be cleaned up before destruction with {@link #cleanUp}.
 */
public class VulkanSwapchain {
	private long swapchain;
	private VkExtent2D swapchainExtent;
	private int imgBufferCount;
	public long[] imgBuffers;
	private long[] imgBufferViews;
	
	/**
	 * Initializes a swapchain with a tripple buffering vsync strategy.
	 * 
	 * @param physicalDevice An initialized {@link VulkanPhysicalDevice}
	 * @param surface A window surface. See {@link GLFWVulkan#glfwCreateWindowSurface}
	 * @param queue An initialized {@link VulkanQueue}
	 * @param logicalDevice An initialized {@link VulkanLogicalDevice}
	 * @param color An initialized {@link VulkanColor}
	 */
	public void init(VulkanPhysicalDevice physicalDevice, long surface, VulkanQueue queue, VulkanLogicalDevice logicalDevice, VulkanColor color, int prefWidth, int prefHeight) throws VulkanException {
		initSwapchain(physicalDevice.get(), surface, queue, logicalDevice.get(), color, prefWidth, prefHeight);
		imgBuffers = getSwapchainBuffers(logicalDevice.get(), swapchain);
		
		imgBufferCount = imgBuffers.length;
		imgBufferViews = new long[imgBufferCount];
		for (var i = 0; i < imgBufferCount; i++) {
			imgBufferViews[i] = createImageView(logicalDevice.get(), color.getFormat(), imgBuffers[i]);
		}
	}
	
	private void initSwapchain(VkPhysicalDevice physicalDevice, long surface, VulkanQueue queue, VkDevice logicalDevice, VulkanColor color, int prefWidth, int prefHeight) throws VulkanException {
		var surfaceCapabilities = VkSurfaceCapabilitiesKHR.calloc();
		var swapchainCreateInfo = VkSwapchainCreateInfoKHR.calloc();
		
		var queueFamilyIndices = memAllocInt(2);
		var pSwapchain = memAllocLong(1);
		
		try {
			var res = vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface, surfaceCapabilities);
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			swapchainExtent = surfaceCapabilities.currentExtent();
			if (swapchainExtent.width() == -1) {
				swapchainExtent.width(Math.max(surfaceCapabilities.minImageExtent().width(), Math.min(surfaceCapabilities.maxImageExtent().width(), prefWidth)));
				swapchainExtent.height(Math.max(surfaceCapabilities.minImageExtent().height(), Math.min(surfaceCapabilities.maxImageExtent().height(), prefHeight)));
			}
			
			var imageCount = surfaceCapabilities.minImageCount()+1; // minImageCount + 1 results in tripple buffering
			if (surfaceCapabilities.maxImageCount() > 0 && imageCount > surfaceCapabilities.maxImageCount()) {
				imageCount = surfaceCapabilities.maxImageCount();
			}
			
			swapchainCreateInfo
					.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
					.surface(surface)
					.minImageCount(imageCount)
					.imageFormat(color.getFormat())
					.imageColorSpace(color.getSpace())
					.imageExtent(swapchainExtent)
					.imageArrayLayers(1)
					.imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
					.preTransform(surfaceCapabilities.currentTransform())
					.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
					.presentMode(VK_PRESENT_MODE_FIFO_KHR) //TODO: add presentation fallback
					.clipped(true)
					.oldSwapchain(VK_NULL_HANDLE);
			
			queueFamilyIndices.put(queue.getGraphicsFamily());
			queueFamilyIndices.put(queue.getPresentFamily());
			if (queue.getGraphicsFamily() != queue.getPresentFamily()) {
				swapchainCreateInfo
						.imageSharingMode(VK_SHARING_MODE_CONCURRENT)
						.pQueueFamilyIndices(queueFamilyIndices);
				VkSwapchainCreateInfoKHR.nqueueFamilyIndexCount(swapchainCreateInfo.address(), queueFamilyIndices.capacity());
			} else {
				swapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);
			}
			
			res = vkCreateSwapchainKHR(logicalDevice, swapchainCreateInfo, null, pSwapchain);
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			swapchain = pSwapchain.get(0);
		} finally {
			memFree(pSwapchain);
			memFree(queueFamilyIndices);
			swapchainCreateInfo.free();
			surfaceCapabilities.free();
		}
	}
	
	private long[] getSwapchainBuffers(VkDevice logicalDevice, long swapchain) throws VulkanException {
		var pImageCount = memAllocInt(1);
		var res = vkGetSwapchainImagesKHR(logicalDevice, swapchain, pImageCount, null);
		if (res != VK_SUCCESS) {
			memFree(pImageCount);
			throw new VulkanException(res);
		}
		
		var imageCount = pImageCount.get(0);
		var pSwapchainImages = memAllocLong(imageCount);
		
		res = vkGetSwapchainImagesKHR(logicalDevice, swapchain, pImageCount, pSwapchainImages);
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			var swapchainImages = new long[imageCount];
			for (var i = 0; i < imageCount; i++) {
				swapchainImages[i] = pSwapchainImages.get(i);
			}
			
			return swapchainImages;
		} finally {
			memFree(pSwapchainImages);
			memFree(pImageCount);
		}
	}
	
	private long createImageView(VkDevice logicalDevice, int colorFormat, long bufferHandle) throws VulkanException {
		var imageViewCreateInfo = VkImageViewCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
//				.flags(0)
				.image(bufferHandle)
				.viewType(VK_IMAGE_VIEW_TYPE_2D)
				.format(colorFormat)
				.components(c ->
						c.r(VK_COMPONENT_SWIZZLE_IDENTITY)
						.g(VK_COMPONENT_SWIZZLE_IDENTITY)
						.b(VK_COMPONENT_SWIZZLE_IDENTITY)
						.a(VK_COMPONENT_SWIZZLE_IDENTITY))
				.subresourceRange(sr ->
						sr.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
						.baseMipLevel(0)
						.levelCount(1)
						.baseArrayLayer(0)
						.layerCount(1));
		
		var pView = memAllocLong(1);
		var res = vkCreateImageView(logicalDevice, imageViewCreateInfo, null, pView);
		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			long imageView = pView.get(0);
			return imageView;
		} finally {
			memFree(pView);
			imageViewCreateInfo.free();
		}
	}
	
	public void cleanUp(VulkanLogicalDevice logicalDevice) {
		for (var i = 0; i < imgBufferCount; i++) {
			if (imgBufferViews[i] != VK_NULL_HANDLE) {
				vkDestroyImageView(logicalDevice.get(), imgBufferViews[i], null);
			}
		}
		imgBufferViews = null;
		imgBuffers = null;
		imgBufferCount = 0;
		
		if (swapchain != VK_NULL_HANDLE) {
			vkDestroySwapchainKHR(logicalDevice.get(), swapchain, null);
			swapchain = VK_NULL_HANDLE;
		}
	}
	
	public long get() {
		return swapchain;
	}
	
	public int getImageCount() {
		return imgBufferCount;
	}
	
	public long[] getImageViews() {
		return imgBufferViews;
	}
	
	public VkExtent2D getExtent() {
		return swapchainExtent;
	}
}
