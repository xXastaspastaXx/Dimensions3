package me.xxastaspastaxx.dimensions.builder;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.xxastaspastaxx.dimensions.Dimensions;

public class CreatePortalManager {

	public static HashMap<Player, CreatePortalInstance> map = new HashMap<Player, CreatePortalInstance>();
	
	public CreatePortalManager(Dimensions pl) {
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, () -> {
			map.values().forEach(inst -> inst.spawnParticles());
		}, 15,15);
		
		Bukkit.getPluginManager().registerEvents(new CreatePortalListener(this), pl);
	}
	
	public void handle(Player p) {
		if (map.containsKey(p)) {
			map.get(p).open();
		} else {
			map.put(p, new CreatePortalInstance(p, true));
		}
	}
	
	public void handle(Player p, String id) {
		if (map.containsKey(p)) {
			map.get(p).open();
		} else {

//			MobStructure structure = BuildableMobs.getManager().getStructureById(id);
//			if (structure==null) {
//				p.sendMessage("§7[§cBuidableMobs§7] §cThe id of the structure you entered is invalid");
//				return;
//			}
//			CreatePortalInstance instance = new CreatePortalInstance(p, structure);
//			
//			instance.options.actions = structure.getActions();
//			
//			instance.guiMap.get(DimensionsGUIType.ACTIONS).open();
//			map.put(p, instance);
		}
	}

	public boolean hasInstance(Player p) {
		return map.containsKey(p);
	}

	public boolean click(Player p, Inventory inv, int rawSlot, boolean rightClick, boolean shiftClick) {
		return map.get(p).click(inv, rawSlot, rightClick, shiftClick);
	}

	public CreatePortalInstance getInstance(Player p) {
		return map.get(p);
	}

	public void clear(Player p) {
		p.closeInventory();
		map.remove(p);
	}
}
