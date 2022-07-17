package me.xxastaspastaxx.dimensions.completePortal;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsSettings;
import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;
import me.xxastaspastaxx.dimensions.events.CustomPortalUseEvent;

public class CompletePortal {
	
	private CustomPortal customPortal;

	private PortalGeometry portalGeometry;
	World world;
	int chunkX;
	int chunkZ;
	
	//We store the last linked world in case the plugin needs to return the player to the world he came from but for some reason the portal is broken
	private CompletePortal linkedPortal;
	private World lastLinkedWorld;
	
	//The fake falling block entities are stored here in order to spawn.despawn them
	ArrayList<PortalEntity> spawnedEntities = new ArrayList<PortalEntity>();
	
	//We keep a list of players that have been teleported to the portal in order to not spam teleport them around
	ArrayList<Entity> hold = new ArrayList<Entity>();
	
	HashMap<String, Object> tags = new HashMap<String, Object>();
	
	public CompletePortal(CustomPortal customPortal, World world, PortalGeometry portalGeometry) {
		this.customPortal = customPortal;
		this.world = world;
		this.portalGeometry = portalGeometry;
		
		Chunk c = getCenter().getChunk();
		chunkX = c.getX();
		chunkZ = c.getZ();
		
		if (portalGeometry==null) return;
		
		Vector min = portalGeometry.getInsideMin();
		Vector max = portalGeometry.getInsideMax();
		boolean zAxis = portalGeometry.iszAxis();
		
		for (double y=min.getY();y<=max.getY();y++) {
			for (double side=zAxis?min.getZ():min.getX();side<=(zAxis?max.getZ():max.getX());side++) {
				PortalEntity entity;
				if (customPortal.getInsideMaterial().isSolid() || customPortal.getInsideMaterial()==Material.NETHER_PORTAL) {
					entity = new PortalEntitySand(new Location(world, zAxis?min.getX():side, y, !zAxis?min.getZ():side), customPortal.getCombinedID(zAxis));
				} else {
					entity = new PortalEntitySolid(new Location(world, zAxis?min.getX():side, y, !zAxis?min.getZ():side), customPortal.getInsideBlockData(zAxis));
				}
				
				spawnedEntities.add(entity);
			}
		}
	}
	
	public CustomPortal getCustomPortal() {
		return customPortal;
	}
	public World getWorld() {
		return world;
	}
	public PortalGeometry getPortalGeometry() {
		return portalGeometry;
	}

	public CompletePortal getLinkedPortal() {
		return linkedPortal;
	}
	public void unlinkPortal() {
		linkedPortal = null;
	}
	public void setLinkedPortal(CompletePortal complete) {
		linkedPortal = complete;
		lastLinkedWorld = complete.getWorld();
	}
	
	public Location getCenter() {
		return portalGeometry.getCenter().toLocation(world);
	}
	
	
	public void handleEntity(Entity en) {
		if (hold.contains(en)) return;
		
		CustomPortalUseEvent useEvent = new CustomPortalUseEvent(this, en, getDestinationPortal(false, null));
		Bukkit.getPluginManager().callEvent(useEvent);
		
		if (useEvent.isCancelled()) return; 
		CompletePortal destination = useEvent.getDestinationPortal();
		
		//If no portal was put as a destination from other sources, we create our own
		if (destination==null) destination = getDestinationPortal(true, null);
		
		Location teleportLocation = destination.getCenter().clone();
		teleportLocation.setY(destination.getPortalGeometry().getInsideMin().getY());
		boolean zAxis = destination.getPortalGeometry().iszAxis();
		teleportLocation.add(!zAxis?0.5f:0.5f,0,!zAxis?0.5f:0.5f);
		
		
		
		destination.pushToHold(en);
		
		en.teleport(teleportLocation);
		
		//customPortal.usePortal(en, this);
	}
	
	public boolean isInsidePortal(Location loc, boolean outside, boolean corner) {
		return loc.getWorld().equals(world) && portalGeometry.isInside(loc, outside, corner);
	}
	
	
	public CompletePortal getDestinationPortal(boolean buildNewPortal, Location overrideLocation) {

		if (linkedPortal!=null) return linkedPortal;
		
		Location newLocation = overrideLocation==null?getCenter():overrideLocation;
		
		World destinationWorld = customPortal.getWorld();
		if (!world.equals(destinationWorld)) {
			newLocation = newLocation.multiply(customPortal.getWorldRatioReturn());
		} else {
			destinationWorld = lastLinkedWorld==null?DimensionsSettings.fallbackWorld:lastLinkedWorld;
			newLocation = newLocation.multiply(customPortal.getWorldRatio());
		}
		newLocation.setWorld(destinationWorld);
		
		//FIX the wolrd height ratio
		int currMinWorldHeight = (int) DimensionsSettings.get("Worlds."+world.getName()+".MinHeight", 0);
		int currMaxWorldHeight = (int) DimensionsSettings.get("Worlds."+world.getName()+".MaxHeight", 255);
		int currWorldHeight = currMaxWorldHeight-currMinWorldHeight;
		
		int minWorldHeight = (int) DimensionsSettings.get("Worlds."+destinationWorld.getName()+".MinHeight", 0);
		int maxWorldHeight = (int) DimensionsSettings.get("Worlds."+destinationWorld.getName()+".MaxHeight", 255);
		int worldHeight = maxWorldHeight-minWorldHeight;

		double currPercent = (getCenter().getY()-currMinWorldHeight)/currWorldHeight;
		
		newLocation.setY(worldHeight*currPercent+minWorldHeight);
		//===============
		
		CompletePortal destination = Dimensions.getCompletePortalManager().getNearestPortal(newLocation, customPortal);
		
		if (destination==null) {
			if (!buildNewPortal) return null;
			byte width = portalGeometry.getPortalWidth();
			byte height = portalGeometry.getPortalHeight();
			boolean zAxis = portalGeometry.iszAxis();

			//TODO find best location
			Location checkLocation = getSafeLocation(newLocation, zAxis, destinationWorld, height, width);
			if (checkLocation!=null) newLocation = checkLocation;
			
			
			double maxY = (newLocation.getY()+height);
			double maxSide = ((zAxis?newLocation.getZ():newLocation.getX())+width);
			
			for (double y=newLocation.getY();y<=maxY;y++) {
				for (double side=(zAxis?newLocation.getZ():newLocation.getX());side<=maxSide;side++) {
					Block block = new Location(destinationWorld, zAxis?newLocation.getX():side, y, !zAxis?newLocation.getZ():side).getBlock();
					if ((y==newLocation.getY() || y==maxY) || ((side==(zAxis?newLocation.getZ():newLocation.getX())) || side==maxSide)) {
						block.setBlockData(customPortal.getAxisOrFace().getNewData(customPortal.getOutsideMaterial().createBlockData()));
					} else {
						block.setType(Material.AIR);
					}
				}
			}
			
			PortalGeometry geom = PortalGeometry.getPortal(customPortal, newLocation.add(zAxis?0:1,1,zAxis?1:0));
			if (geom==null) return null;
			destination = Dimensions.getCompletePortalManager().createNew(new CompletePortal(customPortal, newLocation.getWorld(), geom), null, CustomPortalIgniteCause.EXIT_PORTAL, null);
		}
		
		if (destination.getLinkedPortal()==null) {
			setLinkedPortal(destination);
			destination.setLinkedPortal(this);
		}
		
		return destination;
	}

