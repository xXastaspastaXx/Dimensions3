package me.xxastaspastaxx.dimensions.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;

public class CustomPortalUseEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private boolean cancelled = false;
	
	private CompletePortal completePortal;
	private Entity entity;

	private CompletePortal destinationPortal;
	
    public CustomPortalUseEvent(CompletePortal completePortal, Entity entity, CompletePortal destinationPortal) {
    	this.completePortal = completePortal;
    	this.entity = entity;
    	this.destinationPortal = destinationPortal;
	}

    
    
	public CompletePortal getCompletePortal() {
		return completePortal;
	}

	public Entity getEntity() {
		return entity;
	}
	
	public void setDestinationPortal(CompletePortal destinationPortal) {
		this.destinationPortal = destinationPortal;
	}

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
