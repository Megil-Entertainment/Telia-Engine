package ch.megil.teliaengine.vulkanui;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.game.player.Player;
import ch.megil.teliaengine.physics.Vector;

public class VulkanPlayer extends VulkanElement {
	private static final int SINGLE_OBJECT = 1;
	
	public VulkanPlayer(Player player, Vector cameraPosition) {
		this(player, 0, cameraPosition);
	}
	
	public VulkanPlayer(Player player, int indexOffset, Vector cameraPosition) {
		super(SINGLE_OBJECT, GameConfiguration.MAP_WIDTH.getConfiguration(), 
				GameConfiguration.MAP_HEIGHT.getConfiguration(), cameraPosition);
		
		var vertexBuffer = vertecies.asFloatBuffer();
		var indexBuffer = indicies.asShortBuffer();

		convertElement(vertexBuffer, indexBuffer, player, indexOffset);
	}
}
