package ch.megil.teliaengine.vulkanui;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.physics.Vector;

public class VulkanMap extends VulkanElement {
	public VulkanMap(Map map, Vector cameraPosition) {
		this(map, 0, cameraPosition);
	}
	
	public VulkanMap(Map map, int indexOffset, Vector cameraPosition) {
		super(map.getMapObjects().size(), GameConfiguration.MAP_WIDTH.getConfiguration(), 
				GameConfiguration.MAP_HEIGHT.getConfiguration(), cameraPosition);
		
		var vertexBuffer = vertecies.asFloatBuffer();
		var indexBuffer = indicies.asShortBuffer();
		
		for (var i = 0; i < map.getMapObjects().size(); i++) {
			convertElement(vertexBuffer, indexBuffer, map.getMapObjects().get(i), indexOffset + i*VERTECIES_PER_OBJECT);
		}
	}
}
