/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package ch.megil.teliaengine.vulkandemo;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.EXTDebugReport.VK_ERROR_VALIDATION_FAILED_EXT;
import static org.lwjgl.vulkan.KHRDisplaySwapchain.VK_ERROR_INCOMPATIBLE_DISPLAY_KHR;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.vulkan.*;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.vulkan.*;
import ch.megil.teliaengine.vulkan.exception.VulkanException;
import ch.megil.teliaengine.vulkan.obj.VulkanPolygon;

/**
 * Renders a simple colored triangle on a cornflower blue background on a GLFW window with Vulkan.
 * <p>
 * This is like the {@link TriangleDemo}, but adds an additional "color" vertex attribute.
 * Do a diff between those two classes to see what's new.
 * 
 * @author Kai Burjack
 */
public class ColoredTriangleDemoTest {
	private static final int SEM_NUM_OF_SEM = 2;
	private static final int SEM_IMAGE_AVAILABLE = 0;
	private static final int SEM_RENDER_FINISHED = 1;
	
	private static final int BASE_WIDTH = 1280;
	private static final int BASE_HEIGHT = 720;
	
	private static long window;
	private static long windowSurface;

	private static VulkanInstance vInstance;
	private static VulkanPhysicalDevice vPhysicalDevice;
	private static VulkanQueue vQueue;
	private static VulkanLogicalDevice vLogicalDevice;
	private static VulkanColor vColor;
	private static VulkanSwapchain vSwapchain;
	private static VulkanRenderPass vRenderPass;
	private static VulkanShader vShader;
	private static VulkanVertexBuffer vVertexBuffer;
	private static VulkanPipeline vPipeline;
	private static VulkanFramebuffers vFramebuffers;
	private static VulkanCommandPool vRenderCommandPool;
	private static VulkanSemaphore vSemaphore;

    /**
     * Remove if added to spec.
     */
    private static final int VK_FLAGS_NONE = 0;

    /**
     * This is just -1L, but it is nicer as a symbolic constant.
     */
    private static final long UINT64_MAX = 0xFFFFFFFFFFFFFFFFL;

    private static class Swapchain {
        long swapchainHandle;
        long[] images;
        long[] imageViews;
    }

    /*
     * All resources that must be reallocated on window resize.
     */
    private static Swapchain swapchain;
    private static long[] framebuffers;
    private static int width, height;
    private static VkCommandBuffer[] renderCommandBuffers;

    public static void main(String[] args) throws IOException, VulkanException {
    	vInstance = new VulkanInstance();
    	vPhysicalDevice = new VulkanPhysicalDevice();
    	vQueue = new VulkanQueue();
    	vLogicalDevice = new VulkanLogicalDevice();
    	vColor = new VulkanColor();
    	vSwapchain = new VulkanSwapchain();
    	vRenderPass = new VulkanRenderPass();
    	vShader = new VulkanShader();
    	vPipeline = new VulkanPipeline();
    	vFramebuffers = new VulkanFramebuffers();
    	vRenderCommandPool = new VulkanCommandPool();
    	vVertexBuffer = new VulkanVertexBuffer();
    	vSemaphore = new VulkanSemaphore();
    	
        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }
        if (!glfwVulkanSupported()) {
            throw new AssertionError("GLFW failed to find the Vulkan loader");
        }

        vInstance.init(VK_MAKE_VERSION(1, 0, 2));
        
        vPhysicalDevice.init(vInstance, VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU);
		System.out.println("Using GPU: " + vPhysicalDevice.getProperties().deviceNameString());
        final VkPhysicalDevice physicalDevice = vPhysicalDevice.get();
        
        window = createGlfwWindow();
		windowSurface = createGlfwWindowSurface();
		
		vQueue.init(vPhysicalDevice, windowSurface);
		vLogicalDevice.init(vPhysicalDevice, vQueue);
        
        final VkDevice device = vLogicalDevice.get();
        //TODO: not necessarily needed later
        final VkPhysicalDeviceMemoryProperties memoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
        vkGetPhysicalDeviceMemoryProperties(physicalDevice, memoryProperties);

//        GLFWKeyCallback keyCallback;
//        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
//            public void invoke(long window, int key, int scancode, int action, int mods) {
//                if (action != GLFW_RELEASE)
//                    return;
//                if (key == GLFW_KEY_ESCAPE)
//                    glfwSetWindowShouldClose(window, true);
//            }
//        });

