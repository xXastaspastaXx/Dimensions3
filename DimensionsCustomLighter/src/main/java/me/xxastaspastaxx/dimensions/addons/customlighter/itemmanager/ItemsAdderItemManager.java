package me.xxastaspastaxx.dimensions.addons.customlighter.itemmanager;

import org.bukkit.inventory.ItemStack;

import dev.lone.itemsadder.api.CustomStack;

public class ItemsAdderItemManager extends ItemManager {

	private CustomStack item;
	
	public ItemsAdderItemManager(String string) {
		this.item = CustomStack.getInstance(string);
	}
	
	public CustomStack getItem() {
		return item;
	}

	@Override
	public boolean isAccepted(ItemStack lighter) {
		return CustomStack.byItemStack(lighter).equals(item);
	}
	
}
