package me.xxastaspastaxx.dimensions.addons.customlighter.itemmanager;

import org.bukkit.inventory.ItemStack;

import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.items.OraxenItems;

public class OraxenItemManager extends ItemManager {

	private ItemBuilder item;
	private String id;
	
	public OraxenItemManager(String string) {
		this.id = string;
		this.item = OraxenItems.getItemById(string);
	}
	
	public ItemBuilder getItem() {
		return item;
	}

	@Override
	public boolean isAccepted(ItemStack lighter) {
		return OraxenItems.getIdByItem(lighter).contentEquals(id);
	}
	
}