        // Create static Vulkan resources
        vColor.init(vPhysicalDevice, windowSurface, VK_FORMAT_B8G8R8A8_UNORM);
        
        final VkQueue queue = vQueue.getGraphicsQueue();
        
        vRenderPass.init(vLogicalDevice, vColor);
        final long renderPass = vRenderPass.get();
        
        vShader.init(vLogicalDevice);
        
        vSwapchain.init(vPhysicalDevice, windowSurface, vQueue, vLogicalDevice, vColor, BASE_WIDTH, BASE_HEIGHT);
        swapchain = new Swapchain();
        swapchain.swapchainHandle = vSwapchain.get();
        swapchain.images = vSwapchain.imgBuffers;
        swapchain.imageViews = vSwapchain.getImageViews();
        
        vRenderCommandPool.init(vLogicalDevice, vQueue, vSwapchain.getImageCount());
        final long renderCommandPool = vRenderCommandPool.get();
        
        vPipeline.init(vLogicalDevice, vSwapchain, vShader, vRenderPass, vVertexBuffer);
        final long pipeline = vPipeline.getGraphicsPipeline();
        
        vFramebuffers.init(vLogicalDevice, vSwapchain, vRenderPass);
        framebuffers = vFramebuffers.get();

        // Handle canvas resize
        GLFWWindowSizeCallback windowSizeCallback = new GLFWWindowSizeCallback() {
            public void invoke(long window, int width, int height) {
                if (width <= 0 || height <= 0)
                    return;
                ColoredTriangleDemoTest.width = width;
                ColoredTriangleDemoTest.height = height;
            }
        };
        glfwSetWindowSizeCallback(window, windowSizeCallback);
        glfwShowWindow(window);

        vVertexBuffer.init(vPhysicalDevice, vLogicalDevice);
//        var polygon = new VulkanPolygon();
//        vVertexBuffer.writeVertecies(vLogicalDevice, polygon);
//        polygon.free();
        
        //TODO: change downwards
        var clearColor = VkClearValue.calloc(1);
		clearColor.color()
				.float32(0, 100/255f) //R
				.float32(1, 100/255f) //G
				.float32(2, 255/255f) //B
				.float32(3, 1f);  //A
        vRenderPass.linkRender(vSwapchain, vVertexBuffer, vPipeline, vFramebuffers, vRenderCommandPool, clearColor, width, height);
        renderCommandBuffers = new VkCommandBuffer[vSwapchain.getImageCount()];
        for (var i = 0; i < vSwapchain.getImageCount(); i++) {
        	renderCommandBuffers[i] = vRenderCommandPool.getCommandBuffer(i).get();
        }

        // Pre-allocate everything needed in the render loop

        IntBuffer pImageIndex = memAllocInt(1);
        int currentBuffer = 0;
        PointerBuffer pCommandBuffers = memAllocPointer(1);
        LongBuffer pSwapchains = memAllocLong(1);
        LongBuffer pImageAcquiredSemaphore = memAllocLong(1);
        LongBuffer pRenderCompleteSemaphore = memAllocLong(1);

