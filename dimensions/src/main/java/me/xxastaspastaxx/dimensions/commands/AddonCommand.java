package me.xxastaspastaxx.dimensions.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;

public class AddonCommand extends DimensionsCommand implements Listener {

	private Inventory mainGUI;
	private ItemStack installedAddonsItemStack;
	private ItemStack installAddonsItemStack;
	
	private Inventory installedAddonsGUI;
	
	private Inventory manageAddonGUI;
	private ItemStack updateAddonItemStack;
	private ItemStack unloadAddonItemStack;
	private ItemStack addonInfoItemStack;
	
	public AddonCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand) {
		super(command,args,aliases,description, permission, adminCommand);
		
		
		mainGUI = Bukkit.createInventory(null, 9, "§cDimensions addons manager");
		
		installedAddonsItemStack = new ItemStack(Material.COMMAND_BLOCK, 1);
		ItemMeta installedAddonsItemStackMeta = installedAddonsItemStack.getItemMeta();
		installedAddonsItemStackMeta.setDisplayName("§aInstalled addons");
		installedAddonsItemStackMeta.setLore(new ArrayList<String>(Arrays.asList(new String[] {"§7There are currently", "§a"+Dimensions.getAddonManager().getAddons().size()+"§7 addons installed"})));
		installedAddonsItemStack.setItemMeta(installedAddonsItemStackMeta);
		mainGUI.addItem(installedAddonsItemStack);
		
		installAddonsItemStack = new ItemStack(Material.COMMAND_BLOCK_MINECART, 1);
		ItemMeta installAddonsItemStackkMeta = installAddonsItemStack.getItemMeta();
		installAddonsItemStackkMeta.setDisplayName("§aBrowse addons");
		installAddonsItemStackkMeta.setLore(new ArrayList<String>(Arrays.asList(new String[] {"§7Currently unavailable"})));
		installAddonsItemStack.setItemMeta(installAddonsItemStackkMeta);
		mainGUI.addItem(installAddonsItemStack);
		
		
		installedAddonsGUI = Bukkit.createInventory(null, (int) Math.ceil(Dimensions.getAddonManager().getAddons().size()/9f)*9, "§cDimensions addons manager");
		
		for (DimensionsAddon addon : Dimensions.getAddonManager().getAddons()) {
			ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS, 1);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName("§a"+addon.getName());
			itemMeta.setLore(new ArrayList<String>(Arrays.asList(new String[] {"§7"+addon.getDescription(), "§7v"+addon.getVersion(), "§7Click for more options"})));
			item.setItemMeta(itemMeta);
			

			installedAddonsGUI.addItem(item);
		}
		
		
		manageAddonGUI = Bukkit.createInventory(null, 9, "§cDimensions addons manager");
		
		addonInfoItemStack = new ItemStack(Material.COMMAND_BLOCK, 1);
		ItemMeta addonInfoItemStackMeta = addonInfoItemStack.getItemMeta();
		addonInfoItemStackMeta.setDisplayName("§4Something went wrong");
		addonInfoItemStackMeta.setLore(new ArrayList<String>(Arrays.asList(new String[] {"§7Something went wrong"})));
		addonInfoItemStack.setItemMeta(addonInfoItemStackMeta);
		manageAddonGUI.addItem(addonInfoItemStack);
		
		updateAddonItemStack = new ItemStack(Material.GREEN_BANNER, 1);
		ItemMeta updateAddonItemStackMeta = updateAddonItemStack.getItemMeta();
		updateAddonItemStackMeta.setDisplayName("§cUpdate addon");
		updateAddonItemStackMeta.setLore(new ArrayList<String>(Arrays.asList(new String[] {"§7Click to update the addon"})));
		updateAddonItemStack.setItemMeta(updateAddonItemStackMeta);
		manageAddonGUI.addItem(updateAddonItemStack);

		unloadAddonItemStack = new ItemStack(Material.RED_BANNER, 1);
		ItemMeta unloadAddonItemStackMeta = unloadAddonItemStack.getItemMeta();
		unloadAddonItemStackMeta.setDisplayName("§cUnload addon");
		unloadAddonItemStackMeta.setLore(new ArrayList<String>(Arrays.asList(new String[] {"§7Click to unload the addon"})));
		unloadAddonItemStack.setItemMeta(unloadAddonItemStackMeta);
		manageAddonGUI.addItem(unloadAddonItemStack);
		

		Bukkit.getServer().getPluginManager().registerEvents(this, Dimensions.getInstance());
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (sender instanceof Player) {
			((Player) sender).openInventory(mainGUI);
		} else {
			sender.sendMessage("§cYou must be a player to use this command.");
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onItemClick(InventoryClickEvent e) {
		if (e.getInventory()==null || e.getCurrentItem()==null || e.getClickedInventory()==null || e.getWhoClicked()==null || !(e.getWhoClicked() instanceof Player)) return;
		
		
		try {
			if (e.getCurrentItem().isSimilar(installedAddonsItemStack)) {
				e.getWhoClicked().openInventory(installedAddonsGUI);
				e.setCancelled(true);
			} else if (e.getCurrentItem().isSimilar(installAddonsItemStack)) {
				e.getWhoClicked().sendMessage("§7This feature is not ready yet. It will be added in the future.");
				e.setCancelled(true);
			} else if (e.getClickedInventory().equals(installedAddonsGUI)) {
				DimensionsAddon addon = Dimensions.getAddonManager().getAddonByName(e.getCurrentItem().getItemMeta().getDisplayName().replaceFirst("§a", ""));
				if (addon==null) {
					e.getWhoClicked().sendMessage("§cThere was a problem while trying to access the addon");
				} else {
					Inventory guiClone = Bukkit.createInventory(e.getWhoClicked(), 9, addon.getName());
					guiClone.setContents(manageAddonGUI.getContents());
					
					ItemMeta addonInfoItemStackMeta = addonInfoItemStack.getItemMeta();
					addonInfoItemStackMeta.setDisplayName("§c"+addon.getName());
					addonInfoItemStackMeta.setLore(new ArrayList<String>(Arrays.asList(new String[] {"§7"+addon.getDescription(), "§7v"+addon.getVersion()})));
					guiClone.getItem(0).setItemMeta(addonInfoItemStackMeta);
					e.getWhoClicked().openInventory(guiClone);
				}
				e.setCancelled(true);
			} else if (e.getView().getTitle()!=null) {
				DimensionsAddon addon = Dimensions.getAddonManager().getAddonByName(e.getView().getTitle());
				if (addon!=null) {
					if (e.getCurrentItem().isSimilar(updateAddonItemStack)) {
						e.getWhoClicked().sendMessage("§a"+addon.getName()+" v"+addon.getVersion()+" will be updated after a restart");
					} else if (e.getCurrentItem().isSimilar(unloadAddonItemStack)) {
						Dimensions.getAddonManager().unload(addon);
						e.getWhoClicked().sendMessage("§a"+addon.getName()+" v"+addon.getVersion()+" has been unloaded");
						e.getWhoClicked().openInventory(installedAddonsGUI);
					} else if (e.getSlot()==0) e.getWhoClicked().openInventory(installedAddonsGUI);
					
					e.setCancelled(true);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			e.setCancelled(true);
		}
		
		
	}
	
}
