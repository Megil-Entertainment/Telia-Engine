package ch.megil.teliaengine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkAcquireNextImageKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkQueuePresentKHR;
import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkSubmitInfo;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.file.MapFileManager;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.gamelogic.GameLoop;
import ch.megil.teliaengine.gamelogic.GameState;
import ch.megil.teliaengine.vulkan.*;
import ch.megil.teliaengine.vulkan.buffer.VulkanIndexBuffer;
import ch.megil.teliaengine.vulkan.buffer.VulkanVertexBuffer;
import ch.megil.teliaengine.vulkan.command.VulkanCommandPool;
import ch.megil.teliaengine.vulkan.exception.VulkanException;
import ch.megil.teliaengine.vulkan.file.VulkanTextureLoader;
import ch.megil.teliaengine.vulkanui.VulkanMap;
import ch.megil.teliaengine.vulkanui.VulkanPlayer;

public class GameMain {
	private static final int VK_VERSION = VK_MAKE_VERSION(1, 0, 2);
	
	private static final long UINT64_MAX = -1;
	
	private static final int SEM_NUM_OF_SEM = 2;
	private static final int SEM_IMAGE_AVAILABLE = 0;
	private static final int SEM_RENDER_FINISHED = 1;
	
	private static final int BASE_WIDTH = 1280;
	private static final int BASE_HEIGHT = 720;
	
	private static final float CLEAR_R = 255.0f/255.0f;
	private static final float CLEAR_G = 255.0f/255.0f;
	private static final float CLEAR_B = 255.0f/255.0f;
	private static final float CLEAR_A = 1.0f;
	
	private long window;
	private long windowSurface;
	
	private VulkanInstance instance;
	private VulkanPhysicalDevice physicalDevice;
	private VulkanQueue queue;
	private VulkanLogicalDevice logicalDevice;
	private VulkanColor color;
	private VulkanSwapchain swapchain;
	private VulkanRenderPass renderPass;
	private VulkanShader shader;
	private VulkanSampler sampler;
	private VulkanFramebuffers framebuffers;
	private VulkanCommandPool renderCommandPool;
	private VulkanCommandPool singleCommandPool;
	private VulkanSemaphore semaphore;
	private VulkanDescriptor descriptor;
	private VulkanDescriptorUpdater descriptorUpdater;
	private VulkanPipeline pipeline;
	private VulkanVertexBuffer vertexBuffer;
	private VulkanIndexBuffer indexBuffer;
	
	private VulkanTextureLoader textureLoader;
	
	private VulkanMap map;
	private VulkanPlayer player;
	
	public GameMain() {
		GameState.get().setMap(new Map(BASE_WIDTH, BASE_HEIGHT));
		
		instance = new VulkanInstance();
		physicalDevice = new VulkanPhysicalDevice();
		queue = new VulkanQueue();
		logicalDevice = new VulkanLogicalDevice();
		color = new VulkanColor();
		swapchain = new VulkanSwapchain();
		renderPass = new VulkanRenderPass();
		shader = new VulkanShader();
		sampler = new VulkanSampler();
		framebuffers = new VulkanFramebuffers();
		renderCommandPool = new VulkanCommandPool();
		singleCommandPool = new VulkanCommandPool();
		semaphore = new VulkanSemaphore();
		
		descriptor = new VulkanDescriptor();
		descriptorUpdater = new VulkanDescriptorUpdater();
		pipeline = new VulkanPipeline();
		vertexBuffer = new VulkanVertexBuffer();
		indexBuffer = new VulkanIndexBuffer();
		
		textureLoader = new VulkanTextureLoader();
	}
	
	public GameMain(String mapName) throws AssetNotFoundException, AssetFormatException {
		this();
		GameState.get().setMap(new MapFileManager().load(mapName, false));
	}

