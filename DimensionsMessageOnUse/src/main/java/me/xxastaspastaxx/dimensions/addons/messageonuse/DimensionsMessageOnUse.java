package me.xxastaspastaxx.dimensions.addons.messageonuse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.events.CustomPortalUseEvent;

public class DimensionsMessageOnUse extends DimensionsAddon implements Listener {
	
	//private Plugin pl;
	
	public DimensionsMessageOnUse() {
		super("DimensionsMessageOnUseAddon", "3.0.0", "Send a message when players use portals", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		//this.pl = pl;
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void postPortalUse(CustomPortalUseEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		
		CustomPortal portal = e.getCompletePortal().getCustomPortal();
		Object msg = getOption(portal, "messageOnUse");
		if (msg==null) return;
		
		e.getEntity().sendMessage((String) msg);
	}

	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {

		String str = portalConfig.getString("Addon.MessageOnUse", "none");

		if(str.equalsIgnoreCase("none")) return;
		
		setOption(portal, "messageOnUse", str.replace("&", "§"));
		
		return;
	}
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "1oB17GslaESIseo8j5paZJE0QHE_ZINumkKFkokI8A-g";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "1Z7R_Rw82W3zr0G-sMhxikt2a7jmU3san";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}
	
}
