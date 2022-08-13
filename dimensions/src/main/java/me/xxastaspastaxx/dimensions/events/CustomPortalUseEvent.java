package me.xxastaspastaxx.dimensions.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;

/**
 * The event beign called when a portal is being used
 * @author astas
 *
 */
public class CustomPortalUseEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private boolean cancelled = false;
	
	private CompletePortal completePortal;
	private Entity entity;

	private CompletePortal destinationPortal;
	
	/**
	 * The constructor of the event
	 * @param completePortal the portal being used
	 * @param entity the entity using the portal
	 * @param destinationPortal the destination of the portal
	 */
    public CustomPortalUseEvent(CompletePortal completePortal, Entity entity, CompletePortal destinationPortal) {
    	this.completePortal = completePortal;
    	this.entity = entity;
    	this.destinationPortal = destinationPortal;
	}

    
    /**
     * Get the portal being used
     */
	public CompletePortal getCompletePortal() {
		return completePortal;
	}

	/**
	 * Get the entity using the portal
	 */
	public Entity getEntity() {
		return entity;
	}
	
	/**
	 * Override the destination portal
	 * @param destinationPortal the new portal to teleport to
	 */
	public void setDestinationPortal(CompletePortal destinationPortal) {
		this.destinationPortal = destinationPortal;
	}

	/**
	 * Get the destination portal
	 */
	public CompletePortal getDestinationPortal() {
		return destinationPortal;
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
		this.cancelled = arg0;
	}
	
	
}
