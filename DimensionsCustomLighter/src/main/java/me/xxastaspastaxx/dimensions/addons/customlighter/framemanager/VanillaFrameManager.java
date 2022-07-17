package me.xxastaspastaxx.dimensions.addons.customlighter.framemanager;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class VanillaFrameManager extends FrameManager {
	
	private BlockData blockData;
	
	public VanillaFrameManager(String blockDataAsString) {

		blockData = Bukkit.createBlockData(blockDataAsString);
	}

	@Override
	public boolean isAccepted(Block block) {
		
		BlockData blockData2 = block.getBlockData();
		
		return blockData.matches(blockData2) || blockData.matches(Bukkit.getServer().createBlockData(blockData2.getAsString().replace("facing=north", "facing=west").replace("facing=south", "facing=east"))) || blockData.matches(Bukkit.getServer().createBlockData(blockData2.getAsString().replace("facing=west", "facing=north").replace("facing=east", "facing=south")) );
	}

	@Override
	public void placeBlock(Block block) {
		block.setType(blockData.getMaterial());
		block.setBlockData(blockData);
	}
	
}
