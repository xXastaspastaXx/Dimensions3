package me.xxastaspastaxx.dimensions.completePortal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class PortalEntitySolid extends PortalEntity {

	private Location location;
	private BlockData blockdata;
	
	public PortalEntitySolid(Location location, BlockData blockData) {
		super();
		this.location = location;
		this.blockdata = blockData;
	}

	public void summon(Player p) {
		p.sendBlockChange(location, blockdata);
	}
	
	public void destroy(Player p) {
		p.sendBlockChange(location, Material.AIR.createBlockData());
	}

	public void destroyBroadcast() {
		location.getBlock().setType(Material.AIR);
	}
	
}
