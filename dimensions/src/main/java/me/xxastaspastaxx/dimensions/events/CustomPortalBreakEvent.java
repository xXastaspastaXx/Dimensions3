package me.xxastaspastaxx.dimensions.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalDestroyCause;

public class CustomPortalBreakEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private boolean cancelled = false;
	
	private CompletePortal completePortal;
	private CustomPortalDestroyCause cause;
	private Entity destroyer;
	
    public CustomPortalBreakEvent(CompletePortal completePortal, CustomPortalDestroyCause cause, Entity destroyer) {
    	this.completePortal = completePortal;
    	this.cause = cause;
    	this.destroyer = destroyer;
	}

    
    
	public CompletePortal getCompletePortal() {
		return completePortal;
	}



	public CustomPortalDestroyCause getCause() {
		return cause;
	}



	public Entity getDestroyer() {
		return destroyer;
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
