package ch.megil.teliaengine.vulkanui;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import ch.megil.teliaengine.game.GameElement;
import ch.megil.teliaengine.game.Vector;
import ch.megil.teliaengine.vulkan.VulkanIndexBuffer;
import ch.megil.teliaengine.vulkan.VulkanVertexBuffer;
import ch.megil.teliaengine.vulkan.obj.VulkanObject;

public abstract class VulkanElement extends VulkanObject {
	private static final int SCALE_MODIFIER = 2;
	private static final Vector VULKAN_OFFSET = new Vector(-1, -1);
	
	public static final int VERTECIES_PER_OBJECT = 4;
	public static final int INDICIES_PER_OBJECT = 5;
	
	private static final short INDEX_RESET = (short) 0xFFFF;
	private static final int INDEX_TL = 0;
	private static final int INDEX_TR = 1;
	private static final int INDEX_BR = 2;
	private static final int INDEX_BL = 3;
	
	private Vector scaleVector;
	private int numberOfVertecies;
	private int numberOfIndecies;
	
	public VulkanElement(int numberOfObjects, double spreadWidth, double spreadHeight, Vector cameraOffset) {
		super(VulkanVertexBuffer.VERTEX_SIZE * numberOfObjects * VERTECIES_PER_OBJECT,
				VulkanIndexBuffer.INDEX_SIZE * numberOfObjects * INDICIES_PER_OBJECT);
		scaleVector = new Vector(SCALE_MODIFIER/spreadWidth, SCALE_MODIFIER/spreadHeight);
		numberOfVertecies = numberOfObjects * VERTECIES_PER_OBJECT;
		numberOfIndecies = numberOfObjects * INDICIES_PER_OBJECT;
	}
	
	protected void convertElement(FloatBuffer vertexBuffer, ShortBuffer indexBuffer, GameElement element, int indexOffset) {
		var topLeft = element.getPosition().multiplyByComponent(scaleVector).add(VULKAN_OFFSET);
		var bottomRigh = element.getPosition()
				.add(new Vector(element.getDepiction().getWidth(), element.getDepiction().getHeight()))
				.multiplyByComponent(scaleVector)
				.add(VULKAN_OFFSET);
		
		var color = element.getColor();
		vertexBuffer.put((float) topLeft.getX())   .put((float) topLeft.getY())   .put((float) color.getRed()).put((float) color.getGreen()).put((float) color.getBlue());
		vertexBuffer.put((float) bottomRigh.getX()).put((float) topLeft.getY())   .put((float) color.getRed()).put((float) color.getGreen()).put((float) color.getBlue());
		vertexBuffer.put((float) bottomRigh.getX()).put((float) bottomRigh.getY()).put((float) color.getRed()).put((float) color.getGreen()).put((float) color.getBlue());
		vertexBuffer.put((float) topLeft.getX())   .put((float) bottomRigh.getY()).put((float) color.getRed()).put((float) color.getGreen()).put((float) color.getBlue());
		
		indexBuffer.put((short) (indexOffset+INDEX_TL))
			.put((short) (indexOffset+INDEX_BL))
			.put((short) (indexOffset+INDEX_TR))
			.put((short) (indexOffset+INDEX_BR))
			.put(INDEX_RESET);
	}

	public int getNumberOfVertecies() {
		return numberOfVertecies;
	}
	
	public int getNumberOfIndecies() {
		return numberOfIndecies;
	}
}
