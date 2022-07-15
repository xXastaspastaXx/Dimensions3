package me.xxastaspastaxx.dimensions;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;

public class DimensionsSettings {
	
	private static final double configVersion = 0.5;
	
	public static int searchRadius = 128;
	public static int safeSpotSearchRadius = 16;
	public static World fallbackWorld = null;
	public static int debugLevel = 2;
	//public static int teleportDelay = 4; TODO per portla
	//public static boolean enableParticles = false; TODO per portla
	//public static boolean enableNetherPortalEffect = true; TODO per portla
	// public static boolean enableMobsTeleportation = true; TODO per portla
	//public static boolean enableEntitiesTeleportation = true; TODO per portla
	public static boolean checkForUpdatesOnStartup = false;
	//public static boolean generateNewWorlds = false;
	public static boolean consumeItems = true;
	
	
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
	
	public static void setDefaultWorld() {
		fallbackWorld = Bukkit.getWorlds().get(0);
		config.addDefault("fallbackWorld", fallbackWorld.getName());
		main.saveConfig();
		fallbackWorld = Bukkit.getWorld(config.getString("fallbackWorld"));
		if (!Bukkit.getServer().getWorlds().contains(fallbackWorld)) {
			fallbackWorld = Bukkit.getServer().createWorld(new WorldCreator(config.getString("fallbackWorld")));
		}
	}

	public static Object get(String key) {
		if (config.get(key)==null) {
			DimensionsDebbuger.debug("An option that was not defined in the config is required. Please open the config.yml.", DimensionsDebbuger.VERY_HIGH);
			config.set(key, "PLEASE CHANGE");
			main.saveConfig();
		}
		
		return config.get(key);
	}
	
	public static Object get(String key, Object def) {
		if (config.get(key)==null) {
			config.set(key, def);
			main.saveConfig();
		}
		
		return config.get(key);
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
