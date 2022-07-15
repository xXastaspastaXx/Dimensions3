package me.xxastaspastaxx.dimensions.addons;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;

public abstract class DimensionsAddon {

	private String addonName;
	private String addonVersion;
	private String addonDescription;
	private DimensionsAddonPriority addonPriority = DimensionsAddonPriority.NORMAL;
	
	private HashMap<CustomPortal, HashMap<String, Object>> addonOptions = new HashMap<CustomPortal, HashMap<String, Object>>();
	
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
	
	public void setOption(CustomPortal portal, String key, Object value) {
		if (!addonOptions.containsKey(portal)) addonOptions.put(portal, new HashMap<String, Object>());
		if (value==null)
			addonOptions.get(portal).remove(key);
		else
			addonOptions.get(portal).put(key, value);
	}
	
	public Object getOption(CustomPortal portal, String key) {
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

	public abstract boolean needsUpdate() throws UnsupportedEncodingException, IOException;

	public abstract String getUpdateJarURL();



	
	
}
