package me.xxastaspastaxx.dimensions.addons.pastedportals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.plugin.Plugin;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsSettings;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.addons.pastedportals.skyblock.PastedBentoBox;
import me.xxastaspastaxx.dimensions.addons.pastedportals.skyblock.PastedIridium;
import me.xxastaspastaxx.dimensions.addons.pastedportals.skyblock.PastedSuperiorSkyblock;
import me.xxastaspastaxx.dimensions.addons.pastedportals.worldedit.WorldEditPasting;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;
import world.bentobox.bentobox.BentoBox;

public class DimensionsPastedPortalsAddon extends DimensionsAddon implements Listener {
	
	private Plugin pl;
	
	private boolean onWorldGeneration = false;
	
	public DimensionsPastedPortalsAddon() {
		super("DimensionsPastedPortalsAddon", "3.0.1", "Auto ignite pasted portals", DimensionsAddonPriority.NORMAL);
	}
	
	
	@Override
	public void onEnable(Dimensions main) {
		this.pl = main;


		if (DimensionsSettings.getConfig().getBoolean("PastedPortals.WorldEdit",false)) {
			Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
		    if (worldEdit!=null && worldEdit instanceof WorldEditPlugin) {
		    	new WorldEditPasting();
		    }
	   }

	   if (DimensionsSettings.getConfig().getBoolean("PastedPortals.Skyblock",false)) {
		   Plugin bentoBox = Bukkit.getPluginManager().getPlugin("BentoBox");
		   if (bentoBox!=null && bentoBox instanceof BentoBox) {
			   new PastedBentoBox(this);
		   }
		    
		   Plugin iridiumSkyblock = Bukkit.getPluginManager().getPlugin("IridiumSkyblock");
		   if (iridiumSkyblock!=null && iridiumSkyblock instanceof IridiumSkyblock) {
			   new PastedIridium(this);
		   }
		   Plugin superiorSkyblock = Bukkit.getPluginManager().getPlugin("SuperiorSkyblock2");
		   if (superiorSkyblock!=null && superiorSkyblock instanceof SuperiorSkyblock) {
			   new PastedSuperiorSkyblock(this);
		   }
	   }
		
	   onWorldGeneration = DimensionsSettings.getConfig().getBoolean("PastedPortals.OnWorldGeneration",false);
	   
		Bukkit.getServer().getPluginManager().registerEvents(this, pl);
	}
	
	public Plugin getPlugin() {
		return pl;
	}
	
	@EventHandler
	public void onChunkPopulate(ChunkPopulateEvent e) {
		if (!onWorldGeneration) return;
		Chunk chunk = e.getChunk();
        final int minY = chunk.getWorld().getMinHeight();
        final int maxY = chunk.getWorld().getMaxHeight();
	
        Bukkit.getScheduler().runTaskAsynchronously(pl, new Runnable() {
			
			@Override
			public void run() {
				for (int x = 0; x <= 15; ++x) {
		            for (int y = minY; y <= maxY; ++y) {
		                for (int z = 0; z <= 15; ++z) {
		                	Block block = chunk.getBlock(x, y, z);
		                	if (block.getType()!=Material.OAK_WALL_SIGN) continue;
		                	
		                	Sign signData = (Sign) block.getState().getData();
		                	
		                	if (!signData.getLine(0).contentEquals("[DIMENSIONS]")) continue;
		                	Bukkit.getScheduler().runTask(pl, new Runnable() {
								
								@Override
								public void run() {
									block.setType(Material.AIR);
				                	
				                	CustomPortal portal = Dimensions.getCustomPortalManager().getCustomPortal(signData.getLine(1));
				                	if (portal!=null) {
				                		PortalGeometry temp = PortalGeometry.getPortalGeometry().getPortal(portal, block.getLocation());
				                		if (temp!=null)
				                			Dimensions.getCompletePortalManager().createNew(new CompletePortal(portal, block.getWorld(), temp), null, CustomPortalIgniteCause.PLUGIN, null);
				                	}
								}
							});
		                }
		            }
		        }
				
			}
		});
	}
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "1e0mxkAozMinb6Ro-Ebr94NgrR9bwXjtB55a2py3riiE";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "1wO1GVoG8svgnSgGFxTnf3z1ocVLyV_mM";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}
	
}
