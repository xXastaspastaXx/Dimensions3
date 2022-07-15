package me.xxastaspastaxx.dimensions;

import java.util.logging.Level;

public class DimensionsDebbuger {

	public static final int DEBUG = 5;
	public static final int VERY_LOW = 4;
	public static final int LOW = 3;
	public static final int MEDIUM = 2;
	public static final int HIGH = 1;
	public static final int VERY_HIGH = 0;
	
	public static void debug(Object str, int level) {
		if (DimensionsSettings.debugLevel>=level)
			Dimensions.getInstance().getLogger().log(Level.INFO, str.toString());
	}
			
}
