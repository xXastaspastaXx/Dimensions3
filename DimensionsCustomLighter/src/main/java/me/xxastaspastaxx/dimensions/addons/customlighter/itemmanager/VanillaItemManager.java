package me.xxastaspastaxx.dimensions.addons.customlighter.itemmanager;

import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class VanillaItemManager extends ItemManager {

	private ItemStack item;
	
	public VanillaItemManager(String itemSerialised) {

        YamlConfiguration loaded = new YamlConfiguration();
        try {
			loaded.loadFromString( itemSerialised );
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
        this.item = loaded.getItemStack( "item" );
	}
	
	public VanillaItemManager(ItemStack itemStack) {
		this.item = itemStack;
	}

	public ItemStack getItem() {
		return item;
	}

	@Override
	public boolean isAccepted(ItemStack lighter) {
		
		if (lighter.getType()!=item.getType()) return false;
		
		Map<String, Object> lighterSerialized = item.getItemMeta().serialize();
		Map<String, Object> itemSerialized = lighter.getItemMeta().serialize();
		for (String str : lighterSerialized.keySet()) {
			if (lighterSerialized.get(str).toString().compareTo(itemSerialized.get(str)+"")>0) {
		        return false;
			}
		}
		return true;
	}
	
}
