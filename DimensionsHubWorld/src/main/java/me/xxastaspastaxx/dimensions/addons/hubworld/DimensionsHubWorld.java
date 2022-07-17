package me.xxastaspastaxx.dimensions.addons.hubworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;
import me.xxastaspastaxx.dimensions.events.CustomPortalUseEvent;

public class DimensionsHubWorld extends DimensionsAddon implements Listener {
	
	private Plugin pl;
	
	public DimensionsHubWorld() {
		super("DimensionsHubWorldAddon", "1.0.5", "Set a portal hub", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public void onEnable(Dimensions main) {
		this.pl = main;
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPortalIgnite(CustomPortalIgniteEvent e) {
		
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object hubWorldName = getOption(portal, "hubWorld");
		if (hubWorldName==null) return;
		World hubWorld = Bukkit.getWorld((String) hubWorldName);
		
		
		
		if (complete.getWorld().equals(hubWorld) && !Dimensions.getCompletePortalManager().getCompletePortals(hubWorld).isEmpty())
			e.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPortalUse(CustomPortalUseEvent e) {
		

		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object hubWorldName = getOption(portal, "hubWorld");
		if (hubWorldName==null || !(e.getEntity() instanceof Player)) return;
		Player p = (Player) e.getEntity();
		World hubWorld = Bukkit.getWorld((String) hubWorldName);
		
		Location loc = complete.getCenter();
		if (!loc.getWorld().equals(hubWorld)) {
			
			ArrayList<CompletePortal> hubPortal = Dimensions.getCompletePortalManager().getCompletePortals(portal, hubWorld);
			if (hubPortal.size()==1) {
				CompletePortal dest = hubPortal.get(0);
				dest.setTag("HUBRETURN-"+p.getUniqueId().toString(), DimensionsUtils.locationToString(loc, ","));
				e.setDestinationPortal(dest);
			}
		} else {
			Object str = complete.getTag("HUBRETURN-"+p.getUniqueId().toString());
			if (str!=null) {
				Location ret = DimensionsUtils.parseLocationFromString((String) str, ",");
				complete.setTag("HUBRETURN-"+p.getUniqueId().toString(), null);
				e.setDestinationPortal(Dimensions.getCompletePortalManager().getCompletePortal(ret, false, false));
			}
		}
	}

	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {

		String worldName = portalConfig.getString("Addon.HubWorld", "false");
		if (worldName.equals("false")) return;
		setOption(portal, "hubWorld", worldName);
		
		return;
	}
	
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "1FSicVB7jPfMi26Rr9cfrPW3BbmFQzmy-y_yzrAtBNHw";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "1veowtjolP8dNfUQq4fWEKXEN1s_QwUtQ";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}
	
}
