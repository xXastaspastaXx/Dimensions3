	package me.xxastaspastaxx.dimensions.addons.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;
import me.xxastaspastaxx.dimensions.events.CustomPortalUseEvent;

public class DimensionsWeather extends DimensionsAddon implements Listener {
	
	//private Plugin pl;
	
	public DimensionsWeather() {
		super("DimensionsWeatherAddon", "3.0.0", "Weather controls portals", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		//this.pl = pl;
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPortalIgnite(CustomPortalIgniteEvent e) {
		
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();

		Object obj = getOption(portal, "weatherDisabled");
		if (obj==null) return;

		String disabled = (String) obj;
		Entity entity = e.getEntity();
		
		World world = complete.getWorld();
		boolean thunder = world.isThundering();
		boolean rain = world.hasStorm() && !thunder;
		boolean clear = !thunder && !rain;
		if ((disabled.contains("RAIN") && rain) || (disabled.contains("THUNDER") && thunder) || (disabled.contains("CLEAR") && clear)) {
			if (entity instanceof Player)  entity.sendMessage((String) getOption(portal, "weatherDenyMessage"));
        	e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPortalUse(CustomPortalUseEvent e) {
		
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();

		Object obj = getOption(portal, "weatherDisabled");
		if (obj==null) return;

		String disabled = (String) obj;
		Entity entity = e.getEntity();
		
		World world = complete.getWorld();
		boolean thunder = world.isThundering();
		boolean rain = world.hasStorm() && !thunder;
		boolean clear = !thunder && !rain;
		if ((disabled.contains("RAIN") && rain) || (disabled.contains("THUNDER") && thunder) || (disabled.contains("CLEAR") && clear)) {
			if (entity instanceof Player)  entity.sendMessage((String) getOption(portal, "weatherDenyMessage"));
        	e.setCancelled(true);
		}
	}

	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {

		String disabled = portalConfig.getString("Addon.Weather.Disabled", "none");
		if (disabled.contentEquals("none")) return;

		setOption(portal, "weatherDisabled", disabled);
		setOption(portal, "weatherDenyMessage", portalConfig.getString("Addon.Weather.DenyMessage", "The portal cannot be activate at this weather condition").replace("&", "§"));
		
		return;
	}
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "1IQNnRoQaVT2WURgbHvWECQSJ6owIMwJGt9n0-yCF0Ps";
		
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
