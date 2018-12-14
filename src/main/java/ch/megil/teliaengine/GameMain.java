package ch.megil.teliaengine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.KHRSwapchain.vkAcquireNextImageKHR;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.IntBuffer;

import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkSubmitInfo;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.file.MapSaveLoad;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.gamelogic.GameState;
import ch.megil.teliaengine.vulkan.*;
import ch.megil.teliaengine.vulkan.exception.VulkanException;
import ch.megil.teliaengine.vulkan.obj.VulkanPolygon;

public class GameMain {
	private static final int VK_VERSION = VK_MAKE_VERSION(1, 0, 2);
	
	private static final long UINT64_MAX = -1;
	
	private static final int SEM_NUM_OF_SEM = 2;
	private static final int SEM_IMAGE_AVAILABLE = 0;
	private static final int SEM_RENDER_FINISHED = 1;
	
	private long window;
	private long windowSurface;
	
	private VulkanInstance instance;
	private VulkanPhysicalDevice physicalDevice;
	private VulkanQueue queue;
	private VulkanLogicalDevice logicalDevice;
	private VulkanColor color;
	private VulkanSwapchain swapchain;
	private VulkanShader shader;
	private VulkanRenderPass renderPass;
	private VulkanVertexBuffer vertexBuffer;
	private VulkanPipeline pipeline;
	private VulkanFramebuffers framebuffers;
	private VulkanCommandPool renderCommandPool;
	private VulkanSemaphore semaphore;
	
	public GameMain() {
		instance = new VulkanInstance();
		physicalDevice = new VulkanPhysicalDevice();
		queue = new VulkanQueue();
		logicalDevice = new VulkanLogicalDevice();
		color = new VulkanColor();
		swapchain = new VulkanSwapchain();
		shader = new VulkanShader();
		renderPass = new VulkanRenderPass();
		vertexBuffer = new VulkanVertexBuffer();
		pipeline = new VulkanPipeline();
		framebuffers = new VulkanFramebuffers();
		renderCommandPool = new VulkanCommandPool();
		semaphore = new VulkanSemaphore();
	}
	
	public GameMain(String mapName) throws AssetNotFoundException, AssetFormatException {
		this();
		GameState.get().setMap(new MapSaveLoad().load(mapName, false));
	}

	public void run() throws IllegalStateException, VulkanException {
		if (instance.get() != null) {
			throw new IllegalStateException("Vulkan is already completly or partialy initialized. Use cleanUp first.");
		}
		
		try {
			init();
			loop();
		} finally {
			cleanUp();
		}
		
		//GameLoop.get().start();
		//primaryStage.setOnHidden(e -> GameLoop.get().stop());
	}
	
	private void init() throws VulkanException {
		if (!glfwInit()) {
			throw new RuntimeException("Unable to initialize GLFW.");
		}
		if (!glfwVulkanSupported()) {
			throw new RuntimeException("Vulkan loader could not be found.");
		}
		
		instance.init(VK_VERSION);
		physicalDevice.init(instance, VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU);
		
		System.out.println("Using GPU: " + physicalDevice.getProperties().deviceNameString());
		
		window = createGlfwWindow();
		windowSurface = createGlfwWindowSurface();
		
		queue.init(physicalDevice, windowSurface);
		logicalDevice.init(physicalDevice, queue);
		color.init(physicalDevice, windowSurface, VK_FORMAT_B8G8R8A8_UNORM);
		swapchain.init(physicalDevice, windowSurface, queue, logicalDevice, color);
		shader.init(logicalDevice);
		renderPass.init(logicalDevice, color);
		vertexBuffer.init(physicalDevice, logicalDevice);
		pipeline.init(logicalDevice, swapchain, shader, renderPass, vertexBuffer);
		framebuffers.init(logicalDevice, swapchain, renderPass);
		renderCommandPool.init(logicalDevice, queue, swapchain.getImageCount());
		
		var clearColor = VkClearValue.calloc(1);
		clearColor.color()
				.float32(0, 255f) //R
				.float32(1, 255f) //G
				.float32(2, 255f) //B
				.float32(3, 1f);  //A
		try {
			renderPass.linkRender(swapchain, pipeline, framebuffers, renderCommandPool, clearColor);
		} finally {
			clearColor.free();
		}
		
		semaphore.init(logicalDevice, SEM_NUM_OF_SEM);
		
		glfwShowWindow(window);
	}
	
