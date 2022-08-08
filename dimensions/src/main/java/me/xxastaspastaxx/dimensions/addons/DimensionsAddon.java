package me.xxastaspastaxx.dimensions.addons;

import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;

public abstract class DimensionsAddon {
	
	private String addonName;
	private String addonVersion;
	private String addonDescription;
	private DimensionsAddonPriority addonPriority = DimensionsAddonPriority.NORMAL;

	private static HashMap<CompletePortal, HashMap<String, Object>> addonOptionsOverride = new HashMap<CompletePortal, HashMap<String, Object>>();
	private static HashMap<CustomPortal, HashMap<String, Object>> addonOptions = new HashMap<CustomPortal, HashMap<String, Object>>();
	
	public DimensionsAddon(String addonName, String addonVersion, String addonDescription, DimensionsAddonPriority addonPriority) {
		this.addonName = addonName;
		this.addonVersion = addonVersion;
		this.addonDescription = addonDescription;
		this.addonPriority = addonPriority;
	}
	
	public String getName() {
		return addonName;
	}

	public String getVersion() {
		return addonVersion;
	}
	
	public String getDescription() {
		return addonDescription;
	}
	
	public DimensionsAddonPriority getAddonPriority() {
		return addonPriority;
	}
	
	public static void setOption(CustomPortal portal, String key, Object value) {
		if (!addonOptions.containsKey(portal)) addonOptions.put(portal, new HashMap<String, Object>());
		if (value==null)
			addonOptions.get(portal).remove(key);
		else
			addonOptions.get(portal).put(key, value);
	}
	
	public static void setOption(CompletePortal complete, String key, Object value) {
		if (!addonOptionsOverride.containsKey(complete)) addonOptionsOverride.put(complete, new HashMap<String, Object>());
		if (value==null)
			addonOptionsOverride.get(complete).remove(key);
		else
			addonOptionsOverride.get(complete).put(key, value);
	}
	
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
	
	public static Object getOption(CustomPortal portal, String key) {
		if (!addonOptions.containsKey(portal)) addonOptions.put(portal, new HashMap<String, Object>());
		return addonOptions.get(portal).get(key);
	}
	
	//Functions to override
	public boolean onLoad(Dimensions pl) {
		return true;
	}
	
	public void onUnLoad() {
	}
	
	public void onEnable(Dimensions pl) {
	}
	
	public void onDisable() {
	}
	
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {
	}


	public static void resetOptions() {
		addonOptions.clear();
	}



	
	
}
