package me.xxastaspastaxx.dimensions.addons.customlighter.framemanager;

import org.bukkit.block.Block;

import com.jojodmo.customitems.api.CustomItemsAPI;

public class CustomItemsFrameManager extends FrameManager {

	private String id;
	
	public CustomItemsFrameManager(String string) {
		this.id = string;
	}
	
	public String getId() {
		return id;
	}

	@Override
	public boolean isAccepted(Block block) {
		return CustomItemsAPI.getCustomItemIDAtBlock(block).equals(this.id);
	}
	
	@Override
	public void placeBlock(Block block) {
		CustomItemsAPI.setCustomItemIDAtBlock(block, this.id, false);
	}
	
}
