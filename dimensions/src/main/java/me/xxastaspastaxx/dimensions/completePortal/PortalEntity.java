package me.xxastaspastaxx.dimensions.completePortal;

import org.bukkit.entity.Player;

public abstract class PortalEntity {
	
	public PortalEntity() {
	}

	public abstract void summon(Player p);
	
	public abstract void destroy(Player p);
	
	public abstract void destroyBroadcast();
	
}
