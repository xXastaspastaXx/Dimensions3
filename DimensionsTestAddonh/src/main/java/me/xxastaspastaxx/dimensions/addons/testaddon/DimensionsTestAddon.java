package me.xxastaspastaxx.dimensions.addons.testaddon;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;

public class DimensionsTestAddon extends DimensionsAddon implements Listener {
	
	//private Plugin pl;
	
	public DimensionsTestAddon() {
		super("DimensionsTestAddon", "3.0.0", "That addon should not be here??!", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		//this.pl = pl;
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPortalIgnite(CustomPortalIgniteEvent e) {
		
		System.out.println("test2");
	}
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void postPortalIgnite(CustomPortalIgniteEvent e) {
		
		System.out.println("TEST3");
	}
	
	
	@Override
	public boolean needsUpdate() throws UnsupportedEncodingException, IOException {
		return false;
	}

	@Override
	public String getUpdateJarURL() {
		return null;
	}
	
}
