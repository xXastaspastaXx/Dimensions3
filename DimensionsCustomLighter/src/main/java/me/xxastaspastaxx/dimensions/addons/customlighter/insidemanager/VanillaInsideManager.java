package me.xxastaspastaxx.dimensions.addons.customlighter.insidemanager;

import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;

public class VanillaInsideManager extends InsideManager {

	private BlockData data;
	
	public VanillaInsideManager(String blockDataAsString) {

		data = Bukkit.createBlockData(blockDataAsString);
	}
	
	public BlockData getBlockData() {
		return data;
	}
	
}
