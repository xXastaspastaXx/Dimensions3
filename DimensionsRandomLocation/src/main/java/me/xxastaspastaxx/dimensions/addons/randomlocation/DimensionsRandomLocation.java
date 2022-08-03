package me.xxastaspastaxx.dimensions.addons.randomlocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.events.CustomPortalUseEvent;

public class DimensionsRandomLocation extends DimensionsAddon implements Listener {
	
	//private Plugin pl;
	
	public DimensionsRandomLocation() {
		super("DimensionsRandomLocationAddon", "3.0.1", "Teleport players to a random location", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		//this.pl = pl;
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPortalUse(CustomPortalUseEvent e) {
		
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object rangeOBJ = getOption(portal, "randomTPRange");
		if (rangeOBJ==null) return;

		if (e.getDestinationPortal()!=null) return; 
		
		Location loc = complete.getCenter();
		int range = (int) rangeOBJ;
			
		Location teleportLocation = new Location(null, loc.getX()+DimensionsUtils.getRandom(-range, range), loc.getY(), loc.getZ()+DimensionsUtils.getRandom(-range, range));

		e.setDestinationPortal(complete.getDestinationPortal(true, teleportLocation, null));
	}

	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {

		int range = portalConfig.getInt("Addon.RandomLocationRange", 0);
		if (range==0) return;
		setOption(portal, "randomTPRange", range);
		
		return;
	}
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "1Hxl7S3GeSU-_8KA7gU2p4Fb51kXwgMGdvAvSrWiiRYc";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "1dagEZ15xUEh-caGqD_4GpNXnoAbxuLl0";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}
	
}
