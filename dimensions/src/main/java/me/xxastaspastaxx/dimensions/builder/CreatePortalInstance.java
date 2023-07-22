package me.xxastaspastaxx.dimensions.builder;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.gui.CreatePortalGUI;
import me.xxastaspastaxx.dimensions.gui.DimensionsGUIType;
import me.xxastaspastaxx.dimensions.gui.player.DimensionsPlayerMainGUI;
import me.xxastaspastaxx.dimensions.gui.player.DimensionsPlayerPortalGUI;

public class CreatePortalInstance {
	
	private Player p;
	
	//public CreateStructureAction selectedAction = null;
	public CreatePortalOptions options = new CreatePortalOptions();
	
	public HashMap<DimensionsGUIType, CreatePortalGUI> guiMap = new HashMap<DimensionsGUIType, CreatePortalGUI>();
	private DimensionsGUIType currentGUI = DimensionsGUIType.PLAYER_MAIN;

	public CustomPortal selectedPortal = null;
	
	//public MobStructure updatingStructure;
	
	public CreatePortalInstance(Player p, boolean player) {
		this.p = p;
		
		if (player) {
			guiMap.put(DimensionsGUIType.PLAYER_MAIN, new DimensionsPlayerMainGUI(this));
			guiMap.put(DimensionsGUIType.PLAYER_PORTAL, new DimensionsPlayerPortalGUI(this));
		} else {
//			guiMap.put(DimensionsGUIType.ADMIN_MAIN, new CreateStructureMainGUI(this));
		}
		
		//Actions
//		guiMap.put(CreateStuctureGUIType.ACTIONS_ENTITY, new CreateStructureEntityGUI(this));
//		guiMap.put(CreateStuctureGUIType.ACTIONS_COMMAND, new CreateStructureCommandGUI(this));
//		guiMap.put(CreateStuctureGUIType.ACTIONS_EFFECT, new CreateStructureEffectsGUI(this));
//		guiMap.put(CreateStuctureGUIType.ACTIONS_DELAY, new CreateStructureDelayGUI(this));
//		guiMap.put(CreateStuctureGUIType.ACTIONS_DELETE, new CreateStructureDeleteGUI(this));

//		selection = new BlockSelection(guiMap.get(CreateStuctureGUIType.MAIN).getItem(15));
//		selection.updateItem(null);
		
		open();
	}
	
//	public CreateStructureInstance(Player p, MobStructure updatingStructure) {
//		this(p);
//		this.updatingStructure = updatingStructure;
//	}
	
	//Getters
	public Player getPlayer() {
		return p;
	}

//	public BlockSelection getSelection() {
//		return selection;
//	}
	
	//Methods
	public void open() {
		
		//p.getInventory().remove(selection.item);
		//selection.updateItem(null);
		
		guiMap.get(currentGUI).open();
	}
	
	public void setCurrentGUI(DimensionsGUIType type) {
		currentGUI = type;
	}
	
	public void save() {
//
//		if (updatingStructure==null) {
//			p.sendMessage("§7[§cBuildableMobs§7] §7Saving structure file...");
//			try {
//				p.sendMessage("§7[§cBuildableMobs§7] §aSuccesfully saved §n"+BuildableMobs.getManager().create(options.structureName.replace(" ", "").toLowerCase(), this));
//			} catch (Exception e) {
//				p.sendMessage("§7[§cBuildableMobs§7] §cThere was an error trying to save the file. Please check console for more information.");
//				e.printStackTrace();
//			}
//		} else {
//			p.sendMessage("§7[§cBuildableMobs§7] §7Updating structure file...");
//			try {
//				p.sendMessage("§7[§cBuildableMobs§7] §aSuccesfully updated §n"+BuildableMobs.getManager().updateActions(updatingStructure.getId(), this));
//			} catch (Exception e) {
//				p.sendMessage("§7[§cBuildableMobs§7] §cThere was an error trying to update the file. Please check console for more information.");
//				e.printStackTrace();
//			}
//		}
	}
	
	public void spawnParticles() {
//		Block block = selection.getCenter();
//		if (block==null) return;
//		BuildableMobsUtils.highlightBlock(p,block);
////		if (options.entitySpawnMode==0) {
////			BuildableMobsUtils.highlightSpawnBlock(p,block, options.entitySpawnOffset);
////			//p.spawnParticle(Particle.REDSTONE, offset.getX(), offset.getY()+0.5, offset.getZ(), 1, 0,0,0, new Particle.DustOptions(Color.BLUE,2f));
////		}
	}
	
	//TO-REMOVE
	
	public boolean click(Inventory inv, int index, boolean rightClick, boolean shiftClick) {
		return guiMap.get(currentGUI).handleClick(inv, index, rightClick, shiftClick);
	}

//	public SummonedEntity createEntity() {
//		switch (options.entitySpawnMode) {
//			case 0:
//				return new VanillaSummonedEntity(options.entitySpawnType, options.entitySpawnOffset);
//			case 1:
//				return new CommandSummonedEntity(new ArrayList<String>());
//		}
//		return null;
//	}
//	
	public boolean handleChatInput(String string) {
		return guiMap.get(currentGUI).handleChatAsync(string);
	}
	
}