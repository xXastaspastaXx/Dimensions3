package me.xxastaspastaxx.dimensions;

import org.bukkit.Axis;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;

public class AxisOrFace {

	private Axis axis;
	private BlockFace face;
	
	public AxisOrFace(String axisOrFace) {
		
		try {
			axis = Axis.valueOf(axisOrFace.toUpperCase());
		} catch(IllegalArgumentException e) {}
		
		try {
			face = BlockFace.valueOf(axisOrFace.toUpperCase());
		} catch(IllegalArgumentException e) {}
		
	}

	public Axis getAxis() {
		return axis;
	}

	public boolean isAxis() {
		return axis!=null;
	}

	public BlockFace getFace() {
		return face;
	}
	
	public boolean isFace() {
		return face!=null;
	}
	
	public BlockData getNewData(BlockData blockData) {
		if (isAxis()) {
			((Orientable) blockData).setAxis(getAxis());
		} else if (isFace()){
			((Directional) blockData).setFacing(getFace());
		}
		
		return blockData;
	}

	public boolean isData(BlockData blockData) {
		if (isAxis() && !((Orientable) blockData).getAxis().equals(getAxis())) return false;
		if (isFace() && !((Directional) blockData).getFacing().equals(getFace()) && ((Directional) blockData).getFaces().contains(getFace())) return false;
		
		return true;
	}

}
