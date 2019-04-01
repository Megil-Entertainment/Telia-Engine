package ch.megil.teliaengine.vulkanui;

import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.game.Map;
import ch.megil.teliaengine.game.Vector;

public class VulkanMap extends VulkanElement {
	public VulkanMap(Map map, Vector cameraOffset) {
		this(map, 0, cameraOffset);
	}
	
	public VulkanMap(Map map, int indexOffset, Vector cameraOffset) {
		super(map.getMapObjects().size(), Double.parseDouble(GameConfiguration.MAP_WIDTH.getConfiguration()), 
				Double.parseDouble(GameConfiguration.MAP_HEIGHT.getConfiguration()), cameraOffset);
		
		var vertexBuffer = vertecies.asFloatBuffer();
		var indexBuffer = indicies.asShortBuffer();
		
		for (var i = 0; i < map.getMapObjects().size(); i++) {
			convertElement(vertexBuffer, indexBuffer, map.getMapObjects().get(i), indexOffset + i*VERTECIES_PER_OBJECT);
		}
	}
}