	private Location getSafeLocation(Location newLocation, boolean zAxis, World destinationWorld, int height, int width) {
		Location backupLocation = null;
		Location backupLocation2 = null;
		Location checkLocation;
		
		int minWorldHeight = (int) DimensionsSettings.get("Worlds."+destinationWorld.getName()+".MinHeight", 0);
		int maxWorldHeight = (int) DimensionsSettings.get("Worlds."+destinationWorld.getName()+".MaxHeight", 255);
		
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
					
					if (checkLocation.getY()>minWorldHeight && checkLocation.getY()+height<maxWorldHeight) {
						//TODO check location
						if (canBuildPortal(checkLocation, zAxis, destinationWorld, height, width, true)) return checkLocation;
						if (backupLocation==null && canBuildPortal(checkLocation, !zAxis, destinationWorld, height, width, true)) backupLocation = checkLocation.clone();
						if (backupLocation2==null && canBuildPortal(checkLocation, zAxis, destinationWorld, height, width, false)) backupLocation2 = checkLocation.clone();
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
		
		return backupLocation!=null?backupLocation:backupLocation2;
	}

	private boolean canBuildPortal(Location checkLocation, boolean zAxis, World destinationWorld, int height, int width, boolean checkPlatform) {
		
		double maxY = (checkLocation.getY()+height);
		double maxSide = ((zAxis?checkLocation.getZ():checkLocation.getX())+width);
		
		for (double y=checkLocation.getY();y<=maxY;y++) {
			for (double side=(zAxis?checkLocation.getZ():checkLocation.getX());side<=maxSide;side++) {
				Block block = new Location(destinationWorld, zAxis?checkLocation.getX():side, y, !zAxis?checkLocation.getZ():side).getBlock();
				if (y==checkLocation.getY() && !block.getRelative(BlockFace.DOWN).getType().isSolid()) return false;//check if has bottom
				
				if (checkPlatform && y==checkLocation.getY() && (!block.getRelative(BlockFace.DOWN).getRelative(zAxis?BlockFace.WEST:BlockFace.SOUTH).getType().isSolid() || !block.getRelative(BlockFace.DOWN).getRelative(zAxis?BlockFace.EAST:BlockFace.NORTH).getType().isSolid())) return false;
				
				if ((y==checkLocation.getY() || y==maxY) || ((side==(zAxis?checkLocation.getZ():checkLocation.getX())) || side==maxSide)) {
					if (!customPortal.isPortalBlock(block) && !DimensionsUtils.isAir(block)) return false;
				} else {
					if (!DimensionsUtils.isAir(block)) return false;
				}
			}
		}
		return true;
	}
	
	
	public void fill(Player p) {
		if (p==null) {
			for (Entity player : world.getNearbyEntities(getCenter(), 16*Bukkit.getViewDistance(), 255, 16*Bukkit.getViewDistance(), (player) -> player instanceof Player)) {
				fill((Player) player);
			}
			return;
		}
		
		for (PortalEntity en : spawnedEntities) {
			en.destroy(p);
			en.summon(p);
		}
	}

	public void destroy(Player p) {

		for (PortalEntity en : spawnedEntities) {
			if (p==null)
				en.destroyBroadcast();
			else
				en.destroy(p);
		}
	}
	
	public boolean isInChunk(World world2, int x, int z) {
		
		return world.equals(world2) && chunkX==x && chunkZ==z;
	}

	public void pushToHold(Entity en) {
		hold.add(en);
	}

	public boolean hasInHold(Player player) {
		return hold.contains(player);
	}
	public void removeFromHold(Entity en) {
		hold.remove(en);
	}

	public void setTag(String key, Object value) {
		if (value==null)
			tags.remove(key);
		else
			tags.put(key, value);
	}
	
	public Object getTag(String key) {
		return tags.get(key);
	}

	protected HashMap<String, Object> getTags() {
		return tags;
	}

	public void setTags(HashMap<String, Object> tags) {
		this.tags = tags;
	}
}