        // Info struct to create a semaphore
        VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO)
                .pNext(NULL)
                .flags(VK_FLAGS_NONE);

        // Info struct to submit a command buffer which will wait on the semaphore
        IntBuffer pWaitDstStageMask = memAllocInt(1);
        pWaitDstStageMask.put(0, VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
        VkSubmitInfo submitInfo = VkSubmitInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                .pNext(NULL)
                .waitSemaphoreCount(pImageAcquiredSemaphore.remaining())
                .pWaitSemaphores(pImageAcquiredSemaphore)
                .pWaitDstStageMask(pWaitDstStageMask)
                .pCommandBuffers(pCommandBuffers)
                .pSignalSemaphores(pRenderCompleteSemaphore);

        // Info struct to present the current swapchain image to the display
        VkPresentInfoKHR presentInfo = VkPresentInfoKHR.calloc()
                .sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                .pNext(NULL)
                .pWaitSemaphores(pRenderCompleteSemaphore)
                .swapchainCount(pSwapchains.remaining())
                .pSwapchains(pSwapchains)
                .pImageIndices(pImageIndex)
                .pResults(null);
        
        var err = vkCreateSemaphore(device, semaphoreCreateInfo, null, pImageAcquiredSemaphore);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create image acquired semaphore: " + translateVulkanResult(err));
        }

        // Create a semaphore to wait for the render to complete, before presenting
        err = vkCreateSemaphore(device, semaphoreCreateInfo, null, pRenderCompleteSemaphore);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create render complete semaphore: " + translateVulkanResult(err));
        }
        
        pSwapchains.put(0, swapchain.swapchainHandle);

        // The render loop
        while (!glfwWindowShouldClose(window)) {
            // Handle window messages. Resize events happen exactly here.
            // So it is safe to use the new swapchain images and framebuffers afterwards.
            glfwPollEvents();

            // Create a semaphore to wait for the swapchain to acquire the next image
//            err = vkCreateSemaphore(device, semaphoreCreateInfo, null, pImageAcquiredSemaphore);
//            if (err != VK_SUCCESS) {
//                throw new AssertionError("Failed to create image acquired semaphore: " + translateVulkanResult(err));
//            }
//
//            // Create a semaphore to wait for the render to complete, before presenting
//            err = vkCreateSemaphore(device, semaphoreCreateInfo, null, pRenderCompleteSemaphore);
//            if (err != VK_SUCCESS) {
//                throw new AssertionError("Failed to create render complete semaphore: " + translateVulkanResult(err));
//            }

            // Get next image from the swap chain (back/front buffer).
            // This will setup the imageAquiredSemaphore to be signalled when the operation is complete
            err = vkAcquireNextImageKHR(device, swapchain.swapchainHandle, UINT64_MAX, pImageAcquiredSemaphore.get(0), VK_NULL_HANDLE, pImageIndex);
            currentBuffer = pImageIndex.get(0);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to acquire next swapchain image: " + translateVulkanResult(err));
            }

            // Select the command buffer for the current framebuffer image/attachment
            pCommandBuffers.put(0, renderCommandBuffers[currentBuffer]);

            // Submit to the graphics queue
            err = vkQueueSubmit(queue, submitInfo, VK_NULL_HANDLE);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to submit render queue: " + translateVulkanResult(err));
            }

            // Present the current buffer to the swap chain
//            // This will display the image
//            pSwapchains.put(0, swapchain.swapchainHandle);
            err = vkQueuePresentKHR(queue, presentInfo);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to present the swapchain image: " + translateVulkanResult(err));
            }
            // Create and submit post present barrier
//            vkQueueWaitIdle(queue);

            // Destroy this semaphore (we will create a new one in the next frame)
//            vkDestroySemaphore(device, pImageAcquiredSemaphore.get(0), null);
//            vkDestroySemaphore(device, pRenderCompleteSemaphore.get(0), null);
//            submitPostPresentBarrier(swapchain.images[currentBuffer], postPresentCommandBuffer, queue);
        }
        presentInfo.free();
        memFree(pWaitDstStageMask);
        submitInfo.free();
        memFree(pImageAcquiredSemaphore);
        memFree(pRenderCompleteSemaphore);
        semaphoreCreateInfo.free();
        memFree(pSwapchains);
        memFree(pCommandBuffers);

        windowSizeCallback.free();
