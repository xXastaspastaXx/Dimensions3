package me.xxastaspastaxx.dimensions.addons.customlighter.insidemanager;

import dev.lone.itemsadder.api.CustomBlock;

public class ItemsAdderInsideManager extends InsideManager {

	private CustomBlock block;
	
	public ItemsAdderInsideManager(String string) {
		this.block = CustomBlock.getInstance(string);
		blockData = block.generateBlockData();
	}
	
	public CustomBlock getBlock() {
		return block;
	}
	
}