	public void run() throws IllegalStateException, VulkanException, AssetNotFoundException {
		if (instance.get() != null) {
			throw new IllegalStateException("Vulkan is already completly or partialy initialized. Use cleanUp first.");
		}
		
		try {
			init();
			initMap(GameState.get().getMap(), Player.get());
			
			GameLoop.get().start();
			
			initKeyhandling();
			loop();
		} finally {
			cleanUpMap();
			cleanUp();
			GameLoop.get().stop();
		}
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
		
		window = createGlfwWindow();
		windowSurface = createGlfwWindowSurface();
		
		queue.init(physicalDevice, windowSurface);
		logicalDevice.init(physicalDevice, queue);
		color.init(physicalDevice, windowSurface, VK_FORMAT_R32G32B32A32_SFLOAT);
		swapchain.init(physicalDevice, windowSurface, queue, logicalDevice, color, BASE_WIDTH, BASE_HEIGHT);
		renderPass.init(logicalDevice, color);
		shader.init(logicalDevice);
		sampler.init(logicalDevice);
		framebuffers.init(logicalDevice, swapchain, renderPass);
		renderCommandPool.init(logicalDevice, queue, swapchain.getImageCount());
		singleCommandPool.init(logicalDevice, queue);
		semaphore.init(logicalDevice, SEM_NUM_OF_SEM);
		
		glfwShowWindow(window);
	}
	
	private void initMap(Map mapObj, Player playerObj) throws VulkanException, AssetNotFoundException {
		descriptor.init(logicalDevice);
		descriptorUpdater.init(sampler, descriptor);
		pipeline.init(logicalDevice, swapchain, shader, renderPass, vertexBuffer, descriptor);
		
		textureLoader.loadAndUpdateGameElementTexture(physicalDevice, logicalDevice, queue, singleCommandPool, descriptorUpdater, playerObj);
		for (var element : mapObj.getMapObjects()) {
			textureLoader.loadAndUpdateGameElementTexture(physicalDevice, logicalDevice, queue, singleCommandPool, descriptorUpdater, element);
		}
		
		map = new VulkanMap(GameState.get().getMap(), Player.get().getPosition());
		player = new VulkanPlayer(Player.get(), map.getNumberOfVertecies(), Player.get().getPosition());
		
		try {
			vertexBuffer.init(physicalDevice, logicalDevice, map.getNumberOfVertecies() + player.getNumberOfVertecies(), new int[] {queue.getGraphicsFamily()});
			indexBuffer.init(physicalDevice, logicalDevice, map.getNumberOfIndecies() + player.getNumberOfIndecies(), new int[] {queue.getGraphicsFamily()});
			
			descriptorUpdater.updateDescriptor(logicalDevice);
			
			vertexBuffer.writeVertecies(logicalDevice, map);
			indexBuffer.writeIndicies(logicalDevice, map);
			vertexBuffer.writeVertecies(logicalDevice, player, map.getNumberOfVertecies());
			indexBuffer.writeIndicies(logicalDevice, player, map.getNumberOfIndecies());
		} finally {
			player.free();
			map.free();
		}
	}
	
