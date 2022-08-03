package me.xxastaspastaxx.dimensions.addons.customlighter.framemanager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;

import io.th0rgal.oraxen.compatibilities.provided.lightapi.WrappedLightAPI;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanicFactory;

public class OraxenFrameManager extends FrameManager {

	private NoteBlockMechanic noteBlockMechanic;
	
	public OraxenFrameManager(int customVariation) {
		
		noteBlockMechanic = NoteBlockMechanicFactory.getBlockMechanic(customVariation);
		
	}
	
	public NoteBlockMechanic getMechanic() {
		return noteBlockMechanic;
	}

	@Override
	public boolean isAccepted(Block block) {
		
        if (block.getType() != Material.NOTE_BLOCK)
            return false;
        final NoteBlock noteBlok = (NoteBlock) block.getBlockData();
        @SuppressWarnings("deprecation")
		final NoteBlockMechanic noteBlockMechanic = NoteBlockMechanicFactory
                .getBlockMechanic((int) (noteBlok.getInstrument().getType()) * 25
                        + (int) noteBlok.getNote().getId() + (noteBlok.isPowered() ? 400 : 0) - 26);
        if (noteBlockMechanic == null)
            return false;
        
        return noteBlockMechanic.getCustomVariation()==this.noteBlockMechanic.getCustomVariation();
        
	}
	
	@Override
	public void placeBlock(Block target) {

        // determines the new block data of the block
        final int customVariation = noteBlockMechanic.getCustomVariation();

        target.setBlockData(NoteBlockMechanicFactory.createNoteBlockData(customVariation), false);
        
        
        //if (noteBlockMechanic.hasPlaceSound())
            //placedBlock.getWorld().playSound(placedBlock.getLocation(), noteBlockMechanic.getPlaceSound(), 1.0f, 0.8f);

        if (target != null && noteBlockMechanic.getLight() != -1) {
            WrappedLightAPI.createBlockLight(target.getLocation(), noteBlockMechanic.getLight());
        }
	}
	
}
