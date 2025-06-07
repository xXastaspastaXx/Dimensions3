package me.xxastaspastaxx.dimensions.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;

/**
 * Event called when a portal is being ignited
 *
 */

public class CustomPortalIgniteEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private boolean cancelled = false;
	
	private CompletePortal completePortal;
	private CustomPortalIgniteCause cause;
	private Entity entity;
	private ItemStack lighter;
	
	/**
	 * Constructor of the event
	 * @param completePortal portal being ignited
	 * @param cause the cause igniting the portal
	 * @param entity the entity causing the ignite
	 * @param item the item used to ignite
	 */
    public CustomPortalIgniteEvent(CompletePortal completePortal, CustomPortalIgniteCause cause, Entity entity, ItemStack item) {
    	this.completePortal = completePortal;
    	this.cause = cause;
    	this.entity = entity;
    	this.lighter = item;
	}

    
    /**
     * Get the portal being ignited
     */
	public CompletePortal getCompletePortal() {
		return completePortal;
	}
	
	/**
	 * Replace the portal being ignited with a new portal instance
	 * @param newPortal instance of the new portal
	 */
	public void replaceCompletePortal(CompletePortal newPortal) {
		this.completePortal = newPortal;
	}
	
	/**
	 * The cause igniting the portal
	 */
	public CustomPortalIgniteCause getCause() {
		return cause;
	}
	
	/**
	 * The entity igniting the portal
	 * @return null if no entity was involved
	 */
	public Entity getEntity() {
		return entity;
	}
	/**
	 * The item used to ignite the portal
	 * @return null if no item was involved
	 */
	public ItemStack getLighter() {
		return lighter;
	}



	public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }



	@Override
	public boolean isCancelled() {
		return cancelled;
	}



	@Override
	public void setCancelled(boolean arg0) {
		if (arg0 && this.cause == CustomPortalIgniteCause.EXIT_PORTAL) {
			return;
		}
		this.cancelled = arg0;
	}
	
	
	
}
