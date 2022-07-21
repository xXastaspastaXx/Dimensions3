package me.xxastaspastaxx.dimensions.addons.pastedportals.skyblock;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.bgsoftware.superiorskyblock.api.events.IslandChunkResetEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandCreateEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.pastedportals.DimensionsPastedPortalsAddon;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalDestroyCause;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;

public class PastedSuperiorSkyblock implements Listener  {
	
	DimensionsPastedPortalsAddon main;
	
	HashMap<Island, Boolean> creating = new HashMap<Island, Boolean>();
	
	public PastedSuperiorSkyblock(DimensionsPastedPortalsAddon main) {
		this.main = main;
		
		Bukkit.getServer().getPluginManager().registerEvents(this, main.getPlugin());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onIslandCreated(IslandCreateEvent e) {
		creating.put(e.getIsland(), true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onIslandRegen(IslandChunkResetEvent e) {

		Chunk chunk = e.getWorld().getChunkAt(e.getChunkX(),e.getChunkZ());	
		
		if (!e.getIsland().getAllChunks(Environment.NORMAL, true, false).contains(chunk)) return;
		
    	Bukkit.getScheduler().runTaskAsynchronously(main.getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				while (!e.getIsland().getLoadedChunks(Environment.NORMAL, true, false).contains(chunk) && creating.containsKey(e.getIsland())) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}

		        for (int X = 0; X < 16; X++) {
		        	for (int Z = 0; Z < 16; Z++) {
		        		for (int Y = 1; Y < 255; Y++) {
	        				Block block = chunk.getBlock(X, Y, Z);
	            			if (creating.get(e.getIsland())) {
	                        	if (block.getType()!=Material.OAK_WALL_SIGN) continue;
	                        	
	                        	Sign signData = (Sign) block.getState().getData();
	                        	
	                        	if (!signData.getLine(0).contentEquals("[DIMENSIONS]")) continue;
	    	                	
	    	                	Bukkit.getScheduler().runTask(main.getPlugin(), new Runnable() {
									
									@Override
									public void run() {
										block.setType(Material.AIR);
										CustomPortal portal = Dimensions.getCustomPortalManager().getCustomPortal(signData.getLine(1));
					                	if (portal!=null) {
					                		PortalGeometry temp = PortalGeometry.getPortalGeometry().getPortal(portal, block.getLocation());
					                		if (temp!=null)
					                			Dimensions.getCompletePortalManager().createNew(new CompletePortal(portal, block.getWorld(), temp), null, CustomPortalIgniteCause.PLUGIN, null);
					                	}
									}
								});
	            			} else {
	            				CompletePortal complete = Dimensions.getCompletePortalManager().getCompletePortal(block.getLocation(), true, true);
	            				if (complete!=null) {
	            					Bukkit.getScheduler().runTask(main.getPlugin(), new Runnable() {
										
										@Override
										public void run() {
											Dimensions.getCompletePortalManager().removePortal(complete, CustomPortalDestroyCause.PLUGIN, null);
											
										}
									});
	            				}
	            			}
		        		}
		            }
				}
		        creating.remove(e.getIsland());
			}
		});

    }
    
    @EventHandler(ignoreCancelled = true)
    public void onIslandDeleted(IslandDisbandEvent e) {
		creating.put(e.getIsland(), false);
    }
}