package me.xxastaspastaxx.dimensions.addons.forcelink;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;

public class DimensionsForceLink extends DimensionsAddon implements Listener {
	
	//private Plugin pl;

	HashMap<Player, CompletePortal> savedPortal = new HashMap<Player, CompletePortal>();
	
	public DimensionsForceLink() {
		super("DimensionsForceLink", "3.0.1", "Forcefully link two portals no matter what", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		//this.pl = pl;
		
		Dimensions.getCommandManager().registerCommand(new LinkPortalsCommand("forceLink", "[select/set]", new String[0], "Forcefully link two portals", "", true, this));
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	
	
}
