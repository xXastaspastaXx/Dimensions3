package me.xxastaspastaxx.dimensions.addons.timedportals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalDestroyCause;
import me.xxastaspastaxx.dimensions.events.CustomPortalBreakEvent;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;

public class DimensionsTimedPortals extends DimensionsAddon implements Listener {
	
	private Plugin pl;
	
	private HashMap<CompletePortal, Integer> threads = new HashMap<CompletePortal, Integer>();
	
	public DimensionsTimedPortals() {
		super("DimensionsTimedPortalsAddon", "3.0.0", "Portals unlit after some time", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		this.pl = pl;
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void postPortalIgnite(CustomPortalIgniteEvent e) {
		
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();

		Object destroyAfter = getOption(portal, "timedPortalsDestroyAfter");
		if (destroyAfter==null) return;

		
		if (threads.containsKey(complete)) {
			Bukkit.getScheduler().cancelTask(threads.remove(complete));
		}
		
		int id = Bukkit.getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
			
			@Override
			public void run() {
				
				destroy(complete, (String) getOption(portal, "timedPortalsAction"));
				
			}
		}, ((int) destroyAfter)/50);
		
		threads.put(complete, id);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void postPortalDestroy(CustomPortalBreakEvent e) {
		
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();

		if (getOption(portal, "timedPortalsDestroyAfter")==null) return;
		
		if (threads.containsKey(complete)) {
			Bukkit.getScheduler().cancelTask(threads.get(complete));
			threads.remove(complete);
		}
	}
	
	public void destroy(CompletePortal completePortal, String action) {
		threads.remove(completePortal);
		
		if (action.startsWith("Close")) {
			Dimensions.getCompletePortalManager().removePortal(completePortal, CustomPortalDestroyCause.PLUGIN, null);
		} else if (action.startsWith("Destroy")) {
			Dimensions.getCompletePortalManager().removePortal(completePortal, CustomPortalDestroyCause.PLUGIN, null);
			
			PortalGeometry geom = completePortal.getPortalGeometry();
			Vector min = geom.getMin();
			Vector max = geom.getMax();
			boolean zAxis = geom.iszAxis();
			
			for (double y=min.getY();y<=max.getY();y++) {
				for (double side=zAxis?min.getZ():min.getX();side<=(zAxis?max.getZ():max.getX());side++) {
					(new Location(completePortal.getWorld(), zAxis?min.getX():side, y, !zAxis?min.getZ():side)).getBlock().setType(Material.AIR);
				}
			}
		}

		if (action.contains("{explode%") && Math.random()<=Integer.parseInt(action.split("%")[1].replace("}", ""))/100) {
			completePortal.getWorld().createExplosion(completePortal.getCenter(), 5);
		}
	}
	
	
	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {

		int uses = portalConfig.getInt("Addon.TimedPortals.DestroyAfterMillis", 0);
		if (uses==0) return;
		
		setOption(portal, "timedPortalsDestroyAfter", uses);
		setOption(portal, "timedPortalsAction", portalConfig.getString("Addon.TimedPortals.Action", "Close"));
		
		return;
	}
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "1WIcpqmMZwWs2-D38cseMXyN0Gbza-9PMxaVSqVHMs6I";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "1curqXufkFS7_FzrlewiTo-ndSjPlX8a6";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}
	
}
