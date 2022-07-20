package me.xxastaspastaxx.dimensions.completePortal;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class PortalEntity {
	
	private Location location;
	
	public PortalEntity(Location location) {
		this.location = location;
	}

	public abstract void summon(Player p);
	
	public abstract void destroy(Player p);
	
	public abstract void destroyBroadcast();
	
	public Location getLocation() {
		return location;
	}
	
}