	private void loop() throws VulkanException {
		var pImageIndex = memAllocInt(1);
		
		var waitStage = memAllocInt(1)
				.put(0, VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
		var pRenderCommandBuffer = memAllocPointer(1);
		var submitInfo = VkSubmitInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
				.waitSemaphoreCount(1)
				.pWaitSemaphores(semaphore.getPointer(SEM_IMAGE_AVAILABLE))
				.pWaitDstStageMask(waitStage)
				.pCommandBuffers(pRenderCommandBuffer)
				.pSignalSemaphores(semaphore.getPointer(SEM_RENDER_FINISHED));
		VkSubmitInfo.ncommandBufferCount(submitInfo.address(), pRenderCommandBuffer.capacity());
		VkSubmitInfo.nsignalSemaphoreCount(submitInfo.address(), semaphore.getPointer(SEM_RENDER_FINISHED).capacity());
		
		try {
			while(!glfwWindowShouldClose(window)) {
				System.out.println(swapchain.getImageCount());
				glfwPollEvents();
				
				vkAcquireNextImageKHR(logicalDevice.get(), swapchain.get(), UINT64_MAX, semaphore.get(SEM_IMAGE_AVAILABLE), VK_NULL_HANDLE, pImageIndex);
				var imageIndex = pImageIndex.get(0);
				
				pRenderCommandBuffer.put(0, renderCommandPool.getCommandBuffer(imageIndex).get());
				
				var polygon = new VulkanPolygon();
				vertexBuffer.writeVertecies(logicalDevice, polygon);
				polygon.free();
				
				var res = vkQueueSubmit(queue.getGraphicsQueue(), submitInfo, VK_NULL_HANDLE);
				if (res != VK_SUCCESS) {
					throw new VulkanException(res);
				}
			}
		} finally {
			submitInfo.free();
			memFree(pRenderCommandBuffer);
			memFree(waitStage);
			memFree(pImageIndex);
		}
	}
	
	public void cleanUp() {
		// Destroy bottom up
		semaphore.cleanUp(logicalDevice);
		renderCommandPool.cleanUp(logicalDevice);
		framebuffers.cleanUp(logicalDevice);
		pipeline.cleanUp(logicalDevice);
		vertexBuffer.cleanUp(logicalDevice);
		renderPass.cleanUp(logicalDevice);
		shader.cleanUp(logicalDevice);
		swapchain.cleanUp(logicalDevice);
		color.cleanUp();
		logicalDevice.cleanUp();
		queue.cleanUp();
		
		//TODO: check if there is a possibility to destroy surface
		glfwDestroyWindow(window);
		
		physicalDevice.cleanUp();
		instance.cleanUp();
		
		glfwTerminate();
	}
	
	private long createGlfwWindow() {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		//TODO fullscreen?
//		var window = glfwCreateWindow(1920, 1080, SystemConfiguration.GAME_NAME.getConfiguration(), glfwGetPrimaryMonitor(), NULL);
		var window = glfwCreateWindow(1280, 720, SystemConfiguration.GAME_NAME.getConfiguration(), NULL, NULL);

		return window;
	}
	
	private long createGlfwWindowSurface() throws VulkanException {
		var pSurface = memAllocLong(1);
		var res = glfwCreateWindowSurface(instance.get(), window, null, pSurface);

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

	public static void main(String[] args) throws Exception {
		new GameMain().run();
	}
}
