package ch.megil.teliaengine.configuration.data;

public class GameConfigData {
	private String mapWidth;
	private String mapHeight;

	public GameConfigData(double mapWidth, double mapHeight) {
		this.mapWidth = Double.toString(mapWidth);
		this.mapHeight = Double.toString(mapHeight);
	}

	public String getMapWidth() {
		return mapWidth;
	}

	public String getMapHeight() {
		return mapHeight;
	}
}
