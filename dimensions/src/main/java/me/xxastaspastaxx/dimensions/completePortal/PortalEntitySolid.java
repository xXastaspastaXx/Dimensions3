package me.xxastaspastaxx.dimensions.completePortal;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class PortalEntitySolid extends PortalEntity {

	private BlockData blockdata;
	
	public PortalEntitySolid(Location location, BlockData blockData) {
		super(location);
		this.blockdata = blockData;
	}

	public void summon(Player p) {
		p.sendBlockChange(getLocation(), blockdata);
	}
	
	public void destroy(Player p) {
		p.sendBlockChange(getLocation(), Material.AIR.createBlockData());
	}

	public void destroyBroadcast() {
		getLocation().getBlock().setType(Material.AIR);
		Bukkit.getOnlinePlayers().forEach(p -> p.sendBlockChange(getLocation(), Material.AIR.createBlockData()));
	}
	
}
