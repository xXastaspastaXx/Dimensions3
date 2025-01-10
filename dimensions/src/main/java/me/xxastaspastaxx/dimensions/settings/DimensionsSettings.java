package me.xxastaspastaxx.dimensions.settings;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalDestroyCause;

/**
 * The class creates the config file with the defined variables inside the class for easier access to the config
 *
 */

public class DimensionsSettings {
	
	/**Config version for verion control */
	private static final double configVersion = 1.3;
	
	/**Enable patreon cosmetics for your server*/
	public static boolean enablePatreonCosmetics = true;
	
	/**Enable to show players the available portals*/
	public static boolean showPortalsToPlayers = true;
	
	/**Search radius for nearby portals */
	public static int searchRadius = 128;
	
	/**Only search for portals that are facing the same axis as the one used */
	public static boolean searchSameAxis = false;
	/**Only search for portals that are the same size as the one used */
	public static boolean searchSameSize = false;
	/**Search first for a portal facing the same axis and has the same size as the one used and then search for any portal */
	public static boolean searchFirstClonePortal = true;
	/**Ignore portals that are already linked to another portal */
	public static boolean ignoreLinkedPortals = false;

	/**Radius to search for a safe place to build a portal */
	public static int safeSpotSearchRadius = 16;
	/**Allow in order to not serach for safeSpotSearchRadius blocks in height but for the whole world height */
	public static boolean safeSpotSearchAllY = true;
	
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
	/**List of allowed event checks so admins can control cpu usage */
	public static List<String> listenToEvents = Arrays.asList(CustomPortalDestroyCause.values()).stream().map(s -> s.name()).collect(Collectors.toList());

	/**Check for entities inside the portal every set ticks */
	public static long portalInsideDelay = 1;
	
	/**List of configuration per world for easier access + override*/
	private static HashMap<String, WorldConfiguration> worldConfigurations = new HashMap<String, WorldConfiguration>();
	
	private static FileConfiguration config;
	private static Dimensions main;

	private static ArrayList<String> ignoredSettings = new ArrayList<String>(Arrays.asList(new String[] {
			"fallbackWorld",
			"config",
			"main",
			"worldConfigurations",
			"configVersion",
			"ignoredSettings"
	}));

	public DimensionsSettings(Dimensions main) {
		DimensionsSettings.main = main;
		main.reloadConfig();
		
		try {
			config = main.getConfig();
			
			Field[] fields = this.getClass().getDeclaredFields();
			
			if (config.getDouble("configVersion", 0.0)!=configVersion) {
				//UpdateFromPrev
				config.set("configVersion", configVersion);
			}
			
			for (Field field : fields) {
				if (ignoredSettings.contains(field.getName())) continue;
				config.addDefault(field.getName(), field.get(this.getClass()));
			}
			
			config.options().copyDefaults(true);
			main.saveConfig();
			
			for (Field field : fields) {
				if (ignoredSettings.contains(field.getName())) continue;
				
				field.set(this.getClass(), config.get(field.getName(), field.get(this.getClass())));
				
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	public static WorldConfiguration getWorldConfiguration(World world) {
		if (!worldConfigurations.containsKey(world.getName())) {
			return new WorldConfiguration(world.getMinHeight(), world.getMaxHeight(), world.getWorldBorder().getSize());
		}
		
		return worldConfigurations.get(world.getName());
	}
	
	/**Set the World instance of the default world after all worlds have been loaded */
	public static void setDefaultWorld() {
		fallbackWorld = Bukkit.getWorlds().get(0);
		config.addDefault("fallbackWorld", fallbackWorld.getName());
		config.options().copyDefaults(true);
		main.saveConfig();
		fallbackWorld = Bukkit.getWorld(config.getString("fallbackWorld"));
		
		worldConfigurations.clear();
		if (config.getConfigurationSection("Worlds")!=null) {
			for (String string : config.getConfigurationSection("Worlds").getKeys(false)) {
				World world = Bukkit.getWorld(string);
				worldConfigurations.put(string, new WorldConfiguration(
						config.getInt("Worlds."+string+".MinHeight", world.getMinHeight()),
						config.getInt("Worlds."+string+".MaxHeight", world.getMaxHeight()),
						config.getDouble("Worlds."+string+".Size", world.getWorldBorder().getSize())
						));
			}
		}
	}

	/**Get the ./plugins/Dimensions/config.yml instance of FileConfiguration */
	public static FileConfiguration getConfig() {
		return config;
	}
	
}
