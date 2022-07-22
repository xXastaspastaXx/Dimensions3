package me.xxastaspastaxx.dimensions.completePortal;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public abstract class PortalEntity {
	
	private Location location;
	
	public PortalEntity(Location location) {
		this.location = location;
	}

	public abstract void summon(Player p);
	
	public abstract void destroy(Player p);
	
	public abstract void destroyBroadcast();
	
	public void emitParticles(Color color) {
		location.getWorld().spawnParticle(Particle.REDSTONE, location.getX()+0.5f,location.getY()+0.5f,location.getZ()+0.5f, 3, 0.5,0.5,0.5,new Particle.DustOptions(color,2));
	}
	
	public Location getLocation() {
		return location;
	}
	
}