	private void initKeyhandling() {
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			GameLoop.get().getInputHandler().registerKeyAction(key, action);
		});
	}
	
	private void loop() throws VulkanException {
		var clearColor = VkClearValue.calloc(1);
		clearColor.color()
				.float32(0, CLEAR_R)
				.float32(1, CLEAR_G)
				.float32(2, CLEAR_B)
				.float32(3, CLEAR_A);
		
		try {
			renderPass.linkRender(swapchain, pipeline, framebuffers, renderCommandPool, vertexBuffer, indexBuffer, descriptor, clearColor, BASE_WIDTH, BASE_HEIGHT);
		} finally {
			clearColor.free();
		}
		
		var pImageIndex = memAllocInt(1);
		var pRenderCommandBuffer = memAllocPointer(1);
		var pSwapchain = memAllocLong(1);
		pSwapchain.put(0, swapchain.get());
		
		var waitStage = memAllocInt(1);
		waitStage.put(0, VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
		var submitInfo = VkSubmitInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
				.pWaitSemaphores(semaphore.getPointer(SEM_IMAGE_AVAILABLE))
				.pWaitDstStageMask(waitStage)
				.pCommandBuffers(pRenderCommandBuffer)
				.pSignalSemaphores(semaphore.getPointer(SEM_RENDER_FINISHED));
		VkSubmitInfo.nwaitSemaphoreCount(submitInfo.address(), 1);
		VkSubmitInfo.ncommandBufferCount(submitInfo.address(), pRenderCommandBuffer.capacity());
		VkSubmitInfo.nsignalSemaphoreCount(submitInfo.address(), 1);

		var presentInfo = VkPresentInfoKHR.calloc()
				.sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
				.pWaitSemaphores(semaphore.getPointer(SEM_RENDER_FINISHED))
				.pSwapchains(pSwapchain)
				.pImageIndices(pImageIndex)
				.pResults(null);
		VkPresentInfoKHR.nwaitSemaphoreCount(presentInfo.address(), 1);
		VkPresentInfoKHR.nswapchainCount(presentInfo.address(), pSwapchain.capacity());
		
		try {
			while(!glfwWindowShouldClose(window)) {
				glfwPollEvents();
				
				map = new VulkanMap(GameState.get().getMap(), Player.get().getPosition());
				player = new VulkanPlayer(Player.get(), Player.get().getPosition());
				vertexBuffer.writeVertecies(logicalDevice, map);
				vertexBuffer.writeVertecies(logicalDevice, player, map.getNumberOfVertecies());
				player.free();
				map.free();
				
				vkAcquireNextImageKHR(logicalDevice.get(), swapchain.get(), UINT64_MAX, semaphore.get(SEM_IMAGE_AVAILABLE), VK_NULL_HANDLE, pImageIndex);
				var imageIndex = pImageIndex.get(0);
				
				pRenderCommandBuffer.put(0, renderCommandPool.getCommandBuffer(imageIndex).get());
				
				var res = vkQueueSubmit(queue.getGraphicsQueue(), submitInfo, VK_NULL_HANDLE);
				if (res != VK_SUCCESS) {
					throw new VulkanException(res);
				}
				
				res = vkQueuePresentKHR(queue.getPresentQueue(), presentInfo);
				if (res != VK_SUCCESS) {
					throw new VulkanException(res);
				}
				vkQueueWaitIdle(queue.getPresentQueue());
			}
			vkDeviceWaitIdle(logicalDevice.get());
		} finally {
			presentInfo.free();
			submitInfo.free();
			memFree(pRenderCommandBuffer);
			memFree(waitStage);
			memFree(pImageIndex);
		}
	}
	
	public void cleanUpMap() {
		// Destroy bottom up		
		indexBuffer.cleanUp(logicalDevice);
		vertexBuffer.cleanUp(logicalDevice);
		textureLoader.cleanUp(logicalDevice);
		pipeline.cleanUp(logicalDevice);
		descriptorUpdater.cleanUp();
		descriptor.cleanUp(logicalDevice);
	}
	
	public void cleanUp() {
		// Destroy bottom up
		semaphore.cleanUp(logicalDevice);
		singleCommandPool.cleanUp(logicalDevice);
		renderCommandPool.cleanUp(logicalDevice);
		framebuffers.cleanUp(logicalDevice);
		sampler.cleanUp(logicalDevice);
		shader.cleanUp(logicalDevice);
		renderPass.cleanUp(logicalDevice);
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
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		//TODO fullscreen?
//		var window = glfwCreateWindow(BASE_WIDTH, BASE_HEIGHT, SystemConfiguration.GAME_NAME.getConfiguration(), glfwGetPrimaryMonitor(), NULL);
		var window = glfwCreateWindow(BASE_WIDTH, BASE_HEIGHT, SystemConfiguration.GAME_NAME.getConfiguration(), NULL, NULL);

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
