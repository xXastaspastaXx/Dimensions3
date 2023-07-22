package me.xxastaspastaxx.dimensions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonManager;
import me.xxastaspastaxx.dimensions.addons.patreoncosmetics.DimensionsPatreonCosmetics;
import me.xxastaspastaxx.dimensions.builder.CreatePortalManager;
import me.xxastaspastaxx.dimensions.commands.DimensionsCommandManager;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortalManager;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalManager;
import me.xxastaspastaxx.dimensions.listener.PortalListener;
import me.xxastaspastaxx.dimensions.settings.DimensionsSettings;

/**
 * Main class of the plugin
 */
public class Dimensions extends JavaPlugin {
	
	private static Dimensions instance;
	private static DimensionsCommandManager commandManager;
	private static DimensionsAddonManager addonsManager;
	private static CompletePortalManager completePortalManager;
	private static CustomPortalManager customPortalManager;
	private static CreatePortalManager createPortalManager;
	
	private static DimensionsPatreonCosmetics patreonCosmetics;
	
	public void onLoad() {
		
		instance = this;
		
		DimensionsDebbuger.VERY_LOW.print("Loading Dimensions settings...");
		new DimensionsSettings(this);
 
		DimensionsDebbuger.VERY_LOW.print("Loading addons...");
		addonsManager = new DimensionsAddonManager(this);
		DimensionsDebbuger.VERY_LOW.print("Loaded "+addonsManager.getAddons().size()+" addons.");
		
	}
	
	public void onEnable() {

		DimensionsDebbuger.DEBUG.print("Registering commands...");
		commandManager = new DimensionsCommandManager(this);
		
		if (DimensionsSettings.enablePatreonCosmetics)
			patreonCosmetics = new DimensionsPatreonCosmetics(this);
		
		DimensionsDebbuger.VERY_LOW.print("Enabling addons...");
		addonsManager.enableAddons();
		
		DimensionsDebbuger.VERY_LOW.print("Loading portals...");
		customPortalManager = new CustomPortalManager(this);
		DimensionsDebbuger.MEDIUM.print("Found "+customPortalManager.getCustomPortals().size()+" portals.");
		completePortalManager = new CompletePortalManager(this);
		
		DimensionsDebbuger.VERY_LOW.print("Instatiating GUIs...");
		createPortalManager = new CreatePortalManager(this);

		DimensionsDebbuger.DEBUG.print("Registering Listener class...");
		new PortalListener(this);
		
		//Use a task in order to load portals only after all plugins have loaded and have generated/loaded their worlds
		//Portals require a world instance in order to be loaded and we can only have that if the plugin has loaded the required worlds

		DimensionsDebbuger.DEBUG.print("Dimensions has been loaded. Waiting for server to tick before loading saved portals...");
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			
			@Override
			public void run() {
				
				DimensionsSettings.setDefaultWorld();

				DimensionsDebbuger.DEBUG.print("Loading saved portals...");
				completePortalManager.loadAll();
				DimensionsDebbuger.DEBUG.print("Loading complete...");
				
			}
		}, 1);
		
		int pluginId = 6978;
		 Metrics metrics = new Metrics(this, pluginId);
	        
	        metrics.addCustomChart(new Metrics.DrilldownPie("portal_blocks_frames", () -> {
	            Map<String, Map<String, Integer>> map = new HashMap<>();
	            for (CustomPortal portal : getCustomPortalManager().getCustomPortals()) {
	                Map<String, Integer> entry = new HashMap<>();
	                entry.put(portal.getInsideMaterial().toString(),1);
	                map.put(portal.getOutsideMaterial().toString(), entry);
	            }
	            return map;
	        }));
	        
	        metrics.addCustomChart(new Metrics.DrilldownPie("portal_blocks_lighters", () -> {
	            Map<String, Map<String, Integer>> map = new HashMap<>();
	            for (CustomPortal portal : getCustomPortalManager().getCustomPortals()) {
	                Map<String, Integer> entry = new HashMap<>();
	                entry.put(portal.getLighterMaterial().toString(),1);
	                map.put(portal.getOutsideMaterial().toString(), entry);
	            }
	            return map;
	        }));
	        
	        metrics.addCustomChart(new Metrics.DrilldownPie("used_addons", () -> {
	            Map<String, Map<String, Integer>> map = new HashMap<>();
	            for (DimensionsAddon addon : getAddonManager().getAddons()) {
	                Map<String, Integer> entry = new HashMap<>();
	                entry.put(addon.getVersion(),1);
	                map.put(addon.getName(), entry);
	            }
	            return map;
	        }));
	}
	
	public void reload() {
		if (patreonCosmetics!=null)
			patreonCosmetics.disable();
		addonsManager.unloadAll();
		completePortalManager.save();
		HandlerList.unregisterAll(this);
		
		new DimensionsSettings(this);
		DimensionsSettings.setDefaultWorld();

		commandManager = new DimensionsCommandManager(this);
		
		if (DimensionsSettings.enablePatreonCosmetics)
			patreonCosmetics = new DimensionsPatreonCosmetics(this);

		addonsManager.enableAddons();

		customPortalManager = new CustomPortalManager(this);
		completePortalManager = new CompletePortalManager(this);
		createPortalManager = new CreatePortalManager(this);
		
		new PortalListener(this);
		
		completePortalManager.loadAll();
	}
	
	public void onDisable() {
		
		addonsManager.onDisable();
		completePortalManager.save();
	}
	
	public static Dimensions getInstance() {
		return instance;
	}
	
	public static CompletePortalManager getCompletePortalManager() {
		return completePortalManager;
	}

	public static CustomPortalManager getCustomPortalManager() {
		return customPortalManager;
	}
	
	public static DimensionsAddonManager getAddonManager() {
		return addonsManager;
	}
	
	public static DimensionsCommandManager getCommandManager() {
		return commandManager;
	}

	public static CreatePortalManager getCreatePortalManager() {
		return createPortalManager;
	}
	
}
