package me.xxastaspastaxx.dimensions.gui.player;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.builder.CreatePortalInstance;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.gui.CreatePortalGUI;
import me.xxastaspastaxx.dimensions.gui.DimensionsGUIType;
import me.xxastaspastaxx.dimensions.gui.DimensionsGUIUtils;

public class DimensionsPlayerMainGUI extends CreatePortalGUI {
	
	private final float MAX_ITEMS_PER_PAGE = 45f;
	
	int page = 0;
	int maxPage = 0;
	
	public DimensionsPlayerMainGUI(CreatePortalInstance instance) {
		super(instance, DimensionsGUIType.PLAYER_MAIN);
	}
	
	@Override
	public Inventory createInventory() {
		Inventory inv = Bukkit.createInventory(p, 54, "§cDimensions");

		for (int i=0;i<9;i++) {
			inv.setItem(45+i, DimensionsGUIUtils.BLACK_GLASS);
		}
		
		//Navigation
		inv.setItem(48, DimensionsGUIUtils.createItem(Material.BIRCH_BOAT, "§7Previous page"));
		inv.setItem(49, DimensionsGUIUtils.createItem(Material.RED_STAINED_GLASS_PANE, "§cClose"));
		inv.setItem(50, DimensionsGUIUtils.createItem(Material.BIRCH_BOAT, "§7Next page"));
		
		return inv;
	}
	
	@Override
	public void open() {
		
		ArrayList<CustomPortal> portalList = Dimensions.getCustomPortalManager().getCustomPortals();
		
		maxPage = (int) Math.floor(portalList.size()/MAX_ITEMS_PER_PAGE);
		ItemStack[] contents = inventory.getContents();
		inventory = Bukkit.createInventory(p, inventory.getSize(), "§6Portals. Page "+(page+1)+"/"+(maxPage+1));
		inventory.setContents(contents);
		
		for (int i=0;i<MAX_ITEMS_PER_PAGE;i++) {
			int itemIndex = (int) (MAX_ITEMS_PER_PAGE*page+i);
			if (portalList.size()<=itemIndex) {
				inventory.clear(i);
			} else {
				inventory.setItem(i,DimensionsGUIUtils.createPortalItem(portalList.get(itemIndex)));
			}
		}

		inventory.getItem(48).setType(page==0?Material.MINECART:Material.CHEST_MINECART);
		inventory.getItem(50).setType(page==maxPage?Material.MINECART:Material.CHEST_MINECART);
		
		super.open();
	}

	@Override
	public void handleClick(int index, boolean rightClick, boolean shiftClick) {

		ArrayList<CustomPortal> portalList = Dimensions.getCustomPortalManager().getCustomPortals();
		
		if (index<=44) {
			int itemIndex = (int) (index+MAX_ITEMS_PER_PAGE*page);
			if (portalList.size()<=itemIndex) return;

			instance.selectedPortal = portalList.get(itemIndex);
			instance.guiMap.get(DimensionsGUIType.PLAYER_PORTAL).open();
			
			return;
		}
		
		switch (index) {
		//Navigation
		case 48:
			page = Math.max(0, page-1);
			open();
			break;
		case 50:
			page = (int) Math.min(maxPage, page+1);
			open();
			break;
			
		case 49:
			p.closeInventory();
			Dimensions.getCreatePortalManager().clear(p);
			break;
		}
	}

	@Override
	public boolean handleChat(String string) {
		return true;
	}

}
