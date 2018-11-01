package ch.megil.teliaengine.vulkan.exception;

import static org.lwjgl.vulkan.VK10.VK_ERROR_INCOMPATIBLE_DRIVER;

public class VulkanException extends Exception {
	private static final long serialVersionUID = -8187611104093516686L;

	public VulkanException(String message) {
		super(message);
	}
	
	public VulkanException(int resultCode) {
		this(translateVulkanResult(resultCode));
	}
	
	private static String translateVulkanResult(int result) {
        switch (result) {
	        case VK_ERROR_INCOMPATIBLE_DRIVER:
	            return "Cannot find a compatible Vulkan Interface";
	        default:
	            return String.format("Unknown Error [%d]", result);
        }
    }
}
