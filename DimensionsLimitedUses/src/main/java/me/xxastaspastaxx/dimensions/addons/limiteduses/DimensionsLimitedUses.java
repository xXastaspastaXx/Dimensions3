package me.xxastaspastaxx.dimensions.addons.limiteduses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

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
import me.xxastaspastaxx.dimensions.events.CustomPortalUseEvent;

public class DimensionsLimitedUses extends DimensionsAddon implements Listener {
	
	private Plugin pl;
	
	public DimensionsLimitedUses() {
		super("DimensionsLimitedUsesAddon", "3.0.0", "Portals can now only be used specific amount of times", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		this.pl = pl;
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void postPortalUse(CustomPortalUseEvent e) {
		
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object maxUsesOBJ = getOption(portal, "limitedUses");
		if (maxUsesOBJ==null) return;
		
		Object hubWorldName = getOption(portal, "hubWorld");
		if (hubWorldName!=null) {
			if (complete.getWorld().getName().contentEquals((String) hubWorldName)) return;
		}
		int maxUses = (int) maxUsesOBJ;
		String action = (String) getOption(portal, "limitedUsesAction");
		
		
		Object usesOBJ = complete.getTag("portalUses");
		int uses = 0;
		if (usesOBJ==null) complete.setTag("portalUses", 0);
		else uses = (int) usesOBJ;
		complete.setTag("portalUses", ++uses);
		if (uses>=maxUses) {
			if (action.startsWith("Close")) {
				Dimensions.getCompletePortalManager().removePortal(complete, CustomPortalDestroyCause.PLUGIN, e.getEntity());
			} else if (action.startsWith("Destroy")) {

				Dimensions.getCompletePortalManager().removePortal(complete, CustomPortalDestroyCause.PLUGIN, e.getEntity());
				
				PortalGeometry geom = complete.getPortalGeometry();
				Vector min = geom.getMin();
				Vector max = geom.getMax();
				boolean zAxis = geom.iszAxis();
				
				for (double y=min.getY();y<=max.getY();y++) {
					for (double side=zAxis?min.getZ():min.getX();side<=(zAxis?max.getZ():max.getX());side++) {
						(new Location(complete.getWorld(), zAxis?min.getX():side, y, !zAxis?min.getZ():side)).getBlock().setType(Material.AIR);
					}
				}
			}

			if (action.contains("{explode%") && Math.random()<=Integer.parseInt(action.split("%")[1].replace("}", ""))/100) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
					
					@Override
					public void run() {
						complete.getWorld().createExplosion(complete.getCenter(), 5);
					}
				}, 1);
			}
		}
	}
	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {

		int uses = portalConfig.getInt("Addon.LimitedUses.MaxUses", 0);
		if (uses==0) return;

		setOption(portal, "limitedUses", uses);
		setOption(portal, "limitedUsesAction", portalConfig.getString("Addon.LimitedUses.Action", "Destroy{explode%100}"));
		
		
		return;
	}
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "13o9-9TQm-h0ZuxQIcrGX5sSGk91AhVXHkZVQgchTsZs";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "13oJ4UtBn5uBUvl-eF3cM3obHFymN6Ib8";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}
	
}
