package me.xxastaspastaxx.dimensions.addons.biomes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsDebbuger;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;
import me.xxastaspastaxx.dimensions.events.CustomPortalUseEvent;

public class DimensionsBiomes extends DimensionsAddon implements Listener {
	
	//private Plugin pl;
	
	public DimensionsBiomes() {
		super("DimensionsBiomesAddon", "3.0.0", "Disable portals in certain biomes", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		//this.pl = pl;
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	
	@SuppressWarnings("unchecked")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPortalIgnite(CustomPortalIgniteEvent e) {
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object allowedBiomes = getOption(portal, "biomesEnabled");
		if (allowedBiomes==null) return;

		Entity entity = e.getEntity();
		
		ArrayList<Biome> enabled = (ArrayList<Biome>) getOption(portal, "biomesEnabled");
		ArrayList<Biome> disabled = (ArrayList<Biome>) getOption(portal, "biomesDisabled");
		
		Biome biome = complete.getCenter().getBlock().getBiome();
		if (disabled.contains(biome) || (!enabled.isEmpty() && !enabled.contains(biome))) {
			if (entity instanceof Player) entity.sendMessage((String) getOption(portal, "biomesDenyMessage"));
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPortalUse(CustomPortalUseEvent e) {
		
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object allowedBiomes = getOption(portal, "biomesEnabled");
		if (allowedBiomes==null) return;

		Entity entity = e.getEntity();
		
		ArrayList<Biome> enabled = (ArrayList<Biome>) getOption(portal, "biomesEnabled");
		ArrayList<Biome> disabled = (ArrayList<Biome>) getOption(portal, "biomesDisabled");
		
		Biome biome = complete.getCenter().getBlock().getBiome();
		if (disabled.contains(biome) || (!enabled.isEmpty() && !enabled.contains(biome))) {
			if (entity instanceof Player) entity.sendMessage((String) getOption(portal, "biomesDenyMessage"));
			e.setCancelled(true);
		}
	}

	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {

		String allowedBiomes = portalConfig.getString("Addon.Biomes.Allowed", "all");
		if (allowedBiomes.contentEquals("all")) return;

		ArrayList<Biome> disabled = new ArrayList<Biome>();
		ArrayList<Biome> enabled = new ArrayList<Biome>();
		
		try {
			if (allowedBiomes.contains("!")) {
				for (String str : allowedBiomes.split(", ")) {
					if (str.startsWith("!")) disabled.add(Biome.valueOf(str.toUpperCase().replace("!", "")));
				}
			}
			
			
			if (allowedBiomes.contains("all") && allowedBiomes.contains("!")) {
				enabled = new ArrayList<Biome>(Arrays.asList(Biome.values()));
				for (Biome biome : disabled) {
					enabled.remove(biome);
				}
			} else {
				if (!allowedBiomes.contains(", ")) {
					if (!allowedBiomes.startsWith("!")) enabled.add(Biome.valueOf(allowedBiomes.toUpperCase()));
				} else {
					for (String str : allowedBiomes.toUpperCase().split(", ")) {
						if (!str.startsWith("!")) enabled.add(Biome.valueOf(str));
					}
				}
			}
		} catch (IllegalArgumentException e) {
			DimensionsDebbuger.debug("BiomesAddon error: "+e.getMessage(), DimensionsDebbuger.HIGH);
		}

		setOption(portal, "biomesEnabled", enabled);
		setOption(portal, "biomesDisabled", disabled);
		setOption(portal, "biomesDenyMessage", portalConfig.getString("Addon.Biomes.DenyMessage", "The portal cannot be activate in this biomes").replace("&", "§"));
		
		return;
	}
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "1w8TBlPhuYzqCZUkHIz6BLQNUV_UK9VLIw2H3GU3TAwQ";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "1WkriMT9RgZQIkKGfDytfTSLGg8M4qXWw";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}
	
}
