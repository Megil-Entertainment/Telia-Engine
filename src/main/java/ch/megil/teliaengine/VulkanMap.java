package ch.megil.teliaengine;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import ch.megil.teliaengine.game.GameObject;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.game.Vector;
import ch.megil.teliaengine.vulkan.VulkanIndexBuffer;
import ch.megil.teliaengine.vulkan.VulkanVertexBuffer;
import ch.megil.teliaengine.vulkan.obj.VulkanObject;

public class VulkanMap extends VulkanObject {
	private static final int SCALE_MODIFIER = 2;
	private static final Vector VULKAN_OFFSET = new Vector(-1, -1);
	
	private static final int VERTECIES_PER_OBJECT = 4;
	private static final int INDICIES_PER_OBJECT = 5;
	
	private static final short INDEX_RESET = (short) 0xFFFF;
	private static final int INDEX_TL = 0;
	private static final int INDEX_TR = 1;
	private static final int INDEX_BR = 2;
	private static final int INDEX_BL = 3;
	
	private Vector scaleVector;
	private int numberOfVertecies;
	private int numberOfIndecies;
	
	public VulkanMap(Map map) {
		super(VulkanVertexBuffer.VERTEX_SIZE * map.getMapObjects().size() * VERTECIES_PER_OBJECT,
				VulkanIndexBuffer.INDEX_SIZE * map.getMapObjects().size() * INDICIES_PER_OBJECT);
		numberOfVertecies = map.getMapObjects().size() * VERTECIES_PER_OBJECT;
		numberOfIndecies = map.getMapObjects().size() * INDICIES_PER_OBJECT;
		
		scaleVector = new Vector(SCALE_MODIFIER/map.getWidth(), SCALE_MODIFIER/map.getHeight());
		
		var vertexBuffer = vertecies.asFloatBuffer();
		var indexBuffer = indicies.asShortBuffer();
		
		for (var i = 0; i < map.getMapObjects().size(); i++) {
			convertObject(vertexBuffer, indexBuffer, map.getMapObjects().get(i), i*VERTECIES_PER_OBJECT);
		}
	}
	
	private void convertObject(FloatBuffer vertexBuffer, ShortBuffer indexBuffer, GameObject obj, int offset) {
		var topLeft = obj.getPosition().multiplyByComponent(scaleVector).add(VULKAN_OFFSET);
		var bottomRigh = obj.getPosition().add(obj.getHitbox().getVectorSize()).multiplyByComponent(scaleVector).add(VULKAN_OFFSET);
		
		vertexBuffer.put((float) topLeft.getX())   .put((float) topLeft.getY())   .put(0.0f).put(0.0f).put(0.0f);
		vertexBuffer.put((float) bottomRigh.getX()).put((float) topLeft.getY())   .put(0.0f).put(0.0f).put(0.0f);
		vertexBuffer.put((float) bottomRigh.getX()).put((float) bottomRigh.getY()).put(0.0f).put(0.0f).put(0.0f);
		vertexBuffer.put((float) topLeft.getX())   .put((float) bottomRigh.getY()).put(0.0f).put(0.0f).put(0.0f);
		
		indexBuffer.put((short) (offset+INDEX_TL))
			.put((short) (offset+INDEX_BL))
			.put((short) (offset+INDEX_TR))
			.put((short) (offset+INDEX_BR))
			.put(INDEX_RESET);
	}
	
	public int getNumberOfVertecies() {
		return numberOfVertecies;
	}
	
	public int getNumberOfIndecies() {
		return numberOfIndecies;
	}
}
