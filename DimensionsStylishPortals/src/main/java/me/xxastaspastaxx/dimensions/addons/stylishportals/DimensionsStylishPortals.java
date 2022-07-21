package me.xxastaspastaxx.dimensions.addons.stylishportals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsSettings;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.addons.stylishportals.style.FrameStyle;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;

public class DimensionsStylishPortals extends DimensionsAddon implements Listener {
	
	//private Plugin pl;
	
	public static DimensionsStylishPortals instance;
	
	public DimensionsStylishPortals() {
		super("DimensionsStylishPortalsAddon", "3.0.0", "Give style to your portals", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		//this.pl = pl;
			
		DimensionsStylishPortals.instance = this;
		
		Dimensions.getCommandManager().registerCommand("Portal commands", new StylishPortalCreateCommand("blockData", "", new String[0], "Get the string block data of the block you are looking at", "", true, this));
		
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	/*	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPortalIgnite(CustomPortalIgniteEvent e) {

		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object manager = getOption(portal, "frameStyle");
		if (manager==null) return;
		
		FrameStyle frameManager = (FrameStyle) manager;
		
		if (!(complete.getPortalGeometry() instanceof CustomPortalGeometry)) {
			if (e.getCause()==CustomPortalIgniteCause.EXIT_PORTAL || e.getCause()==CustomPortalIgniteCause.LOAD_PORTAL) {
				
				PortalGeometry old = complete.getPortalGeometry();
				
				frameManager.setPortal(complete.getWorld(), old.getMin(), old.getMax(), old.iszAxis());
				
				PortalGeometry geom = CustomPortalGeometry.getPortal(portal, complete.getCenter(), frameManager);
				if (geom==null) return;
				e.replaceCompletePortal(Dimensions.getCompletePortalManager().createNew(new CompletePortal(portal, complete.getWorld(), geom), null, e.getCause(), null));
			} else {
				e.setCancelled(true);
			}
		}
	}
	
	HashMap<Player,Long> clicked = new HashMap<Player,Long>();
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPortalInteract(PlayerInteractEvent e) {
		
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
        	Block block = e.getClickedBlock().getRelative(e.getBlockFace());
        	if (Dimensions.getCompletePortalManager().getCompletePortal(block.getLocation(), false, false)!=null) return;
        	for (CustomPortal portals : Dimensions.getCustomPortalManager().getCustomPortals()) {
        		if (getOption(portals, "frameStyle")==null) continue;
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
	}
	
	public CompletePortal tryIgnite(CustomPortal portal, Player player, ItemStack item, Location loc) {
		PortalGeometry temp = CustomPortalGeometry.getPortal(portal, loc, (FrameStyle) getOption(portal, "frameStyle"));
		if (temp==null) return null;
		
		return Dimensions.getCompletePortalManager().createNew(new CompletePortal(portal, loc.getWorld(), temp), player, CustomPortalIgniteCause.PLAYER, item);
		
	}
	*/
	

	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {
		
		List<String> frameStyle = portalConfig.getStringList("Addon.StylishPortal.FrameStyle");
		//List<String> insideStyle = portalConfig.getStringList("Addon.StylishPortal.InsideStyle");
		if (frameStyle.size()==0) return;
		
		FrameStyle style = new FrameStyle(frameStyle);
		setOption(portal, "frameStyle", style);
		PortalGeometry.setCustomGeometry(portal, new CustomPortalGeometry(null, null, null, null, false, null, style));
		//options.put(portal, new AddonOptions(frameStyle, disabledWorlds, portalDiameters[3], portalDiameters[2], worldHeight[0], worldHeight[1],portalDiameters[1],portalDiameters[0],portalConfig.getStringList("Entities.Spawning.List").size()!=0,buildExitPortal));
		
		return;
	}
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "1lkPDoR0qWSnKoU_2Uclm1RBq5RkDCHONYogn4HbRi3I";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "1iGAl4JzIooHvSp_qtSHGM9XuX_yaQ5GY";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}
	
}
