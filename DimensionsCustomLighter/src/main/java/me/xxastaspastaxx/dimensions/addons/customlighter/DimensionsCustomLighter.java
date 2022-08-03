package me.xxastaspastaxx.dimensions.addons.customlighter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.addons.customlighter.commands.CustomFrameCommand;
import me.xxastaspastaxx.dimensions.addons.customlighter.commands.CustomInsideCommand;
import me.xxastaspastaxx.dimensions.addons.customlighter.commands.CustomLighterCommand;
import me.xxastaspastaxx.dimensions.addons.customlighter.framemanager.CustomItemsFrameManager;
import me.xxastaspastaxx.dimensions.addons.customlighter.framemanager.FrameManager;
import me.xxastaspastaxx.dimensions.addons.customlighter.framemanager.ItemsAdderFrameManager;
import me.xxastaspastaxx.dimensions.addons.customlighter.framemanager.OraxenFrameManager;
import me.xxastaspastaxx.dimensions.addons.customlighter.framemanager.VanillaFrameManager;
import me.xxastaspastaxx.dimensions.addons.customlighter.insidemanager.CustomItemsInsideManager;
import me.xxastaspastaxx.dimensions.addons.customlighter.insidemanager.InsideManager;
import me.xxastaspastaxx.dimensions.addons.customlighter.insidemanager.ItemsAdderInsideManager;
import me.xxastaspastaxx.dimensions.addons.customlighter.insidemanager.OraxenInsideManager;
import me.xxastaspastaxx.dimensions.addons.customlighter.insidemanager.VanillaInsideManager;
import me.xxastaspastaxx.dimensions.addons.customlighter.itemmanager.CustomItemsItemManager;
import me.xxastaspastaxx.dimensions.addons.customlighter.itemmanager.ItemManager;
import me.xxastaspastaxx.dimensions.addons.customlighter.itemmanager.ItemsAdderItemManager;
import me.xxastaspastaxx.dimensions.addons.customlighter.itemmanager.OraxenItemManager;
import me.xxastaspastaxx.dimensions.addons.customlighter.itemmanager.VanillaItemManager;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;

public class DimensionsCustomLighter extends DimensionsAddon implements Listener {
	
	//private Plugin pl;
	
	public static DimensionsCustomLighter instance;

	private HashMap<CustomPortal, YamlConfiguration> configs = new HashMap<CustomPortal, YamlConfiguration>();
	
