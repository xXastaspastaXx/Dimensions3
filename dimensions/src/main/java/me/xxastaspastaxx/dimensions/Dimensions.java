package me.xxastaspastaxx.dimensions;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.xxastaspastaxx.dimensions.addons.DimensionsAddonManager;
import me.xxastaspastaxx.dimensions.commands.DimensionsCommandManager;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortalManager;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalManager;
import me.xxastaspastaxx.dimensions.listener.PortalListener;

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
		
		DimensionsSettings.setDefaultWorld();

		commandManager = new DimensionsCommandManager(this);
		
		addonsManager.enableAddons();
		
		DimensionsDebbuger.debug("Loading portals", DimensionsDebbuger.DEBUG);
		customPortalManager = new CustomPortalManager(this);
		completePortalManager = new CompletePortalManager(this);
		
		new PortalListener(this);
		
		//Use a task in order to load portals only after all plugins have loaded and have generated/loaded their worlds
		//Portals require a world instance in order to be loaded and we can only have that if the plugin has loaded the required worlds
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			
			@Override
			public void run() {
				completePortalManager.loadAll();
				
			}
		}, 1);
	}
	
	public void reload() {
		addonsManager.unloadAll();
		completePortalManager.save();
		
		
		
		new DimensionsSettings(this);
		DimensionsSettings.setDefaultWorld();

		commandManager = new DimensionsCommandManager(this);

		addonsManager.enableAddons();

		customPortalManager = new CustomPortalManager(this);
		completePortalManager = new CompletePortalManager(this);
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
	
}
