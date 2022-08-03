package me.xxastaspastaxx.dimensions.addons.customlighter.insidemanager;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import com.jojodmo.customitems.api.CustomItemsAPI;

import me.xxastaspastaxx.dimensions.DimensionsSettings;

public class CustomItemsInsideManager extends InsideManager {

	private String id;
	
	public CustomItemsInsideManager(String string) {
		this.id = string;
		Block block = new Location(DimensionsSettings.fallbackWorld, 0,0,0).getBlock();
		BlockData data = block.getBlockData();
		this.blockData = CustomItemsAPI.setCustomItemIDAtBlock(block, string, false).getBlockData();
		block.setBlockData(data);
	}
	
}
