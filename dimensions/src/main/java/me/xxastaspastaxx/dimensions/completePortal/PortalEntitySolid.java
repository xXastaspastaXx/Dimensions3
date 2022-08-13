package me.xxastaspastaxx.dimensions.completePortal;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

/**
 * PortalEntity class for blocks that are not solid and dont have to be spawned as falling blocks
 *
 */

public class PortalEntitySolid extends PortalEntity {

	private BlockData blockdata;
	
	/**
	 * Construct PortalEntity with the blockData to place
	 * @param location the location of the block
	 * @param blockData the block data to place
	 */
	public PortalEntitySolid(Location location, BlockData blockData) {
		super(location);
		this.blockdata = blockData;
	}

	/**
	 * Send block change (block data) to the player
	 */
	public void summon(Player p) {
		p.sendBlockChange(getLocation(), blockdata);
	}
	
	/**
	 * Send block change (air) to the player
	 */
	public void destroy(Player p) {
		p.sendBlockChange(getLocation(), Material.AIR.createBlockData());
	}

	/**
	 * Send block change (air) to all players
	 */
	public void destroyBroadcast() {
		getLocation().getBlock().setType(Material.AIR);
		Bukkit.getOnlinePlayers().forEach(p -> p.sendBlockChange(getLocation(), Material.AIR.createBlockData()));
	}
	
}