	public DimensionsCustomLighter() {
		super("DimensionsCustomLighterAddon", "3.0.0", "Custom lighters and blocks", DimensionsAddonPriority.NORMAL);
		DimensionsCustomLighter.instance = this;
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		//this.pl = pl;
		
		Dimensions.getCommandManager().registerCommand("Portal commands", new CustomLighterCommand("setLighter", "<portal>", new String[0], "Set the vanilla lighter for the portal", "", true, this));
		Dimensions.getCommandManager().registerCommand("Portal commands", new CustomFrameCommand("setFrameBlock", "<portal>", new String[0], "Set the vanilla frame blockdata for the portal", "", true, this));
		Dimensions.getCommandManager().registerCommand("Portal commands", new CustomInsideCommand("setInsideBlock", "<portal>", new String[0], "Set the vanilla inside blockdata for the portal", "", true, this));
	
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPortalIgnite(CustomPortalIgniteEvent e) {

		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object manager = getOption(portal, "customItem");
		if (manager==null) return;
		
		if (e.getLighter()==null) {
			if (e.getCause()==CustomPortalIgniteCause.PLAYER)
				e.setCancelled(true);
			return;
		}
		
		ItemManager itemManager = (ItemManager) manager;
		
		if (!itemManager.isAccepted(e.getLighter())) e.setCancelled(true);

	}
	/*
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPortalUse(CustomPortalUseEvent e) {
		
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object manager = getOption(portal, "customFrame");
		if (manager==null) return;
		
		if (e.getDestinationPortal()==null)
			complete.getDestinationPortal(true, null);
	}*/
	/*
	HashMap<Player,Long> clicked = new HashMap<Player,Long>();
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPortalInteract(PlayerInteractEvent e) {
		
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
        	Block block = e.getClickedBlock().getRelative(e.getBlockFace());
        	if (Dimensions.getCompletePortalManager().getCompletePortal(block.getLocation(), false, false)!=null) return;
        	for (CustomPortal portals : Dimensions.getCustomPortalManager().getCustomPortals()) {
        		if (getOption(portals, "customFrame")==null) continue;
    			if (tryIgnite(portals, e.getPlayer(), e.getItem(), block.getLocation()) != null) {
    				e.setCancelled(true);
					if (e.getPlayer().getGameMode()!=GameMode.CREATIVE && DimensionsSettings.consumeItems) {
						ItemStack item = e.getItem();
						if (item.getType().toString().contains("BUCKET") && item.getType()!=Material.BUCKET) {
							item.setType(Material.BUCKET);
						} else if (item.getItemMeta() instanceof Damageable) {
							Damageable dmg = (Damageable) item.getItemMeta();
							dmg.setDamage(dmg.getDamage()+1);
							item.setItemMeta((ItemMeta) dmg);
							if (dmg.getDamage()>=item.getType().getMaxDurability()) {
								item.setAmount(item.getAmount()-1);
							}
						} else {
							item.setAmount(item.getAmount()-1);
						}
							
					}
            		clicked.put(e.getPlayer(), System.currentTimeMillis());
        		}
    		}
        }
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerClick(PlayerAnimationEvent e) {
		Player p = e.getPlayer();

		if (clicked.containsKey(p)) {
			if (System.currentTimeMillis()-clicked.get(p)<500) {
				e.setCancelled(true);
				return;
			} else {
				clicked.remove(p);
			}
		}
	}*/
	
	/*public CompletePortal tryIgnite(CustomPortal portal, Player player, ItemStack item, Location loc) {
		
		Object manager = getOption(portal, "customItem");
		if (manager!=null) {
			if (!((ItemManager) manager).isAccepted(item)) {
				return null;
			}
		} else {
			if (portal.getLighterMaterial()!=item.getType()) {
				return null;
			}
		}
		
		PortalGeometry temp = PortalGeometry.getPortalGeometry().getPortal(portal, loc);
		if (temp==null) return null;
		
		return Dimensions.getCompletePortalManager().createNew(new CompletePortal(portal, loc.getWorld(), temp), player, CustomPortalIgniteCause.PLAYER, item);
		
	}*/
	

	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {
		
		String item = portalConfig.getString("Addon.CustomLighter.Item");
		if (item!=null) {
			ItemManager itemManager = null;
			String itemManagerString = item.substring(0, item.indexOf(':')).toUpperCase();
			item = item.substring(item.indexOf(':')+1, item.length());
			if (itemManagerString.equals("MINECRAFT")) itemManager = new VanillaItemManager(item);
			if (itemManagerString.equals("ITEMSADDER")) itemManager = new ItemsAdderItemManager(item);
			if (itemManagerString.equals("ORAXEN")) itemManager = new OraxenItemManager(item);
			if (itemManagerString.equals("CUSTOMITEMS")) itemManager = new CustomItemsItemManager(item);
			setOption(portal, "customItem", itemManager);
		} 
		
		
		String frame = portalConfig.getString("Addon.CustomLighter.FrameBlock");
		if (frame != null) {
			FrameManager frameManager = null;
			String frameManagerString = frame.substring(0, frame.indexOf(':')).toUpperCase();
			frame = frame.substring(frame.indexOf(':')+1, frame.length());
			if (frameManagerString.equals("MINECRAFT")) frameManager = new VanillaFrameManager(frame);
			if (frameManagerString.equals("ITEMSADDER")) frameManager = new ItemsAdderFrameManager(frame);
			if (frameManagerString.equals("ORAXEN")) frameManager = new OraxenFrameManager(Integer.parseInt(frame));
			if (frameManagerString.equals("CUSTOMITEMS")) frameManager = new CustomItemsFrameManager(frame);
			setOption(portal, "customFrame", frameManager);
			PortalGeometry.setCustomGeometry(portal, new CustomPortalGeometry(null, null, null, null, false, null, frameManager));
		}
		
		
		String inside = portalConfig.getString("Addon.CustomLighter.InsideBlock");
		if (inside !=null) {
			InsideManager insideManager = null;
			String insideManagerString = inside.substring(0, inside.indexOf(':')).toUpperCase();
			inside = inside.substring(inside.indexOf(':')+1, inside.length());
			if (insideManagerString.equals("MINECRAFT")) insideManager = new VanillaInsideManager(inside);
			if (insideManagerString.equals("ITEMSADDER")) insideManager = new ItemsAdderInsideManager(inside);
			if (insideManagerString.equals("ORAXEN")) insideManager = new OraxenInsideManager(Integer.parseInt(inside));
			if (insideManagerString.equals("CUSTOMITEMS")) insideManager = new CustomItemsInsideManager(inside);
			
			portal.setInsideBlockData(insideManager.getBlockData());
		}
		
		configs.put(portal, portalConfig);
	}
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "1oPEk-zh0mpt8_XkKx55l1LG3PIrzTikyqBG0RlIE9jU";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "1_6qrtPMf3adkZJsKav7M4imQjbpUO-5B";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}

	public YamlConfiguration getPortalConfig(CustomPortal portal) {
		return configs.get(portal);
	}
	
}