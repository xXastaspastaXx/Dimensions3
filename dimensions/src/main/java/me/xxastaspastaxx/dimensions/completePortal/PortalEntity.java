package me.xxastaspastaxx.dimensions.completePortal;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * Represents one block inside the portal that is going to place a block or summon a fake block
 *
 */

public abstract class PortalEntity {
	
	private Location location;
	
	/**
	 * Constructor of PortalEntity
	 * @param location the location to place the block
	 */
	public PortalEntity(Location location) {
		this.location = location.clone().add(0.5f,0.5f,0.5f);
	}

	/**
	 * Place the block
	 * @param p the player to show the block
	 */
	public abstract void summon(Player p);
	
	/**
	 * Remove the block
	 * @param p the player to remove the block for
	 */
	public abstract void destroy(Player p);
	
	/**
	 * Remove the block for all players
	 */
	public abstract void destroyBroadcast();
	
	/**
	 * Make the portal emit particles
	 * @param color the color of the particle
	 */
	public void emitParticles(Color color) {
		Particle redstoneParticle = Particle.valueOf("REDSTONE") == null ? Particle.valueOf("DUST") : Particle.valueOf("REDSTONE");
		location.getWorld().spawnParticle(redstoneParticle, location.getX(),location.getY(),location.getZ(), 3, 0.5,0.5,0.5,new Particle.DustOptions(color,2));
	}
	
	/**
	 * Get the location of the PortalEntity
	 * @return the location of the PortalEntity
	 */
	public Location getLocation() {
		return location;
	}
	
}
