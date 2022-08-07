package me.xxastaspastaxx.dimensions;

import java.util.logging.Level;

public class DimensionsDebbuger {

	public static final DimensionsDebbuger DEBUG = new DimensionsDebbuger(5);
	public static final DimensionsDebbuger VERY_LOW = new DimensionsDebbuger(4);
	public static final DimensionsDebbuger LOW = new DimensionsDebbuger(3);
	public static final DimensionsDebbuger MEDIUM = new DimensionsDebbuger(2);
	public static final DimensionsDebbuger HIGH = new DimensionsDebbuger(1);
	public static final DimensionsDebbuger VERY_HIGH = new DimensionsDebbuger(0);
	
	
	private int level = 0;
	
	public DimensionsDebbuger(int i) {
		this.level = i;
	}

	public void print(Object str) {
		if (DimensionsSettings.debugLevel>=level)
			Dimensions.getInstance().getLogger().log(Level.INFO, str.toString());
	}
			
}
