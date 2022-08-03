package me.xxastaspastaxx.dimensions.addons.customlighter.framemanager;

import org.bukkit.block.Block;

import dev.lone.itemsadder.api.CustomBlock;

public class ItemsAdderFrameManager extends FrameManager {

	private CustomBlock block;
	
	public ItemsAdderFrameManager(String string) {
		this.block = CustomBlock.getInstance(string);
	}
	
	public CustomBlock getBlock() {
		return block;
	}

	@Override
	public boolean isAccepted(Block block) {
		return CustomBlock.byAlreadyPlaced(block).equals(this.block);
	}
	
	@Override
	public void placeBlock(Block block) {
		this.block.place(block.getLocation());
	}
	
}
