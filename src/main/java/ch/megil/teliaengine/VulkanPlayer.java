package ch.megil.teliaengine;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.game.Vector;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.vulkan.VulkanIndexBuffer;
import ch.megil.teliaengine.vulkan.VulkanVertexBuffer;
import ch.megil.teliaengine.vulkan.obj.VulkanObject;

public class VulkanPlayer extends VulkanObject {
	private static final int SCALE_MODIFIER = 2;
	private static final Vector VULKAN_OFFSET = new Vector(-1, -1);
	
	private static final short INDEX_RESET = (short) 0xFFFF;
	private static final int INDEX_TL = 0;
	private static final int INDEX_TR = 1;
	private static final int INDEX_BR = 2;
	private static final int INDEX_BL = 3;
	
	private Vector scaleVector;
	public static final int NUMBER_OF_VERTECIES = 4;
	public static final int NUMBER_OF_INDECIES = 5;
	
	public VulkanPlayer(Player player, Map map) {
		this(player, map, 0);
	}
	
	public VulkanPlayer(Player player, Map map, int indexOffset) {
		super(VulkanVertexBuffer.VERTEX_SIZE*NUMBER_OF_VERTECIES, VulkanIndexBuffer.INDEX_SIZE*NUMBER_OF_INDECIES);
		
		scaleVector = new Vector(SCALE_MODIFIER/map.getWidth(), SCALE_MODIFIER/map.getHeight());
		
		var vertexBuffer = vertecies.asFloatBuffer();
		var indexBuffer = indicies.asShortBuffer();

		convertPlayer(vertexBuffer, indexBuffer, player, indexOffset);
	}
	
	private void convertPlayer(FloatBuffer vertexBuffer, ShortBuffer indexBuffer, Player player, int indexOffset) {
		var topLeft = player.getPosition().multiplyByComponent(scaleVector).add(VULKAN_OFFSET);
		var bottomRigh = player.getPosition().add(player.getHitbox().getVectorSize()).multiplyByComponent(scaleVector).add(VULKAN_OFFSET);
		
		vertexBuffer.put((float) topLeft.getX())   .put((float) topLeft.getY())   .put(0.0f).put(0.0f).put(0.0f);
		vertexBuffer.put((float) bottomRigh.getX()).put((float) topLeft.getY())   .put(0.0f).put(0.0f).put(0.0f);
		vertexBuffer.put((float) bottomRigh.getX()).put((float) bottomRigh.getY()).put(0.0f).put(0.0f).put(0.0f);
		vertexBuffer.put((float) topLeft.getX())   .put((float) bottomRigh.getY()).put(0.0f).put(0.0f).put(0.0f);
		
		indexBuffer.put((short) (indexOffset+INDEX_TL))
			.put((short) (indexOffset+INDEX_BL))
			.put((short) (indexOffset+INDEX_TR))
			.put((short) (indexOffset+INDEX_BR))
			.put(INDEX_RESET);
	}
}
