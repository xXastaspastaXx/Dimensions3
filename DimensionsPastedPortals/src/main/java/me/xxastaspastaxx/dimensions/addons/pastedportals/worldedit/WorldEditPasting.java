package me.xxastaspastaxx.dimensions.addons.pastedportals.worldedit;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.block.BlockStateHolder;

public class WorldEditPasting {
	
	public WorldEditPasting() {
		WorldEdit.getInstance().getEventBus().register(this);
	}
	
	@Subscribe(priority = com.sk89q.worldedit.util.eventbus.EventHandler.Priority.VERY_EARLY)
	public void onEditSessionEvent(EditSessionEvent e) {
        Actor actor = e.getActor();
        if (actor != null && actor.isPlayer()) {
            e.setExtent(new WorldEditLogger(actor, e.getExtent(), true));
        }
    }
	
	@Subscribe(priority = com.sk89q.worldedit.util.eventbus.EventHandler.Priority.VERY_LATE)
	public void onEditSessionEventLate(EditSessionEvent e) {
        Actor actor = e.getActor();
        if (actor != null && actor.isPlayer()) {
            e.setExtent(new WorldEditLogger(actor, e.getExtent(), false));
        }
    }
	
}

abstract class AbstractLoggingExtent extends AbstractDelegateExtent {


    protected AbstractLoggingExtent(Extent extent) {
        super(extent);
    }
    

    protected <T extends BlockStateHolder<T>> void onBlockChange(BlockVector3 position, T block) {
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
    	onBlockChange(location, block);
    	return super.setBlock(location, block);
    }
    
}