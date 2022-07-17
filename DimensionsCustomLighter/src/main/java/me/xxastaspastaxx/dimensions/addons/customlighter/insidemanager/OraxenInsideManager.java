package me.xxastaspastaxx.dimensions.addons.customlighter.insidemanager;

import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanicFactory;

public class OraxenInsideManager extends InsideManager {

	private NoteBlockMechanic noteBlockMechanic;
	
	public OraxenInsideManager(int customVariation) {

		noteBlockMechanic = NoteBlockMechanicFactory.getBlockMechanic(customVariation);
		blockData = NoteBlockMechanicFactory.createNoteBlockData(customVariation);
	}
	
	public NoteBlockMechanic getMechanic() {
		return noteBlockMechanic;
	}
	
}
