package me.xxastaspastaxx.dimensions.settings;

public class WorldConfiguration {
	
	private int minHeight;
	private int maxHeight;
	private double size;
	
	public WorldConfiguration(int minHeight, int maxHeight, double size) {
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.size = size;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public double getSize() {
		return size;
	}

}
