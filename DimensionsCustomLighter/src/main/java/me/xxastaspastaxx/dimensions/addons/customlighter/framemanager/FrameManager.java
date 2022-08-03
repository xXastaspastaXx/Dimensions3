package me.xxastaspastaxx.dimensions.addons.customlighter.framemanager;

import org.bukkit.block.Block;

public abstract class FrameManager {

	public boolean isAccepted(Block block) {
		return true;
	}

	/*public boolean isAccepted(Block[][] portal2D) {
		for (int y = 0;y<portal2D.length;y++) {
			for (int side = 0;side<portal2D[0].length;side++) {
				
				Block block = portal2D[y][side];
				
				//is round the correct material && is empty inside? (skip corners)	
				if ((y==0 && side==0) || (y==portal2D.length-1 && side==0) || (y==0 && side==portal2D[0].length-1) || (y==portal2D.length-1 && side==portal2D[0].length-1)) continue;
				if (y!=0 && y!=portal2D.length-1 && side!=0 && side!=portal2D[0].length-1) {
					if (!DimensionsUtils.isAir(block) || Dimensions.getCompletePortalManager().getPortal(block.getLocation(), false, false)!=null) return false;
				} else {
					if (!isAccepted(block)) return false;
				}
			}
		}
		return true;
	}*/

	public abstract void placeBlock(Block block);

}