//        keyCallback.free();
        glfwDestroyWindow(window);
        glfwTerminate();

        // We don't bother disposing of all Vulkan resources.
        // Let the OS process manager take care of it.
        
        vSemaphore.cleanUp(vLogicalDevice);
        vVertexBuffer.cleanUp(vLogicalDevice);
        vRenderCommandPool.cleanUp(vLogicalDevice);
        vFramebuffers.cleanUp(vLogicalDevice);
        vPipeline.cleanUp(vLogicalDevice);
        vShader.cleanUp(vLogicalDevice);
        vRenderPass.cleanUp(vLogicalDevice);
        vSwapchain.cleanUp(vLogicalDevice);
        vColor.cleanUp();
        vLogicalDevice.cleanUp();
        vQueue.cleanUp();
        vPhysicalDevice.cleanUp();
        vInstance.cleanUp();
    }

    public static String translateVulkanResult(int result) {
        switch (result) {
        // Success codes
        case VK_SUCCESS:
            return "Command successfully completed.";
        case VK_NOT_READY:
            return "A fence or query has not yet completed.";
        case VK_TIMEOUT:
            return "A wait operation has not completed in the specified time.";
        case VK_EVENT_SET:
            return "An event is signaled.";
        case VK_EVENT_RESET:
            return "An event is unsignaled.";
        case VK_INCOMPLETE:
            return "A return array was too small for the result.";
        case VK_SUBOPTIMAL_KHR:
            return "A swapchain no longer matches the surface properties exactly, but can still be used to present to the surface successfully.";

            // Error codes
        case VK_ERROR_OUT_OF_HOST_MEMORY:
            return "A host memory allocation has failed.";
        case VK_ERROR_OUT_OF_DEVICE_MEMORY:
            return "A device memory allocation has failed.";
        case VK_ERROR_INITIALIZATION_FAILED:
            return "Initialization of an object could not be completed for implementation-specific reasons.";
        case VK_ERROR_DEVICE_LOST:
            return "The logical or physical device has been lost.";
        case VK_ERROR_MEMORY_MAP_FAILED:
            return "Mapping of a memory object has failed.";
        case VK_ERROR_LAYER_NOT_PRESENT:
            return "A requested layer is not present or could not be loaded.";
        case VK_ERROR_EXTENSION_NOT_PRESENT:
            return "A requested extension is not supported.";
        case VK_ERROR_FEATURE_NOT_PRESENT:
            return "A requested feature is not supported.";
        case VK_ERROR_INCOMPATIBLE_DRIVER:
            return "The requested version of Vulkan is not supported by the driver or is otherwise incompatible for implementation-specific reasons.";
        case VK_ERROR_TOO_MANY_OBJECTS:
            return "Too many objects of the type have already been created.";
        case VK_ERROR_FORMAT_NOT_SUPPORTED:
            return "A requested format is not supported on this device.";
        case VK_ERROR_SURFACE_LOST_KHR:
            return "A surface is no longer available.";
        case VK_ERROR_NATIVE_WINDOW_IN_USE_KHR:
            return "The requested window is already connected to a VkSurfaceKHR, or to some other non-Vulkan API.";
        case VK_ERROR_OUT_OF_DATE_KHR:
            return "A surface has changed in such a way that it is no longer compatible with the swapchain, and further presentation requests using the "
                    + "swapchain will fail. Applications must query the new surface properties and recreate their swapchain if they wish to continue" + "presenting to the surface.";
        case VK_ERROR_INCOMPATIBLE_DISPLAY_KHR:
            return "The display used by a swapchain does not use the same presentable image layout, or is incompatible in a way that prevents sharing an" + " image.";
        case VK_ERROR_VALIDATION_FAILED_EXT:
            return "A validation layer found an error.";
        default:
            return String.format("%s [%d]", "Unknown", Integer.valueOf(result));
        }
    }
    
    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param resource   the resource to read
     * @param bufferSize the initial buffer size
     *
     * @return the resource data
     *
     * @throws IOException if an IO error occurs
     */
    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        File file = new File(url.getFile());
        if (file.isFile()) {
            FileInputStream fis = new FileInputStream(file);
            FileChannel fc = fis.getChannel();
            buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            fc.close();
            fis.close();
        } else {
            buffer = BufferUtils.createByteBuffer(bufferSize);
            InputStream source = url.openStream();
            if (source == null)
                throw new FileNotFoundException(resource);
            try {
                byte[] buf = new byte[8192];
                while (true) {
                    int bytes = source.read(buf, 0, buf.length);
                    if (bytes == -1)
                        break;
                    if (buffer.remaining() < bytes)
                        buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                    buffer.put(buf, 0, bytes);
                }
                buffer.flip();
            } finally {
                source.close();
            }
        }
        return buffer;
    }
    
    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
    
    private static long createGlfwWindow() {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		var window = glfwCreateWindow(BASE_WIDTH, BASE_HEIGHT, SystemConfiguration.GAME_NAME.getConfiguration(), NULL, NULL);

		return window;
	}
	
	private static long createGlfwWindowSurface() throws VulkanException {
		var pSurface = memAllocLong(1);
		var res = glfwCreateWindowSurface(vInstance.get(), window, null, pSurface);

		try {
			if (res != VK_SUCCESS) {
				throw new VulkanException(res);
			}
			
			var surface = pSurface.get(0);
			return surface;
		} finally {
			memFree(pSurface);
		}
	}
}