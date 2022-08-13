package me.xxastaspastaxx.dimensions.addons.exampleaddon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;

/**
 * Example addon
 * An addon that will summon explosion particles when a player ignites a portal with permission
 * 
 * @author astaspasta
 *
 */

public class DimensionsExampleAddonMain extends DimensionsAddon implements Listener {
	
	//private Plugin pl;
	
	public DimensionsExampleAddonMain() {
		super("ExampleAddon", "0.0.1", "An example addon", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		//this.pl = pl;
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void postPortalIgnite(CustomPortalIgniteEvent e) {
		
		//We check if the entity is a player, if its not we skip
		//If the player does not have permission, we also skip
		if (!(e.getEntity() instanceof Player) || !e.getEntity().hasPermission("dimensions.exampleaddon.explosion")) return;
		
		//Everything looks fine so we summon the explosion
		CompletePortal complete = e.getCompletePortal(); //We get the complete portal
		Location location = complete.getCenter(); //We get the center of the portal
		
		//We summon the particles
		location.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, location, 5);
	}
	
	
}
