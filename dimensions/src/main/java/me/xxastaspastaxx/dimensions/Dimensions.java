package me.xxastaspastaxx.dimensions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonManager;
import me.xxastaspastaxx.dimensions.commands.DimensionsCommandManager;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortalManager;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalManager;
import me.xxastaspastaxx.dimensions.listener.PortalListener;

/**
 * Main class of the plugin
 */
public class Dimensions extends JavaPlugin {
	
	private static Dimensions instance;
	private static DimensionsCommandManager commandManager;
	private static DimensionsAddonManager addonsManager;
	private static CompletePortalManager completePortalManager;
	private static CustomPortalManager customPortalManager;
	
	public void onLoad() {
		
		instance = this;
		
		new DimensionsSettings(this);
		
		addonsManager = new DimensionsAddonManager(this);
	}
	
	public void onEnable() {

		commandManager = new DimensionsCommandManager(this);
		
		addonsManager.enableAddons();
		
		DimensionsDebbuger.DEBUG.print("Loading portals...");
		customPortalManager = new CustomPortalManager(this);
		completePortalManager = new CompletePortalManager(this);
		
		new PortalListener(this);
		
		//Use a task in order to load portals only after all plugins have loaded and have generated/loaded their worlds
		//Portals require a world instance in order to be loaded and we can only have that if the plugin has loaded the required worlds
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			
			@Override
			public void run() {
				
				DimensionsSettings.setDefaultWorld();
				completePortalManager.loadAll();
				
			}
		}, 1);
		
		int pluginId = 6978;
		 Metrics metrics = new Metrics(this, pluginId);
			
		 metrics.addCustomChart(new Metrics.MultiLineChart("players_and_servers", new Callable<Map<String, Integer>>() {
	            @Override
	            public Map<String, Integer> call() throws Exception {
	                Map<String, Integer> valueMap = new HashMap<>();
	                valueMap.put("servers", 1);
	                valueMap.put("players", Bukkit.getOnlinePlayers().size());
	                return valueMap;
	            }
	        }));
	        
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
	        
	        metrics.addCustomChart(new Metrics.SingleLineChart("total_portal_uses", () -> DimensionsSettings.metricsSave));
	}
	
	public void reload() {
		addonsManager.unloadAll();
		completePortalManager.save();
		
		

		getConfig().set("metricsSave", DimensionsSettings.metricsSave);
		saveConfig();
		new DimensionsSettings(this);
		DimensionsSettings.setDefaultWorld();

		commandManager = new DimensionsCommandManager(this);

		addonsManager.enableAddons();

		customPortalManager = new CustomPortalManager(this);
		completePortalManager = new CompletePortalManager(this);
		completePortalManager.loadAll();
	}
	
	public void onDisable() {

		getConfig().set("metricsSave", DimensionsSettings.metricsSave);
		saveConfig();
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
	
}
