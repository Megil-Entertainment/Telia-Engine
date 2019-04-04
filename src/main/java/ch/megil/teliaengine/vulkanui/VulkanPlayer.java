package ch.megil.teliaengine.vulkanui;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.game.player.Player;

public class VulkanPlayer extends VulkanElement {
	private static final int SINGLE_OBJECT = 1;
	
	public VulkanPlayer(Player player) {
		this(player, 0);
	}
	
	public VulkanPlayer(Player player, int indexOffset) {
		super(SINGLE_OBJECT, Double.parseDouble(GameConfiguration.MAP_WIDTH.getConfiguration()), 
				Double.parseDouble(GameConfiguration.MAP_HEIGHT.getConfiguration()), player.getPosition());
		
		var vertexBuffer = vertecies.asFloatBuffer();
		var indexBuffer = indicies.asShortBuffer();

		convertElement(vertexBuffer, indexBuffer, player, indexOffset);
	}
}
