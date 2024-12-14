package me.xxastaspastaxx.dimensions.gui;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.xxastaspastaxx.dimensions.customportal.CustomPortal;

public class DimensionsGUIUtils {
	
	//Constants
	public static ItemStack BLACK_GLASS = createBlackGlass();
	private static ItemStack createBlackGlass() {
		if (BLACK_GLASS!=null) return BLACK_GLASS;
		return createItem(Material.BLACK_STAINED_GLASS_PANE, "§7");
	}
	
	private static Enchantment DECOR_ENCHANT = Enchantment.getByName("DAMAGE_ALL") == null ? Enchantment.getByName("SMITE") : Enchantment.getByName("DAMAGE_ALL");
	
	
	//Create ItemStack
	public static ItemStack createItem(Material material, String title) {
		return createItem(material, 1, title);
	}
	
	public static ItemStack createItem(Material material, int amount, String title) {
		return createItem(material, amount, title, new String[0]);
	}
	
	public static ItemStack createItem(Material material, String title, String[] lore) {
		return createItem(material, 1, title, lore);
	}
	
	public static ItemStack createItem(Material material, int amount, String title, String[] lore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(title);
		meta.setLore(new ArrayList<String>(Arrays.asList(lore)));
		item.setItemMeta(meta);
		
		return item;
	}

	
	//Update ItemStack
	public static void updateItem(Inventory inventory, int index, String title, String[] lore, int toggleEnchant) {
		ItemStack item = inventory.getItem(index);
		
		if (toggleEnchant==1) {
			item.addUnsafeEnchantment(DECOR_ENCHANT, 1);
		} else if (toggleEnchant==-1) {
			item.removeEnchantment(DECOR_ENCHANT);
		}
		
		ItemMeta meta = item.getItemMeta();
		if (title!=null) meta.setDisplayName(title);
		if (lore!=null) meta.setLore(new ArrayList<String>(Arrays.asList(lore)));
		if (toggleEnchant==1) {
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		item.setItemMeta(meta);
	}
	
	public static void updateItem(ItemStack item, String title, String[] lore, int toggleEnchant) {
		
		if (toggleEnchant==1) {
			item.addUnsafeEnchantment(DECOR_ENCHANT, 1);
		} else if (toggleEnchant==-1) {
			item.removeEnchantment(DECOR_ENCHANT);
		}
		
		ItemMeta meta = item.getItemMeta();
		if (title!=null) meta.setDisplayName(title);
		if (lore!=null) meta.setLore(new ArrayList<String>(Arrays.asList(lore)));
		if (toggleEnchant==1) {
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		item.setItemMeta(meta);
	}
	
	public static void updateItem(ItemStack item, String title, ArrayList<String> lore, int toggleEnchant) {
		
		if (toggleEnchant==1) {
			item.addUnsafeEnchantment(DECOR_ENCHANT, 1);
		} else if (toggleEnchant==-1) {
			item.removeEnchantment(DECOR_ENCHANT);
		}
		
		ItemMeta meta = item.getItemMeta();
		if (title!=null) meta.setDisplayName(title);
		if (lore!=null) meta.setLore(lore);
		if (toggleEnchant==1) {
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		item.setItemMeta(meta);
	}


	public static ItemStack createPortalItem(CustomPortal customPortal) {
		return createItem(customPortal.getOutsideMaterial(), "§f"+customPortal.getDisplayName());
	}
	
}
