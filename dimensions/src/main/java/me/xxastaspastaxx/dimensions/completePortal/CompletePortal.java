package me.xxastaspastaxx.dimensions.completePortal;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
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
	private World world;
	private int chunkX;
	private int chunkZ;
	
	private int particlesTask;
	
	//We store the last linked world in case the plugin needs to return the player to the world he came from but for some reason the portal is broken
	private CompletePortal linkedPortal;
	private World lastLinkedWorld;
	
	//The fake falling block entities are stored here in order to spawn.despawn them
	private ArrayList<PortalEntity> spawnedEntities = new ArrayList<PortalEntity>();
	
	//We keep a list of players that have been teleported to the portal in order to not spam teleport them around
	private ArrayList<Entity> hold = new ArrayList<Entity>();
	private HashMap<Entity, Integer> queue = new HashMap<Entity, Integer>();
	
	private HashMap<String, Object> tags = new HashMap<String, Object>();
	
	public CompletePortal(CustomPortal customPortal, World world, PortalGeometry portalGeometry) {
		this.customPortal = customPortal;
		this.world = world;
		this.portalGeometry = portalGeometry;
		
		if (portalGeometry==null) return;
		
		Chunk c = getCenter().getChunk();
		chunkX = c.getX();
		chunkZ = c.getZ();
		
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
	
	public CompletePortal(CustomPortal customPortal, World world, PortalGeometry portalGeometry, CompletePortal linked) {
		this(customPortal, world, portalGeometry);
		
		if (linked==null) return;
		setLinkedPortal(linked);
		linked.setLinkedPortal(this);
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
	
	public ArrayList<PortalEntity> getPortalEntities() {
		return spawnedEntities;
	}
	
	
	public void handleEntity(Entity en) {
		if (hold.contains(en)) return;
		
		int delay = customPortal.getTeleportDelay()*20;
		if ((en instanceof Player) && (((Player) en).getGameMode()==GameMode.CREATIVE || ((Player) en).getGameMode()==GameMode.SPECTATOR)) delay = 0;
		
		queue.put(en, Bukkit.getScheduler().scheduleSyncDelayedTask(Dimensions.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				CustomPortalUseEvent useEvent = new CustomPortalUseEvent(CompletePortal.this, en, getDestinationPortal(false, null, null));
				Bukkit.getPluginManager().callEvent(useEvent);
				
				if (useEvent.isCancelled()) return; 
				CompletePortal destination = useEvent.getDestinationPortal();
				
				//If no portal was put as a destination from other sources, we create our own
				if (destination==null) destination = getDestinationPortal(true, null, null);
				
				Location teleportLocation = destination.getCenter().clone();
				teleportLocation.setY(destination.getPortalGeometry().getInsideMin().getY());
				
				destination.pushToHold(en);
				
				en.teleport(teleportLocation);
				removeFromHold(en);
			}
		}, delay));
		
		//customPortal.usePortal(en, this);
	}
	
	public boolean isInsidePortal(Location loc, boolean outside, boolean corner) {
		return loc.getWorld().equals(world) && portalGeometry.isInside(loc, outside, corner);
	}
	
	
	public CompletePortal getDestinationPortal(boolean buildNewPortal, Location overrideLocation, World overrideWorld) {

		if (linkedPortal!=null) return linkedPortal;
		
		Location newLocation = overrideLocation==null?getCenter():overrideLocation;

		
		World destinationWorld = overrideWorld;
		if (destinationWorld==null) {
			destinationWorld = customPortal.getWorld();
			if (world.equals(destinationWorld))
				destinationWorld = lastLinkedWorld==null?DimensionsSettings.fallbackWorld:lastLinkedWorld;
		}
		newLocation.setWorld(destinationWorld);
		
		FileConfiguration conf = DimensionsSettings.getConfig();
		
		//Fix world ratio
		double currWorldSize = conf.getDouble("Worlds."+world.getName()+".Size", world.getWorldBorder().getSize());
		double worldSize = conf.getDouble("Worlds."+destinationWorld.getName()+".Size", destinationWorld.getWorldBorder().getSize());
		double ratio = worldSize/currWorldSize;
		newLocation = newLocation.multiply(ratio);
		
		//FIX the wolrd height ratio
		int currMinWorldHeight = conf.getInt("Worlds."+world.getName()+".MinHeight", -60);
		int currMaxWorldHeight = conf.getInt("Worlds."+world.getName()+".MaxHeight", world.getMaxHeight());
		int currWorldHeight = currMaxWorldHeight-currMinWorldHeight;
		
		int minWorldHeight = conf.getInt("Worlds."+destinationWorld.getName()+".MinHeight", -60);
		int maxWorldHeight = conf.getInt("Worlds."+destinationWorld.getName()+".MaxHeight", destinationWorld.getMaxHeight());
		int worldHeight = maxWorldHeight-minWorldHeight;

		double currPercent = (getCenter().getY()-currMinWorldHeight)/currWorldHeight;
		
		newLocation.setY(worldHeight*currPercent+minWorldHeight);
		//===============
		
		CompletePortal destination = null;
		if (DimensionsSettings.searchFirstClonePortal) 
			destination = Dimensions.getCompletePortalManager().getNearestPortal(newLocation, this, ratio, true, true);
		
		if (destination==null)
			destination = Dimensions.getCompletePortalManager().getNearestPortal(newLocation, this, ratio, DimensionsSettings.searchSameAxis, DimensionsSettings.searchSameSize);
		
		if (destination==null) {
			if (!buildNewPortal) return null;
			boolean zAxis = portalGeometry.iszAxis();
			byte width = portalGeometry.getPortalWidth();
			byte height = portalGeometry.getPortalHeight();

			//TODO find best location
			Location checkLocation = getSafeLocation(newLocation, zAxis, destinationWorld, height, width);
			if (checkLocation!=null) newLocation = checkLocation;
			
			portalGeometry.buildPortal(newLocation, destinationWorld, customPortal);
			
			PortalGeometry geom = PortalGeometry.getPortalGeometry().getPortal(customPortal, newLocation.add(zAxis?0:1,1,zAxis?1:0));
			if (geom==null) return null;
			destination = Dimensions.getCompletePortalManager().createNew(new CompletePortal(customPortal, newLocation.getWorld(), geom), null, CustomPortalIgniteCause.EXIT_PORTAL, null);
			if (destination==null) return null;
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
		if (getTag("hidePortalInside") != null) return;
		if (p==null) {
			Bukkit.getScheduler().cancelTask(particlesTask);
			if (customPortal.isEnableParticles()) {
				particlesTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Dimensions.getInstance(), new Runnable() {
					
					@Override
					public void run() {
						if (getTag("hidePortalParticles")!=null) return;
						for (PortalEntity en : spawnedEntities) {
							en.emitParticles(customPortal.getParticlesColor());
						}
					}
				}, 20, 20);
			}
			
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
		
		if (p==null) {
			Bukkit.getScheduler().cancelTask(particlesTask);
		}
		
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
		return hold.contains(player) || queue.containsKey(player);
	}
	public void removeFromHold(Entity en) {
		hold.remove(en);
		try {
			Bukkit.getScheduler().cancelTask(queue.remove(en));
		} catch (NullPointerException e) {
			
		}
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
