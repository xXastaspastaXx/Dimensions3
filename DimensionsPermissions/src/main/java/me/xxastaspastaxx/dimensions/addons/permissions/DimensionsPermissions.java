package me.xxastaspastaxx.dimensions.addons.permissions;

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

public class DimensionsPermissions extends DimensionsAddon implements Listener {
	
	//private Plugin pl;
	
	public DimensionsPermissions() {
		super("DimensionsPermissionsAddon", "3.0.0", "Require permissions to use a portal", DimensionsAddonPriority.NORMAL);
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
		Object permNode = getOption(portal, "permissionNode");
		if (permNode==null) return;
		Entity entity = e.getEntity();
		
		if (entity instanceof Player && !entity.hasPermission((String) permNode)) {
			entity.sendMessage((String) getOption(portal, "permissionDenyMessage"));
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPortalUse(CustomPortalUseEvent e) {
		
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object permNode = getOption(portal, "permissionNode");
		if (permNode==null) return;
		
		Entity entity = e.getEntity();
		
		if (entity instanceof Player && !entity.hasPermission((String) permNode)) {
			entity.sendMessage((String) getOption(portal, "permissionDenyMessage"));
			e.setCancelled(true);
		}
	}

	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {

		String node = portalConfig.getString("Addon.Permission.Node", "none");
		if (node.contentEquals("none")) return;
		setOption(portal, "permissionNode", node);
		setOption(portal, "permissionDenyMessage", portalConfig.getString("Addon.Permission.DenyMessage", "You have no permission to use this portal.").replace("&", "§"));
		
		return;
	}
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "1P4qaeLCq-XZkX9m7-UY3pJc_hU04Z9lyZeowo7d81oA";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "1oeSiJYmkwv1tR-CMsMkqtGXqGPhPxwJX";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}
	
}
