package ch.megil.teliaengine.vulkanui;

import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.game.player.Player;

public class VulkanPlayer extends VulkanElement {
	private static final int SINGLE_OBJECT = 1;
	
	public VulkanPlayer(Player player, Map map) {
		this(player, map, 0);
	}
	
	public VulkanPlayer(Player player, Map map, int indexOffset) {
		super(SINGLE_OBJECT, map.getWidth(), map.getHeight());
		
		var vertexBuffer = vertecies.asFloatBuffer();
		var indexBuffer = indicies.asShortBuffer();

		convertElement(vertexBuffer, indexBuffer, player, indexOffset);
	}
}
