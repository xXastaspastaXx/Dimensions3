package me.xxastaspastaxx.dimensions;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Use static instances of the class to print debug messages to the appropriate debug level set in the config
 */
public class DimensionsDebbuger {
	
	/**Show messages for debug level 5 */
	public static final DimensionsDebbuger DEBUG = new DimensionsDebbuger(5);
	/**Show messages for debug level 4 */
	public static final DimensionsDebbuger VERY_LOW = new DimensionsDebbuger(4);
	/**Show messages for debug level 3 */
	public static final DimensionsDebbuger LOW = new DimensionsDebbuger(3);
	/**Show messages for debug level 2 */
	public static final DimensionsDebbuger MEDIUM = new DimensionsDebbuger(2);
	/**Show messages for debug level 1 */
	public static final DimensionsDebbuger HIGH = new DimensionsDebbuger(1);
	/**Show messages for debug level 0 */
	public static final DimensionsDebbuger VERY_HIGH = new DimensionsDebbuger(0);
	
	
	private int level = 0;
	
	private DimensionsDebbuger(int i) {
		this.level = i;
	}

	/**
	 * Use the DimensionsDebbuger instance to print a message in the console for the appropriate debugLevels set in the config
	 * 
	 * @param str The message to print
	 */
	public void print(Object... str) {
		if (DimensionsSettings.debugLevel>=level)
			Dimensions.getInstance().getLogger().log(Level.INFO, String.join(", ",Arrays.asList(str).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList())));
	}
			
}
