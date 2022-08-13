package me.xxastaspastaxx.dimensions.addons.lightapi;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.completePortal.PortalEntity;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.events.CustomPortalBreakEvent;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;
import ru.beykerykt.minecraft.lightapi.common.LightAPI;
import ru.beykerykt.minecraft.lightapi.common.api.engine.LightFlag;

public class DimensionsLightAPI extends DimensionsAddon implements Listener {
	
	//private Plugin pl;
	
	LightAPI api;
	
	public DimensionsLightAPI() {
		super("DimensionsLightAPIAddon", "3.0.1", "Unbreakable portals", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public boolean onLoad(Dimensions pl) {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("LightAPI"/*+"_Beta"*/);
	    if (plugin!=null) {
			return true;
	    }
	    
	    return false;
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		//this.pl = pl;
		
		api = LightAPI.get();
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void postPortalIgnite(CustomPortalIgniteEvent e) {
		CompletePortal complete = e.getCompletePortal();
		Object option = getOption(complete, "lightapilevel");
		if (option==null) return;
		

		for (PortalEntity en : complete.getPortalEntities()) {
			Location loc = en.getLocation();
			api.setLightLevel(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), (int) option, LightFlag.BLOCK_LIGHTING);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void postPortalBreak(CustomPortalBreakEvent e) {
		CompletePortal complete = e.getCompletePortal();
		Object option = getOption(complete, "lightapilevel");
		if (option==null) return;
		
		for (PortalEntity en : complete.getPortalEntities()) {
			Location loc = en.getLocation();
			api.setLightLevel(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 0, LightFlag.BLOCK_LIGHTING);
		}
		
	}

	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {

		int spl = portalConfig.getInt("Addon.LightAPI.Level", 0);

		if(spl==0) return;
		
		setOption(portal, "lightapilevel", spl);
		
		return;
	}
	
	
}
