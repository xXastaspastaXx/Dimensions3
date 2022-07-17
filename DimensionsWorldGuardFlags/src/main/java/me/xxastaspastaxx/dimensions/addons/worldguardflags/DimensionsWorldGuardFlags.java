package me.xxastaspastaxx.dimensions.addons.worldguardflags;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.protection.flags.StateFlag;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.events.CustomPortalBreakEvent;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;
import me.xxastaspastaxx.dimensions.events.CustomPortalUseEvent;

public class DimensionsWorldGuardFlags extends DimensionsAddon implements Listener {
	
	private Plugin pl;

	private WorldGuardFlags worldGuardFlags;
	
	public DimensionsWorldGuardFlags() {
		super("DimensionsWorldGuardFlagsAddon", "3.0.0", "Add world guard flags for Dimensions portals", DimensionsAddonPriority.NORMAL);
	}
	
	
	@Override
	public boolean onLoad(Dimensions main) {
		this.pl = main;

		Plugin worldGuardPlugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
	    if (worldGuardPlugin!=null) {
			worldGuardFlags = new WorldGuardFlags();
			return true;
	    }
	    
	    return false;
	}
	
	@Override
	public void onEnable(Dimensions main) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				try {
					if (worldGuardFlags!=null) worldGuardFlags.enablePlatform();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}, 20);
		

		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPortalIgnite(CustomPortalIgniteEvent e) {
		if (testWorldGuard(e.getEntity(), e.getCompletePortal().getCenter(), WorldGuardFlags.IgniteCustomPortal)) {
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPortalDestroy(CustomPortalBreakEvent e) {
		if (testWorldGuard(e.getDestroyer(), e.getCompletePortal().getCenter(), WorldGuardFlags.DestroyCustomPortal)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPortalUse(CustomPortalUseEvent e) {
		if (testWorldGuard(e.getEntity(), e.getCompletePortal().getCenter(), WorldGuardFlags.UseCustomPortal)) {
			e.setCancelled(true);
		}
	}
	
	private boolean testWorldGuard(Entity entity, Location center, StateFlag flag) {
		if (!(entity instanceof Player)) return false;

		return !worldGuardFlags.testState((Player) entity, center, flag);
	}
	
/*
	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {
		
		if (Bukkit.getPluginManager().getPlugin("WorldGuard")==null) return;
		
		return;
	}*/
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "1v2HQxiU8yCYXB4ze65gpEUSt7gJwgO4NenmDUqKM7ic";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "1x8qI2PfQEqBKGUHpvuoRM3vkyjHCphWq";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}
	
}
