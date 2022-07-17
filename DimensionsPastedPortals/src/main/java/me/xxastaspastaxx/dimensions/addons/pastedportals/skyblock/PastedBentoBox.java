package me.xxastaspastaxx.dimensions.addons.pastedportals.skyblock;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.pastedportals.DimensionsPastedPortalsAddon;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalDestroyCause;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;
import world.bentobox.bentobox.api.events.island.IslandCreateEvent;
import world.bentobox.bentobox.api.events.island.IslandDeleteEvent;
import world.bentobox.bentobox.api.events.island.IslandResetEvent;
import world.bentobox.bentobox.api.events.island.IslandResettedEvent;
import world.bentobox.bentobox.database.objects.Island;

public class PastedBentoBox implements Listener  {
	
	DimensionsPastedPortalsAddon main;
	
	public PastedBentoBox(DimensionsPastedPortalsAddon main) {
		this.main = main;
		
		Bukkit.getServer().getPluginManager().registerEvents(this, main.getPlugin());
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onIslandCreated(IslandCreateEvent e) {
    	//blueprintBundle.put(e.getIsland(), e.getBlueprintBundle());
		onIslandCreate(e.getIsland());
	}
	
	@EventHandler(ignoreCancelled = true)
    public void onIslandReseted(IslandResettedEvent e) {
    	onIslandDelete(e.getIsland());
    	onIslandCreate(e.getIsland());
    }
    
   // HashMap<Island, BlueprintBundle> blueprintBundle = new HashMap<Island, BlueprintBundle>(); 
    @EventHandler(ignoreCancelled = true)
    public void onIslandReseted(IslandResetEvent e) {

    	//blueprintBundle.put(e.getIsland(), e.getBlueprintBundle());
    }
    
    
    public void onIslandCreate(Island island) {
    	
		Location min = new Location(island.getWorld(), island.getMinProtectedX(), 0, island.getMinProtectedZ());
		Location max = new Location(island.getWorld(), island.getMaxProtectedX(), 0, island.getMaxProtectedZ());
		
		Bukkit.getScheduler().runTaskAsynchronously(main.getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				for(int x = (int) Math.max(max.getBlockX(), min.getBlockX()); x >= (int) Math.min(min.getBlockX(), max.getBlockX()); x--) {
					for(int y = 200; y >= 50; y--) {
						for(int z = (int) Math.max(max.getBlockZ(), min.getBlockZ()); z >= (int) Math.min(min.getBlockZ(), max.getBlockZ()); z--) {
							Block block = new Location(island.getWorld(),x,y,z).getBlock();
		                	if (block.getType()!=Material.OAK_WALL_SIGN) continue;
		                	
		                	Sign signData = (Sign) block.getState().getData();
		                	
		                	if (!signData.getLine(0).contentEquals("[DIMENSIONS]")) continue;
		                	
		                	Bukkit.getScheduler().runTask(main.getPlugin(), new Runnable() {
								
								@Override
								public void run() {
				                	block.setType(Material.AIR);
				                	CustomPortal portal = Dimensions.getCustomPortalManager().getCustomPortal(signData.getLine(1));
				                	if (portal!=null) {
				                		PortalGeometry temp = PortalGeometry.getPortal(portal, block.getLocation());
				                		if (temp!=null)
				                			Dimensions.getCompletePortalManager().createNew(new CompletePortal(portal, block.getWorld(), temp), null, CustomPortalIgniteCause.PLUGIN, null);
				                	}
								}
							});
					    }
				    }
				}
				
			}
		});
    }

    @EventHandler(ignoreCancelled = true)
    public void onIslandDeleted(IslandDeleteEvent e) {
    	onIslandDelete(e.getIsland());
    }
    
    public void onIslandDelete(Island island) {
    	@SuppressWarnings("unchecked")
		ArrayList<CompletePortal> toRemove = (ArrayList<CompletePortal>) Dimensions.getCompletePortalManager().getNearestPortals(island.getCenter(), island.getRange()).clone();
    	for (CompletePortal complete : toRemove) {
    		Dimensions.getCompletePortalManager().removePortal(complete, CustomPortalDestroyCause.PLUGIN, null);
    	}
    }
}