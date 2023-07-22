package me.xxastaspastaxx.dimensions.builder;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class CreatePortalListener implements Listener {
	
	CreatePortalManager manager;
	
	public CreatePortalListener(CreatePortalManager manager) {
		this.manager = manager;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onClick(InventoryClickEvent e) {
		if (!manager.hasInstance((Player) e.getWhoClicked())) return;
		if (manager.click((Player) e.getWhoClicked(), e.getClickedInventory(), e.getRawSlot(), e.isRightClick(), e.isShiftClick())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onchat(AsyncPlayerChatEvent e) {
		if (!manager.hasInstance(e.getPlayer())) return;
		
		e.setCancelled(manager.getInstance(e.getPlayer()).handleChatInput(e.getMessage()));
		
	}
	
	
	/*@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent e) {
		if (!manager.hasInstance(e.getPlayer())) return;
		
		CreatePortalInstance a = manager.getInstance(e.getPlayer());
		if (e.getItem()!=null && e.getItem().isSimilar(a.getSelection().item)) {
			BlockSelection selection = a.getSelection();
			
			if (e.getAction()==Action.LEFT_CLICK_BLOCK) {
				selection.setOne(e.getClickedBlock());
				e.getPlayer().sendMessage("§7[§cBuildableMobs§7] Selected point 1");
				selection.updateItem(e.getItem());
				e.setCancelled(true);
			} else if (e.getAction()==Action.RIGHT_CLICK_BLOCK) {
				selection.setTwo(e.getClickedBlock());
				e.getPlayer().sendMessage("§7[§cBuildableMobs§7] Selected point 2");
				selection.updateItem(e.getItem());
				e.setCancelled(true);
			}
		}
	}*/
	
}
