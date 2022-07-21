package me.xxastaspastaxx.dimensions.addons.pastedportals.worldedit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockStateHolder;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalDestroyCause;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;

public class WorldEditLogger extends AbstractLoggingExtent {
    private final Actor actor;
    private final boolean early;
    
    public WorldEditLogger(Actor actor, Extent extent, boolean early) {
        super(extent);
        this.actor = actor;
        this.early = early;
    }
    
    @Override
    protected <T extends BlockStateHolder<T>> void onBlockChange(final BlockVector3 position, T block) {
        final Player p = Bukkit.getServer().getPlayer(actor.getName());
        Block oldBlock = p.getWorld().getBlockAt(position.getBlockX(), position.getBlockY(), position.getBlockZ());
        //System.out.println(actor.getName() + " set block @ " + position +  " from " + oldBlock + " to " + block);
        

		if (early)  {
	        for (BlockFace face : new BlockFace[]{BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.DOWN, BlockFace.UP})
	        {
	        	Block relative = oldBlock.getRelative(face);
	        	CompletePortal portal = Dimensions.getCompletePortalManager().getCompletePortal(relative.getLocation(), true, false);
	        	if (portal!=null) {
	        		Dimensions.getCompletePortalManager().removePortal(portal, CustomPortalDestroyCause.PLUGIN, p);
	        	}
	        }
		}
		
		if (!early) {
			String materialName = block.getBlockType().getId().toUpperCase();
			if (materialName.startsWith("MINECRAFT:")) {
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(Dimensions.getInstance(), new Runnable() {
					
					public void run() {
						Block pastedBlock = p.getWorld().getBlockAt(position.getBlockX(), position.getBlockY(), position.getBlockZ());
						if (pastedBlock.getType()!=Material.OAK_WALL_SIGN) return;
	                	
	                	Sign signData = (Sign) pastedBlock.getState().getData();
	                	
	                	if (!signData.getLine(0).contentEquals("[DIMENSIONS]")) return;
	                	
	                	pastedBlock.setType(Material.AIR);
	                	
	                	CustomPortal portal = Dimensions.getCustomPortalManager().getCustomPortal(signData.getLine(1));
	                	if (portal==null) return;
	                	
	                	if (portal!=null) {
	                		PortalGeometry temp = PortalGeometry.getPortalGeometry().getPortal(portal, pastedBlock.getLocation());
	                		if (temp!=null)
	                			Dimensions.getCompletePortalManager().createNew(new CompletePortal(portal, pastedBlock.getWorld(), temp), p, CustomPortalIgniteCause.PLUGIN, null);
	                	}
					}
				}, 20);
			}
		}
		
    }

}