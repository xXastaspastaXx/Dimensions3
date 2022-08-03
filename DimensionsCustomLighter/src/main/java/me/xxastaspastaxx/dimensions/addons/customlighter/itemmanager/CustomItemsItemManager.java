package me.xxastaspastaxx.dimensions.addons.customlighter.itemmanager;

import org.bukkit.inventory.ItemStack;

import com.jojodmo.customitems.api.CustomItemsAPI;

public class CustomItemsItemManager extends ItemManager {

	private String id;
	
	public CustomItemsItemManager(String string) {
		this.id = string;
	}
	
	@Override
	public boolean isAccepted(ItemStack lighter) {
		return CustomItemsAPI.isCustomItem(lighter, id);
	}
	
}
