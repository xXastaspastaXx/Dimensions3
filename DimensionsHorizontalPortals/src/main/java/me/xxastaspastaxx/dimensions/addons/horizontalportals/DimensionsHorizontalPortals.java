package me.xxastaspastaxx.dimensions.addons.horizontalportals;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsSettings;
import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.completePortal.PortalEntity;
import me.xxastaspastaxx.dimensions.completePortal.PortalEntitySand;
import me.xxastaspastaxx.dimensions.completePortal.PortalEntitySolid;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;
import me.xxastaspastaxx.dimensions.events.CustomPortalUseEvent;

public class DimensionsHorizontalPortals extends DimensionsAddon implements Listener {
	
	//private Plugin pl;
	
	public DimensionsHorizontalPortals() {
		super("DimensionsHorizontalPortalsAddon", "3.0.1", "Horizontal portals", DimensionsAddonPriority.LOW);
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		//this.pl = pl;
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void postPortalIgnite(CustomPortalIgniteEvent e) {
		
		CompletePortal complete = e.getCompletePortal();
		Object option = getOption(complete, "horizontalPortal");
		if (option==null) return;
		if (!(complete.getPortalGeometry() instanceof HorizontalPortalGeometry)) return;
		CustomPortal customPortal = complete.getCustomPortal();
		
		complete.getPortalEntities().clear();
		PortalGeometry geom = complete.getPortalGeometry();
		
		Vector min = geom.getInsideMin();
		Vector max = geom.getInsideMax();
		

		ArrayList<PortalEntity> spawnedEntities = new ArrayList<PortalEntity>();
		for (double x=min.getX();x<=max.getX();x++) {
			for (double z=min.getZ();z<=max.getZ();z++) {
				PortalEntity entity;
				Location loc = new Location(complete.getWorld(), x, min.getY(), z);
				if (customPortal.getInsideMaterial().isSolid() || customPortal.getInsideMaterial()==Material.NETHER_PORTAL || customPortal.getInsideMaterial()==Material.END_GATEWAY) {
					entity = new PortalEntitySand(loc, customPortal.getCombinedID(false));
				} else {
					entity = new PortalEntitySolid(loc, customPortal.getInsideBlockData(false));
				}
				
				spawnedEntities.add(entity);
			}
		}
		
		complete.getPortalEntities().addAll(spawnedEntities);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void postPortalUse(CustomPortalUseEvent e) {
		if (e.getDestinationPortal()!=null) return;
		CompletePortal complete = e.getCompletePortal();
		if (getOption(complete, "horizontalPortal")==null) return;
		if (!(complete.getPortalGeometry() instanceof HorizontalPortalGeometry)) return;

		CustomPortal customPortal = complete.getCustomPortal();
		
		CompletePortal destination = null;
		
		PortalGeometry portalGeometry = complete.getPortalGeometry();
		byte width = portalGeometry.getPortalWidth();

		//TODO find best location
		Location newLocation = complete.getDestinationLocation(null, null);
		World destinationWorld = newLocation.getWorld();
		Location checkLocation = getSafeLocation(customPortal, newLocation, destinationWorld, width);
		if (checkLocation!=null) newLocation = checkLocation;
		
		portalGeometry.buildPortal(newLocation, destinationWorld, customPortal);

		PortalGeometry geom = PortalGeometry.getPortalGeometry(customPortal).getPortal(customPortal, newLocation.add(1.5,0,1.5));
		if (geom==null) return;
		destination = Dimensions.getCompletePortalManager().createNew(new CompletePortal(customPortal, destinationWorld, geom), null, CustomPortalIgniteCause.EXIT_PORTAL, null);
		if (destination==null) return;
		
		e.setDestinationPortal(destination);
	}
	
	private Location getSafeLocation(CustomPortal customPortal, Location newLocation, World destinationWorld, int width) {
		Location backupLocation = null;
		Location checkLocation;
	
//		int minWorldHeight = (int) DimensionsSettings.get("Worlds."+destinationWorld.getName()+".MinHeight", 0);
//		int maxWorldHeight = (int) DimensionsSettings.get("Worlds."+destinationWorld.getName()+".MaxHeight", destinationWorld.getMaxHeight());
		
		for (int m =0;m<DimensionsSettings.safeSpotSearchRadius;m++) {
			checkLocation = newLocation.clone();

			int y = 0;
			int yAdd = 1;
			while (y!=m+1) {
				checkLocation.setY(newLocation.getY()+y);
				
				int dir = 0; 

				int x = 0;
				int z = 0;
				float travel = 1;
				if (y!=m && y!=-m) {
					travel = m*2-0.5f;
					x=-m+1;
					z=m;
					dir = 1;
				}
				int travelCurr = (int) travel;
				// 0 up
				// 1 right
				// 2 down
				// 3 left
				while (x!=-m || z!=m+1) {

					checkLocation.setZ(newLocation.getZ()+z);
					checkLocation.setX(newLocation.getX()+x);
					
					if (destinationWorld.getWorldBorder().isInside(checkLocation)) {
						//TODO check location
						if (canBuildPortal(customPortal, checkLocation, destinationWorld, width, true)) return checkLocation;
						if (backupLocation==null && canBuildPortal(customPortal, checkLocation, destinationWorld, width, false)) backupLocation = checkLocation.clone();
					}
					
					switch (dir) {
						case 0:
							z++;
							break;
						case 1:
							x++;
							break;
						case 2:
							z--;
							break;
						case 3:
							x--;
							break;
						default:
							break;
					}
					if (--travelCurr<=0) {
						travel+=0.5f;
						travelCurr= (int) travel;
						if (++dir==4) dir=0;
						//continue;
					}
					
				}
				
				y+=yAdd;
				if (yAdd>0) yAdd = -(++yAdd);
				else yAdd = Math.abs(--yAdd);
			}
			
		}
		
		return backupLocation;
	}

	private boolean canBuildPortal(CustomPortal customPortal, Location checkLocation, World destinationWorld, int width, boolean checkPlatform) {
		
		double maxX = (checkLocation.getX()+width);
		double maxZ = (checkLocation.getZ()+width);
		
		for (double x=checkLocation.getX();x<=maxX;x++) {
			for (double z=checkLocation.getZ();z<=maxZ;z++) {
				Block block = new Location(destinationWorld, x, checkLocation.getY(), z).getBlock();
				if (checkPlatform && !block.getRelative(BlockFace.DOWN).getType().isSolid()) return false;//check if has bottom
				
				if ((x==checkLocation.getX() || x==maxX) || ((z==checkLocation.getZ()) || z==maxZ)) {
					if (!customPortal.isPortalBlock(block) && !DimensionsUtils.isAir(block)) return false;
				} else {
					if (!DimensionsUtils.isAir(block)) return false;
				}
			}
		}
		return true;
	}

	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {
		
		String s = portalConfig.getString("Addon.HorizontalPortal", "");
		if(!s.equals("true") && !s.equals("both")) return;

		setOption(portal, "horizontalPortal", s);
		
		PortalGeometry.setCustomGeometry(portal, new HorizontalPortalGeometry(null,null));
		
		return;
	}
	
	
}
