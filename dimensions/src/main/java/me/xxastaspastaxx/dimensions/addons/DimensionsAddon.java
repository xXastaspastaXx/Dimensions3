package me.xxastaspastaxx.dimensions.addons;

import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;

/**
 * The abstract class that is parent to all the addons that are being loaded by Dimensions
 *
 */

public abstract class DimensionsAddon {
	
	private String addonName;
	private String addonVersion;
	private String addonDescription;
	private DimensionsAddonPriority addonPriority = DimensionsAddonPriority.NORMAL;

	private static HashMap<CompletePortal, HashMap<String, Object>> addonOptionsOverride = new HashMap<CompletePortal, HashMap<String, Object>>();
	private static HashMap<CustomPortal, HashMap<String, Object>> addonOptions = new HashMap<CustomPortal, HashMap<String, Object>>();
	
	/**
	 * Constructor of DimensionsAddon
	 * 
	 * @param addonName The name of the addon
	 * @param addonVersion The version of the addon
	 * @param addonDescription A short description of the addon
	 * @param addonPriority Priority to enable the addon
	 */
	public DimensionsAddon(String addonName, String addonVersion, String addonDescription, DimensionsAddonPriority addonPriority) {
		this.addonName = addonName;
		this.addonVersion = addonVersion;
		this.addonDescription = addonDescription;
		this.addonPriority = addonPriority;
	}
	
	/**
	 * Get the name of the addon
	 * @return the name of the addon
	 */
	public String getName() {
		return addonName;
	}

	/**
	 * Get the version of the addon
	 * @return the version of the addon
	 */
	public String getVersion() {
		return addonVersion;
	}
	
	/**
	 * Get the description of the addon
	 * @return the description of the addon
	 */
	public String getDescription() {
		return addonDescription;
	}
	
	/**
	 * Get the priority of the addon
	 * @return the priority of the addon
	 */
	public DimensionsAddonPriority getAddonPriority() {
		return addonPriority;
	}
	
	/**
	 * Store an Object with a String key for all portals that are the given CustomPortal
	 * @param portal the CustomPortal that contains the option
	 * @param key the key to store and access the value
	 * @param value the value to be stored
	 */
	public static void setOption(CustomPortal portal, String key, Object value) {
		if (!addonOptions.containsKey(portal)) addonOptions.put(portal, new HashMap<String, Object>());
		if (value==null)
			addonOptions.get(portal).remove(key);
		else
			addonOptions.get(portal).put(key, value);
	}
	
	/**
	 * Store an Object with a String key for this specific portal
	 * @param complete the portal that contains the option
	 * @param key the key to store and access the value
	 * @param value the value to be stored
	 */
	public static void setOption(CompletePortal complete, String key, Object value) {
		if (!addonOptionsOverride.containsKey(complete)) addonOptionsOverride.put(complete, new HashMap<String, Object>());
		if (value==null)
			addonOptionsOverride.get(complete).remove(key);
		else
			addonOptionsOverride.get(complete).put(key, value);
	}
	
	/**
	 * Get the option for the portal. If the portal does not contain the key, then we check the type of the portal for the given key
	 * @param complete the portal to get the option for
	 * @param key the key to access the value
	 * @return
	 */
	public static Object getOption(CompletePortal complete, String key) {
		if (addonOptionsOverride.containsKey(complete) && addonOptionsOverride.get(complete).containsKey(key)) {
			Object obj = addonOptionsOverride.get(complete).get(key);
			if (obj instanceof String && ((String) obj).equals("NULL")) return null;
			return obj;
		}
		else {
			CustomPortal portal = complete.getCustomPortal();
			if (!addonOptions.containsKey(portal)) addonOptions.put(portal, new HashMap<String, Object>());
			return addonOptions.get(portal).get(key);
		}
		
	}
	
	/**
	 * Get the option for the type of the portal for the given key
	 * @param CustomPortal to get the option for
	 * @param key the key to access the value
	 * @return
	 */
	public static Object getOption(CustomPortal portal, String key) {
		if (!addonOptions.containsKey(portal)) addonOptions.put(portal, new HashMap<String, Object>());
		return addonOptions.get(portal).get(key);
	}
	
	//Functions to override
	/**
	 * Run when loading the addon
	 * @param pl the instance of the Dimensions plugin
	 * @return true if the addon was loaded without any missing dependencies
	 */
	public boolean onLoad(Dimensions pl) {
		return true;
	}
	
	/**
	 * Run when the addon is being enabled
	 * @param pl the instance of the Dimensions plugin
	 */
	public void onEnable(Dimensions pl) {
	}
	
	/**
	 * Run when disabling the addon
	 */
	public void onDisable() {
	}
	
	/**
	 * This function provides the portal config file for each portal that is being loaded and addons can get data from it.
	 * @param portalConfig the YamlConfiguration for the portal loading
	 * @param portal the portal loading
	 */
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {
	}

	/**
	 * This runs when we reload the plugin <b>AFTER</b> all the addons have been disabled in order to reset all the data
	 */
	public static void resetOptions() {
		addonOptions.clear();
	}



	
	
}
