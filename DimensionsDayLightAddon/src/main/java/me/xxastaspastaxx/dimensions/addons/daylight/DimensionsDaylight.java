package me.xxastaspastaxx.dimensions.addons.daylight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;
import me.xxastaspastaxx.dimensions.events.CustomPortalUseEvent;

public class DimensionsDaylight extends DimensionsAddon implements Listener {
	
	//private Plugin pl;
	
	public DimensionsDaylight() {
		super("DimensionsDayLightAddon", "3.0.0", "Allow portals to be ignite only for a specific period of time", DimensionsAddonPriority.NORMAL);
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
		Object min = getOption(portal, "dayLightMin");
		if (min==null) return;

		if (!isOk(portal, complete.getWorld().getTime())) {
			Entity entity = e.getEntity();
			if (entity instanceof Player) entity.sendMessage((String) getOption(portal, "dayLightDenyMessage"));
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPortalUse(CustomPortalUseEvent e) {

		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object min = getOption(portal, "dayLightMin");
		if (min==null) return;

		
		if (!isOk(portal, complete.getWorld().getTime())) {
			Entity entity = e.getEntity();
			if (entity instanceof Player) entity.sendMessage((String) getOption(portal, "dayLightDenyMessage"));
			e.setCancelled(true);
		}
	}

	public boolean isOk(CustomPortal portal, long time) {
		int min = (int) getOption(portal, "dayLightMin");
		int max = (int) getOption(portal, "dayLightMax");
		
		if (min<max) {
			if ((!(time>=min && time<=max))) {
				return false;
			}
		} else {
			//18000
			//20000-6000
			if (time<min && time>max) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {

		int min = portalConfig.getInt("Addon.DayLightSensor.StartAllow", 0);
		int max = portalConfig.getInt("Addon.DayLightSensor.StopAllow", 0);
		if (min==max) return;

		setOption(portal, "dayLightMin", min);
		setOption(portal, "dayLightMax", max);
		setOption(portal, "dayLightDenyMessage", portalConfig.getString("Addon.DayLightSensor.DenyMessage", "The portal cannot be activate at this time of the day").replace("&", "§"));
	}
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "1YCE5wXiNC1dO5ag-V_hBJLfFGV61cvrgXWiqmW8hq6k";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "1_ejpmA_7zokKhO0CH_a2ukw8sVhdksNd";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}
	
}
