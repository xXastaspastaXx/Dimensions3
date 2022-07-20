package me.xxastaspastaxx.dimensions.addons.particles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.completePortal.PortalEntity;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.events.CustomPortalBreakEvent;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;

public class DimensionsParticles extends DimensionsAddon implements Listener {
	
private Plugin pl;
	
	HashMap<CompletePortal, ArrayList<Integer>> tasks = new HashMap<CompletePortal, ArrayList<Integer>>();
	
	public DimensionsParticles() {
		super("DimensionsUnbreakableAddon", "3.0.0", "Unbreakable portals", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		this.pl = pl;
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void postIgnitePortal(CustomPortalIgniteEvent e) {
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		
		Object particlesOBJ = getOption(portal, "particlePacks");
		if (particlesOBJ==null) return;
		@SuppressWarnings("unchecked")
		ArrayList<ParticlePack> packs = (ArrayList<ParticlePack>) particlesOBJ;
		
		complete.setTag("hidePortalParticles", true);
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		for (ParticlePack pack : packs) {
			list.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {
			
				@Override
				public void run() {
					pack.runPortal(complete, complete.getCenter());

					for (PortalEntity en : complete.getPortalEntities()) {
						pack.runTile(complete, en.getLocation());
					}
					
				}
			}, 0, pack.vars.get("frequency").intValue()));
		}
		
		tasks.put(complete, list);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void postDestroyPortal(CustomPortalBreakEvent e) {
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		
		Object particlesOBJ = getOption(portal, "particlePacks");
		if (particlesOBJ==null) return;
		
		complete.setTag("hidePortalParticles", null);	
		for (int i : tasks.get(complete)) {
			Bukkit.getScheduler().cancelTask(i);
		}
		tasks.remove(complete);
	}
	
	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {

		ArrayList<ParticlePack> spl = ParticlePack.load(portalConfig.getStringList("Addon.Particles"));
		
		
		if(spl.isEmpty()) return;
		
		setOption(portal, "particlePacks", spl);
		
		return;
	}
	
	@Override
	public void onDisable() {
		for (CompletePortal completePortal : Dimensions.getCompletePortalManager().getCompletePortals()) {
			if (!tasks.containsKey(completePortal)) continue;
			for (int i : tasks.get(completePortal)) {
				Bukkit.getScheduler().cancelTask(i);
			}
		}
	}
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "1Bd-tQ7Weq99lqHCymBZA-ZfMwbxRRF_qbHY1arhefX0";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "1n1lkfTT7xEJjrOu07nvz1A4QQpemc1hG";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}
	
}
