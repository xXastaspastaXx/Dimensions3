package me.xxastaspastaxx.dimensions.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.builder.CreatePortalInstance;

public abstract class CreatePortalGUI {
	
	public DimensionsGUIType guiType;
	public Inventory inventory;
	public CreatePortalInstance instance;
	public Player p;
	
	public String waitingResponse = "no";
	
	public CreatePortalGUI(CreatePortalInstance instance, DimensionsGUIType guiType) {
		this.instance = instance;
		this.guiType = guiType;
		this.p = instance.getPlayer();
		inventory = createInventory();
	}
	
	public void open() {
		p.openInventory(inventory);
		instance.setCurrentGUI(guiType);
	}
	
	public abstract Inventory createInventory();
	
	public boolean handleClick(Inventory inv, int index, boolean rightClick, boolean shiftClick) {
		
		if (!inv.equals(inventory)) return false;
		handleClick(index, rightClick, shiftClick);
		return true;
	}
	
	public abstract void handleClick(int index, boolean rightClick, boolean shiftClick);
	
	public boolean handleChatAsync(String input) {
		if (waitingResponse=="no") return false;
		
		Bukkit.getScheduler().runTask(Dimensions.getInstance(), () -> {
			if (handleChat(input)) waitingResponse = "no";
		});
		
		return true;
	}

	public abstract boolean handleChat(String string);
	
	public ItemStack getItem(int i) {
		return inventory.getItem(i);
	}

	public void updateItem(int index, String title, String[] lore, int toggleGlow) {
		DimensionsGUIUtils.updateItem(inventory, index, title, lore, toggleGlow);
		
	}
	
}