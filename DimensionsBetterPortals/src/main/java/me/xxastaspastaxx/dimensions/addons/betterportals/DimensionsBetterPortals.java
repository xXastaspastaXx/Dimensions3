package me.xxastaspastaxx.dimensions.addons.betterportals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.lauriethefish.betterportals.api.BetterPortal;
import com.lauriethefish.betterportals.api.BetterPortalsAPI;
import com.lauriethefish.betterportals.api.PortalDirection;
import com.lauriethefish.betterportals.api.PortalPosition;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalDestroyCause;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;
import me.xxastaspastaxx.dimensions.events.CustomPortalBreakEvent;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;
import me.xxastaspastaxx.dimensions.events.CustomPortalUseEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class DimensionsBetterPortals extends DimensionsAddon implements Listener {
	
	private Plugin pl;
	
	private BetterPortalsAPI bpAPI;

    HashMap<CompletePortal, BukkitTask> tasks = new HashMap<CompletePortal, BukkitTask>();
    //ArrayList<CompletePortal> used = new ArrayList<CompletePortal>();
	
	public DimensionsBetterPortals() {
		super("DimensionsBetterPortalsAddon", "3.0.1", "Hook for the better portals plugin", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public boolean onLoad(Dimensions main) {
		this.pl = main;
		
		return main.getServer().getPluginManager().getPlugin("BetterPortals") != null;
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		Dimensions.getCommandManager().registerCommand("Portal commands", new MirrorPortalCommand("mirror", "", new String[0], "Make the portal look the other way", "", true, this));
		try {
            this.bpAPI = BetterPortalsAPI.get();
		}   catch(IllegalStateException ex) {
			ex.printStackTrace();
        }
		
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPortalUse(CustomPortalUseEvent e) {
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object temp = getOption(portal, "betterPortal");
		if (temp==null) return;
		
		//if (used.contains(completePortal)) {
		//	return 1;
		//}
		
		if ((tasks.containsKey(complete)  && Bukkit.getScheduler().isCurrentlyRunning(tasks.get(complete).getTaskId())) || (complete.getTag("betterPortal")!=null && ((boolean) complete.getTag("betterPortal")))) {
			e.setCancelled(true);
		}
	}
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void postIgnitePortal(CustomPortalIgniteEvent e) {
		if (e.getCause()==CustomPortalIgniteCause.EXIT_PORTAL) return;
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object temp = getOption(portal, "betterPortal");
		if (temp==null) return;
		
		@SuppressWarnings("unchecked")
		HashMap<CompletePortal, UUID> map = (HashMap<CompletePortal, UUID>) temp;
		
		if (map.containsKey(complete)) return;

		CompletePortal linked = complete.getLinkedPortal();
		if (linked!=null) {
			link(complete, linked, false);
			return;
		}
		
		if (e.getCause()==CustomPortalIgniteCause.LOAD_PORTAL) return;
		
		Entity entity = e.getEntity();
		
		tasks.put(complete, Bukkit.getScheduler().runTask(pl, new Runnable() {
			
			@Override
			public void run() {
				if (entity!=null && entity instanceof Player) 
					((Player) entity).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Creating exit portal...."));

				CompletePortal tpPortal = complete.getDestinationPortal(true, null, null);
				
				if (tpPortal==null) return;
				
				if (tpPortal.getLinkedPortal()==null || tpPortal.getLinkedPortal().equals(complete)) {
					link(complete, tpPortal, false);
				}
			}
		}));
		
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void postUsePortal(CustomPortalUseEvent e) {
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object temp = getOption(portal, "betterPortal");
		if (temp==null) return;

		//if (used.contains(completePortal)) return;
		
		@SuppressWarnings("unchecked")
		HashMap<CompletePortal, UUID> map = (HashMap<CompletePortal, UUID>) temp;
		

		if (map.containsKey(complete)) return;
		
		CompletePortal linked = complete.getLinkedPortal();
		if (linked==null) return;
	    link(complete, linked, false);
	    
	}
	
	public void link(CompletePortal completePortal, CompletePortal linked, boolean mirrored) {
		PortalGeometry geom = completePortal.getPortalGeometry();
		PortalGeometry geom2 = linked.getPortalGeometry();
		if (geom.getPortalWidth()!=geom2.getPortalWidth() || geom.getPortalHeight()!=geom2.getPortalHeight()) return;
		
		completePortal.setLinkedPortal(linked);
		linked.setLinkedPortal(completePortal);
		
		PortalPosition originPos = new PortalPosition(
	           completePortal.getCenter(),
	           geom.iszAxis()?PortalDirection.EAST:PortalDirection.NORTH
	    );

	    PortalPosition destinationPos = new PortalPosition(
	    		linked.getCenter(),
	            !mirrored?(geom2.iszAxis()?PortalDirection.EAST:PortalDirection.NORTH):(geom2.iszAxis()?PortalDirection.WEST:PortalDirection.SOUTH)
	    );
	    
	    Vector size = geom.getMax().clone().subtract(geom.getMin());
	    //if (geom.iszAxis()) size.setX(size.getZ());
	    size.subtract(new Vector(1, 1, 1));
	    
	    BetterPortal betterPortal = bpAPI.createPortal(
	    		originPos,
	    		destinationPos,
	    		 originPos.getDirection().swapVector(size), UUID.randomUUID(), "dimensions"
		    );
	    
	    BetterPortal betterPortal2 = bpAPI.createPortal(
		    	destinationPos,
		    	originPos,
		    	destinationPos.getDirection().swapVector(size), UUID.randomUUID(), "dimensions"
		    );
	    

		@SuppressWarnings("unchecked")
		HashMap<CompletePortal, UUID> map = (HashMap<CompletePortal, UUID>) getOption(completePortal.getCustomPortal(), "betterPortal");

	    if (map.containsKey(completePortal)) {
			BetterPortal bt = bpAPI.getPortalById(map.get(completePortal));
			completePortal.setTag("betterPortal", null);
			completePortal.setTag("hidePortalInside", null);
			completePortal.setTag("hidePortalParticles", null);
			completePortal.setTag("mirrored", null);
			
			if (bt!=null) 
				bt.remove(false);
	    }
	    
	    if (map.containsKey(linked)) {
			BetterPortal bt = bpAPI.getPortalById(map.get(linked));
			linked.setTag("betterPortal", null);
			linked.setTag("hidePortalInside", null);
			linked.setTag("hidePortalParticles", null);
			linked.setTag("mirrored", null);
			
			if (bt!=null) 
				bt.remove(false);
	    }


	    map.put(completePortal, betterPortal.getId());
	    map.put(linked, betterPortal2.getId());
	    completePortal.setTag("betterPortal", true);
	    linked.setTag("betterPortal", true);
	    
	    completePortal.setTag("hidePortalInside", true);
	    linked.setTag("hidePortalInside", true);

	    completePortal.setTag("hidePortalParticles", true);
	    linked.setTag("hidePortalParticles", true);
	    

	    if (mirrored) {
	    	completePortal.setTag("mirrored", true);
		    linked.setTag("mirrored", true);
	    }

	    completePortal.destroy(null);
	    
	}
	
	public void unlink(CompletePortal complete) {
		CustomPortal portal = complete.getCustomPortal();
		Object temp = getOption(portal, "betterPortal");
		if (temp==null) return;
		
		@SuppressWarnings("unchecked")
		HashMap<CompletePortal, UUID> map = (HashMap<CompletePortal, UUID>) temp;

		if (!map.containsKey(complete)) return;
		BetterPortal bt = bpAPI.getPortalById(map.get(complete));
		
		if (bt!=null) 
			bt.remove(false);
		
		BetterPortal bt2 = bpAPI.getPortalById(map.get(complete.getLinkedPortal()));
		if (bt2!=null) {
			bt2.remove(false);
			complete.getLinkedPortal().setTag("betterPortal", null);
			complete.getLinkedPortal().setTag("hidePortalInside", null);
			complete.getLinkedPortal().setTag("hidePortalParticles", null);
			complete.getLinkedPortal().setTag("mirrored", null);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPortalDestroy(CustomPortalBreakEvent e) {
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object temp = getOption(portal, "betterPortal");
		if (temp==null) return;
		
		if (tasks.containsKey(complete) && Bukkit.getScheduler().isCurrentlyRunning(tasks.get(complete).getTaskId())) {
			e.setCancelled(true);
			return;
		}
		
		@SuppressWarnings("unchecked")
		HashMap<CompletePortal, UUID> map = (HashMap<CompletePortal, UUID>) temp;
	
		if (!map.containsKey(complete)) return;
		BetterPortal bt = bpAPI.getPortalById(map.get(complete));
		if (bt!=null && e.getCause()==CustomPortalDestroyCause.PLAYER_INSIDE)
			e.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void postDestroyPortal(CustomPortalBreakEvent e) {
		
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object temp = getOption(portal, "betterPortal");
		if (temp==null) return;
		
		@SuppressWarnings("unchecked")
		HashMap<CompletePortal, UUID> map = (HashMap<CompletePortal, UUID>) temp;

		if (!map.containsKey(complete)) return;
		BetterPortal bt = bpAPI.getPortalById(map.get(complete));
		
		if (bt!=null) 
			bt.remove(false);
		
		BetterPortal bt2 = bpAPI.getPortalById(map.get(complete.getLinkedPortal()));
		if (bt2!=null) {
			bt2.remove(false);
			complete.getLinkedPortal().setTag("betterPortal", null);
			complete.getLinkedPortal().setTag("hidePortalInside", null);
			complete.getLinkedPortal().setTag("hidePortalParticles", null);
			complete.getLinkedPortal().setTag("mirrored", null);
		}
		
	}

	@Override
	public void onDisable() {
		//Dimensions.getCommandManager().unregisterCommand("Portal commands", cmd);
		
		for (CustomPortal portal : Dimensions.getCustomPortalManager().getCustomPortals()) {
			Object temp = getOption(portal, "betterPortal");
			if (temp==null) continue;
			
			@SuppressWarnings("unchecked")
			HashMap<CompletePortal, UUID> map = (HashMap<CompletePortal, UUID>) temp;
			
			for (CompletePortal compl : map.keySet()) {
				try {
					bpAPI.getPortalById(map.get(compl)).remove(false);
					compl.setTag("betterPortal", null);
					compl.setTag("hidePortalInside", null);
					compl.setTag("hidePortalParticles", null);
					compl.setTag("mirrored", null);
				} catch (NullPointerException e) {
					
				}
			}
		}
	}
	
	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {

		if (!portalConfig.getBoolean("Addon.EnableBetterPortals", true)) return;
		
		setOption(portal, "betterPortal", new HashMap<CompletePortal, UUID>());
	}
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "1JIHziATYQYWhMFU5wYv8oWTG_5TC1hkxpPt5tyoV9bs";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "1kYPAOaMaVzl1P5CfKCEvdbz41rCmMmif";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}
	
}
