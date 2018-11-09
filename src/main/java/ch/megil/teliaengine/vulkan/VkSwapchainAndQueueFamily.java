package ch.megil.teliaengine.vulkan;

public class VkSwapchainAndQueueFamily {
	private int graphicsQueue;
	private int presentQueue;
	
	public VkSwapchainAndQueueFamily(int graphicsQueue, int presentQueue) {
		this.graphicsQueue = graphicsQueue;
		this.presentQueue = presentQueue;
	}
	
	public int getGraphicsQueue() {
		return graphicsQueue;
	}
	
	public int getPresentQueue() {
		return presentQueue;
	}
}
