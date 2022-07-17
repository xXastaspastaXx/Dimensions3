package me.xxastaspastaxx.dimensions.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;

public class CustomPortalIgniteEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private boolean cancelled = false;
	
	private CompletePortal completePortal;
	private CustomPortalIgniteCause cause;
	private Entity entity;
	
    public CustomPortalIgniteEvent(CompletePortal completePortal, CustomPortalIgniteCause cause, Entity entity) {
    	this.completePortal = completePortal;
    	this.cause = cause;
    	this.entity = entity;
	}

    
    
	public CompletePortal getCompletePortal() {
		return completePortal;
	}

	public CustomPortalIgniteCause getCause() {
		return cause;
	}
	
	public Entity getEntity() {
		return entity;
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
