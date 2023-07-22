package me.xxastaspastaxx.dimensions.addons;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.xxastaspastaxx.dimensions.customportal.CustomPortal;

public abstract class DimensionsAddonPlayerGUIAction {

	public abstract ItemStack getItemStack();
	
	public abstract boolean execute(Player player, CustomPortal selectedPortal);

}
