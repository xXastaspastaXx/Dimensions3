package me.xxastaspastaxx.dimensions.addons.pastedportals.skyblock;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.api.IslandCreateEvent;
import com.iridium.iridiumskyblock.api.IslandDeleteEvent;
import com.iridium.iridiumskyblock.api.IslandRegenEvent;
import com.iridium.iridiumskyblock.database.Island;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.pastedportals.DimensionsPastedPortalsAddon;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalDestroyCause;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;

public class PastedIridium implements Listener  {
	
	DimensionsPastedPortalsAddon main;
	
	World world;
	
	public PastedIridium(DimensionsPastedPortalsAddon main) {
		this.main = main;
		
		world = IridiumSkyblockAPI.getInstance().getWorld();
		
		Bukkit.getServer().getPluginManager().registerEvents(this, main.getPlugin());
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onIslandCreated(IslandCreateEvent e) {
		
		onIslandCreate(e.getIslandName());
	}
	
	@EventHandler
    public void onIslandRegen(IslandRegenEvent e) {
    	onIslandDelete(e.getIsland());
    	onIslandCreate(e.getIsland().getName());
    }
    
    public void onIslandCreate(String islandName) {
    	Bukkit.getScheduler().runTaskLaterAsynchronously(main.getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				Island island = IridiumSkyblockAPI.getInstance().getIslandByName(islandName).get();

				Location min = island.getPos1(world);
				Location max = island.getPos2(world);
				
				for(int x = (int) Math.max(max.getBlockX(), min.getBlockX()); x >= (int) Math.min(min.getBlockX(), max.getBlockX()); x--) {
					for(int y = 200; y >= 50; y--) {
						for(int z = (int) Math.max(max.getBlockZ(), min.getBlockZ()); z >= (int) Math.min(min.getBlockZ(), max.getBlockZ()); z--) {
							Block block = new Location(world,x,y,z).getBlock();
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
		}, 20);
    }

    @EventHandler(ignoreCancelled = true)
    public void onIslandDeleted(IslandDeleteEvent e) {
    	onIslandDelete(e.getIsland());
    }
    
    public void onIslandDelete(Island island) {
    	@SuppressWarnings("unchecked")
		ArrayList<CompletePortal> toRemove = (ArrayList<CompletePortal>) Dimensions.getCompletePortalManager().getNearestPortals(island.getHome(), (int) island.getPos1(world).distance(island.getPos2(world))).clone();
    	for (CompletePortal complete : toRemove) {
    		Dimensions.getCompletePortalManager().removePortal(complete, CustomPortalDestroyCause.PLUGIN, null);
    	}
    }
}