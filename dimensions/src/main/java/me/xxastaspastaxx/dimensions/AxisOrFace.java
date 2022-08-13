package me.xxastaspastaxx.dimensions;

import org.bukkit.Axis;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;

/**
 * Is used to combine <a href="https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/Orientable.html">Orientable</a> & <a href="https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/Directional.html">Directional</a>
 * 
 */
public class AxisOrFace {

	private Axis axis;
	private BlockFace face;
	
	/**
	 * Stores the axis or the face if there is a value of the given string in any of these enums
	 * 
	 * @param axisOrFace String value of <a href="https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Axis.html">Axis</a> or <a href="https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/BlockData.html">BlockFace</a>
	 */
	public AxisOrFace(String axisOrFace) {
		
		try {
			axis = Axis.valueOf(axisOrFace.toUpperCase());
		} catch(IllegalArgumentException e) {}
		
		try {
			face = BlockFace.valueOf(axisOrFace.toUpperCase());
		} catch(IllegalArgumentException e) {}
		
	}
	
	private Axis getAxis() {
		return axis;
	}
	
	private boolean isAxis() {
		return axis!=null;
	}
	
	private BlockFace getFace() {
		return face;
	}
	
	private boolean isFace() {
		return face!=null;
	}
	
	/**
	 * Changes the block data to be placed either along the set Axis or facing the set BlockFace
	 * 
	 * @param blockData the BlockData that is going to be corrected
	 * @return the BlockData with the correct Axis/Face
	 */
	public BlockData getNewData(BlockData blockData) {
		if (isAxis()) {
			((Orientable) blockData).setAxis(getAxis());
		} else if (isFace()){
			((Directional) blockData).setFacing(getFace());
		}
		
		return blockData;
	}

	/**
	 * Check if the block data has the correct Axis/Face
	 * 
	 * @param blockData the blockdata that is going to be checkd
	 * @return true if the block data has the correct Axis/Face
	 */
	public boolean isData(BlockData blockData) {
		if (isAxis() && !((Orientable) blockData).getAxis().equals(getAxis())) return false;
		if (isFace() && !((Directional) blockData).getFacing().equals(getFace()) && ((Directional) blockData).getFaces().contains(getFace())) return false;
		
		return true;
	}

}
