package me.xxastaspastaxx.dimensions;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The class creates the config file with the defined variables inside the class for easier access to the config
 *
 */

public class DimensionsSettings {
	
	/**Config version for verion control */
	private static final double configVersion = 0.9;
	
	/**Search radius for nearby portals */
	public static int searchRadius = 128;
	
	/**Only search for portals that are facing the same axis as the one used */
	public static boolean searchSameAxis = false;
	/**Only search for portals that are the same size as the one used */
	public static boolean searchSameSize = false;
	/**Search first for a portal facing the same axis and has the same size as the one used and then search for any portal */
	public static boolean searchFirstClonePortal = true;

	/**Radius to search for a safe place to build a portal */
	public static int safeSpotSearchRadius = 16;
	/**The world that portals will teleport to when they dont know where to lead */
	public static World fallbackWorld = null;
	/**The level of the debug messages in the console */
	public static int debugLevel = 2;
	/**Enable if a fake block is being placed at the feet of the player to play the nether portal effect */
	public static boolean enableNetherPortalEffect = true;
	// public static boolean enableMobsTeleportation = true; TODO per portla
	//public static boolean enableEntitiesTeleportation = true; TODO per portla
	/**Allow item consumption on portal ignite */
	public static boolean consumeItems = true;
	/**Enable entities teleporting using portals */
	public static boolean enableEntitiesTeleport = false;
	/**Check for entities inside the portal every set ticks */
	public static long updateEveryTick = 7;
	/**Variable storing the total portal uses inside the server for Metrics */
	public static int metricsSave = 0;
	
	
	private static FileConfiguration config;
	private static Dimensions main;


	public DimensionsSettings(Dimensions main) {
		DimensionsSettings.main = main;
		
		try {
			config = main.getConfig();
			
			Field[] fields = this.getClass().getDeclaredFields();
			
			if (config.getDouble("configVersion", 0.0)!=configVersion) {
				for (Field field : fields) {
					if (field.getName().equals("fallbackWorld") || field.getName().startsWith("config") || field.getName().equals("main")) continue;
					config.addDefault(field.getName(), field.get(this.getClass()));
				}

				//config.addDefault("fallbackWorld", fallbackWorld.getName());
				
				config.set("configVersion", configVersion);
				
				config.options().copyDefaults(true);
				main.saveConfig();
			}
			
			//fallbackWorld = Bukkit.getWorld(config.getString("fallbackWorld"));
			
			for (Field field : fields) {
				if (field.getName().equals("fallbackWorld") || field.getName().startsWith("config") || field.getName().equals("main")) continue;
				
				field.set(this.getClass(), config.get(field.getName(), field.get(this.getClass())));
				
			}
		

			main.saveConfig();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	/**Set the World instance of the default world after all worlds have been loaded */
	public static void setDefaultWorld() {
		fallbackWorld = Bukkit.getWorlds().get(0);
		config.addDefault("fallbackWorld", fallbackWorld.getName());
		main.saveConfig();
		fallbackWorld = Bukkit.getWorld(config.getString("fallbackWorld"));
	}

	/**Get the ./plugins/Dimensions/config.yml instance of FileConfiguration */
	public static FileConfiguration getConfig() {
		return config;
	}
	
	public void reload() {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals("fallbackWorld") || field.getName().startsWith("config") || field.getName().equals("main")) continue;
			
			try {
				field.set(this.getClass(), config.get(field.getName(), field.get(this.getClass())));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
	}
	
}
