package me.xxastaspastaxx.dimensions.completePortal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsDebbuger;
import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;
import me.xxastaspastaxx.dimensions.events.CustomPortalUseEvent;
import me.xxastaspastaxx.dimensions.settings.DimensionsSettings;
import me.xxastaspastaxx.dimensions.settings.WorldConfiguration;

/**
 * Class containing the info of a built portal
 *
 */

public class CompletePortal {
	
	private CustomPortal customPortal;

	private PortalGeometry portalGeometry;
	private World world;
	private int chunkX;
	private int chunkZ;

	private int particlesTask;
	private int entitiesTask;
	
	//We store the last linked world in case the plugin needs to return the player to the world he came from but for some reason the portal is broken
	private CompletePortal linkedPortal;
	private World lastLinkedWorld;
	
	//The fake falling block entities are stored here in order to spawn.despawn them
	private ArrayList<PortalEntity> spawnedEntities = new ArrayList<PortalEntity>();
	
	//We keep a list of players that have been teleported to the portal in order to not spam teleport them around
	private ArrayList<Entity> hold = new ArrayList<Entity>();
	private HashMap<Entity, Integer> queue = new HashMap<Entity, Integer>();
	
	private HashMap<String, Object> tags = new HashMap<String, Object>();
	
	private boolean brokenPortal = false;
	
	/**
	 * Construct the CompletePortal
	 * If <b>portalGeometry</b> is not null, create <a href="../PortalEntity.html">PortalEntities</a>
	 * @param customPortal the customPortal that is built
	 * @param world the world that is built
	 * @param portalGeometry PortalGeometry of the portal
	 */
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
	
	/**
	 * Construct the CompletePortal
	 * If <b>portalGeometry</b> is not null, create <a href="../PortalEntity.html">PortalEntities</a>
	 * If <b>linked</b> is not null, links the portals
	 * @param customPortal the customPortal that is built
	 * @param world the world that is built
	 * @param portalGeometry PortalGeometry of the portal
	 * @param linked The portal to be linked with
	 */
	public CompletePortal(CustomPortal customPortal, World world, PortalGeometry portalGeometry, CompletePortal linked) {
		this(customPortal, world, portalGeometry);
		
		if (linked==null) return;
		setLinkedPortal(linked);
		linked.setLinkedPortal(this);
	}
	
	
	/**
	 * Get the CustomPortal
	 * @return the CustomPortal
	 */
	public CustomPortal getCustomPortal() {
		return customPortal;
	}
	/**
	 * Get the world of the portal
	 * @return the world of the portal
	 */
	public World getWorld() {
		return world;
	}
	/**
	 * Get the portal geometry of the portal
	 * @return the portal geometry of the portal
	 */
	public PortalGeometry getPortalGeometry() {
		return portalGeometry;
	}

	/**
	 * Get the portal that is linked with
	 * @return null if there is no linked portal or the linked portal
	 */
	public CompletePortal getLinkedPortal() {
		return linkedPortal;
	}
	/**
	 * Unlink the portal
	 */
	public void unlinkPortal() {
		linkedPortal = null;
	}
	
	/**
	 * Link to a portal
	 * @param complete the portal to link to
	 */
	public void setLinkedPortal(CompletePortal complete) {
		linkedPortal = complete;
		lastLinkedWorld = complete.getWorld();
	}
	
	/**
	 * Get the center of the portal
	 * @return the center of the portal
	 */
	public Location getCenter() {
		return portalGeometry.getCenter().toLocation(world);
	}
	
	/**
	 * Get the PortalEntity list
	 * @return the PortalEntity list
	 */
	public ArrayList<PortalEntity> getPortalEntities() {
		return spawnedEntities;
	}
	
	
	private ArrayList<Entity> savedEntities = new ArrayList<Entity>();
	/**
	 * Check for nearby entities to teleport
	 */
	public void updatePortal() {
		if (!isActive() || brokenPortal) return;

		savedEntities.addAll(world.getNearbyEntities(portalGeometry.getBoundingBox(), new Predicate<Entity>() {
			@Override
			public boolean test(Entity t) {
				return !savedEntities.contains(t) && !(t instanceof Player) && !hold.contains(t);
			}
		}));
		
		ArrayList<Entity> toRemove = new ArrayList<Entity>();
		savedEntities.stream().filter(en -> !isInsidePortal(en.getLocation(), false, false) && (!(en instanceof LivingEntity) || !isInsidePortal(((LivingEntity) en).getEyeLocation(), false, false))).forEach(en -> toRemove.add(en));
		
		toRemove.forEach(en -> {removeFromHold(en); savedEntities.remove(en);});
		
		savedEntities.forEach(en -> { if (!hasInHold(en)) {handleEntity(en);}});
	}
	
	/**
	 * Start the teleport countdown for the entity if they are not in hold.
	 * The delay for Creative and Spectator players is 0
	 * Call the CustomPortalUseEvent and if its not cancelled the teleport the entity
	 * @param en the entity to teleport
	 * 
	 * @see CustomPortalUseEvent
	 */
	public void handleEntity(Entity en) {
		if (hold.contains(en) || brokenPortal) return;
		
		int delay = customPortal.getTeleportDelay()*20;
		if ((en instanceof Player) && (((Player) en).getGameMode()==GameMode.CREATIVE || ((Player) en).getGameMode()==GameMode.SPECTATOR)) delay = 0;
		
		queue.put(en, Bukkit.getScheduler().scheduleSyncDelayedTask(Dimensions.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				if (brokenPortal) return;
				CustomPortalUseEvent useEvent = new CustomPortalUseEvent(CompletePortal.this, en, getDestinationPortal(false, null, null));
				Bukkit.getPluginManager().callEvent(useEvent);

				if (useEvent.isCancelled()) return;
				
				if (tags.containsKey("disableTP")) {
					tags.remove("disableTP");
					DimensionsDebbuger.DEBUG.print("DISABLE");
					return;
				}
				
				CompletePortal destination = useEvent.getDestinationPortal();
				
				//If no portal was put as a destination from other sources, we create our own
				if (destination==null) {
					if (customPortal.canBuildExitPortal()) {
						destination = getDestinationPortal(true, null, null);
					} else {
						Location destLoc = getDestinationLocation(null, null);
						destination = new CompletePortal(customPortal, destLoc.getWorld(), portalGeometry.createGeometry(destLoc.toVector(), destLoc.toVector()));
						
						Block b = destination.getCenter().getBlock().getRelative(BlockFace.DOWN);
						if (!b.getType().isSolid()) b.setType(customPortal.getOutsideMaterial());
					}
				}
				
				
				Location teleportLocation = destination.getCenter().clone();
				teleportLocation.setY(destination.getPortalGeometry().getInsideMin().getY());
				teleportLocation.setYaw(en.getLocation().getYaw());
				teleportLocation.setPitch(en.getLocation().getPitch());
				
				EntityType trasnformation = customPortal.getEntityTransformation(en.getType());
				if (trasnformation==null) {
					destination.pushToHold(en);
					en.teleport(teleportLocation);

					removeFromHold(en);
				} else {
					Entity newEn = teleportLocation.getWorld().spawnEntity(teleportLocation, trasnformation);

					DimensionsUtils.cloneEntity(en, newEn);
					destination.pushToHold(newEn);
					
					en.remove();
				}
				
				
			}
		}, delay));
		
		//customPortal.usePortal(en, this);
	}
	
	/**
	 * Check if the location is inside the portal
	 * @param loc location to check
	 * @param outside true to count the frame of the portal
	 * @param corner true to count the corners of the portal
	 * @return true if the location is inside the portal
	 */
	public boolean isInsidePortal(Location loc, boolean outside, boolean corner) {
		return loc.getWorld().equals(world) && portalGeometry.isInside(loc, outside, corner);
	}
	
	/**
	 * Calculate the teleport destination for the portal
	 * Get the destination world if its not being overriden
	 * Fix the world ratio using the world sizes
	 * Fix the height ratio using the world min/max heights
	 * @param overrideLocation override the location with this
	 * @param overrideWorld override the world with this
	 * @return the destination location to try and build an exit portal to use
	 */
	public Location getDestinationLocation(Location overrideLocation, World overrideWorld) {
		Location newLocation = overrideLocation==null?getCenter():overrideLocation;

		
		World destinationWorld = overrideWorld;
		if (destinationWorld==null) {
			destinationWorld = customPortal.getWorld();
			if (world.equals(destinationWorld))
				destinationWorld = lastLinkedWorld==null?DimensionsSettings.fallbackWorld:lastLinkedWorld;
		}
		newLocation.setWorld(destinationWorld);
		
		//Fix world ratio
		newLocation = newLocation.multiply(getWorldRatio(destinationWorld));
		WorldBorder border = destinationWorld.getWorldBorder();
		if (!border.isInside(newLocation)) {

			double borderX = border.getCenter().getX();
			double borderZ = border.getCenter().getZ();
			double borderSize = (border.getSize()/2)-(getPortalGeometry().getPortalWidth(customPortal)*2);
			
			if (newLocation.getX()>borderX) {
				newLocation.setX(Math.min(newLocation.getX(), borderX+borderSize));
			} else {
				newLocation.setX(Math.max(newLocation.getX(), borderX-borderSize));
			}
			
			if (newLocation.getZ()>borderZ) {
				newLocation.setZ(Math.min(newLocation.getZ(), borderZ+borderSize));
			} else {
				newLocation.setZ(Math.max(newLocation.getZ(), borderZ-borderSize));
			}
			
		}

		WorldConfiguration currWorldConfig = DimensionsSettings.getWorldConfiguration(world);
		WorldConfiguration destWorldConfig = DimensionsSettings.getWorldConfiguration(destinationWorld);
		
		//FIX the wolrd height ratio
		int currMinWorldHeight = currWorldConfig.getMinHeight();
		int currMaxWorldHeight = currWorldConfig.getMaxHeight();
		int currWorldHeight = currMaxWorldHeight-currMinWorldHeight;
		
		int minWorldHeight = destWorldConfig.getMinHeight();
		int maxWorldHeight = destWorldConfig.getMaxHeight();
		int worldHeight = maxWorldHeight-minWorldHeight;

		double currPercent = (getCenter().getY()-currMinWorldHeight)/currWorldHeight;
		
		newLocation.setY(worldHeight*currPercent+minWorldHeight);
		
		return newLocation;
	}
	
	/**
	 * Get the destination portal to use
	 * @param buildNewPortal true to build an exit portal
	 * @param overrideLocation location to override
	 * @param overrideWorld world to override
	 * @return the portal to use
	 */
	public CompletePortal getDestinationPortal(boolean buildNewPortal, Location overrideLocation, World overrideWorld) {

		if (linkedPortal!=null) return linkedPortal;
		
		Location newLocation = getDestinationLocation(overrideLocation, overrideWorld);
		DimensionsDebbuger.DEBUG.print("New Location: "+newLocation);
		World destinationWorld = newLocation.getWorld();
		DimensionsDebbuger.DEBUG.print("Destination World: "+destinationWorld);
		double ratio = getWorldRatio(destinationWorld);
		DimensionsDebbuger.DEBUG.print("Ratio: "+ratio);
		//===============
		
		CompletePortal destination = null;
		if (DimensionsSettings.searchFirstClonePortal) 
			destination = Dimensions.getCompletePortalManager().getNearestPortal(newLocation, this, ratio, true, true);
		
		if (destination==null)
			destination = Dimensions.getCompletePortalManager().getNearestPortal(newLocation, this, ratio, DimensionsSettings.searchSameAxis, DimensionsSettings.searchSameSize);
		

		DimensionsDebbuger.DEBUG.print("First try for destination (check for already existing portal): "+destination);
		if (destination!=null) {
			DimensionsDebbuger.DEBUG.print("Destination not null. DestLocation: "+destination.getCenter());
		}
		
		if (destination==null) {
			DimensionsDebbuger.DEBUG.print("Destination not found, attempting to create a portal: ");
			if (!buildNewPortal) {
				DimensionsDebbuger.DEBUG.print("buildExitPortal var is false, skipping this time");
				return null;
			}
			boolean zAxis = portalGeometry.iszAxis();
			byte width = portalGeometry.getPortalWidth(customPortal);
			byte height = portalGeometry.getPortalHeight(customPortal);

			DimensionsDebbuger.DEBUG.print("Exit portal info: zAxis: "+zAxis+", width: "+width+", height: "+height);

			//TODO find best location
			Location checkLocation = getSafeLocation(newLocation, zAxis, destinationWorld, height, width);
			DimensionsDebbuger.DEBUG.print("SafeLocation found: "+checkLocation);
			if (checkLocation!=null) newLocation = checkLocation;
			DimensionsDebbuger.DEBUG.print("Final location: "+newLocation);
			

			DimensionsDebbuger.DEBUG.print("Attempting to build portal...");
			portalGeometry.buildPortal(newLocation, destinationWorld, customPortal);
			DimensionsDebbuger.DEBUG.print("Portal should be built at: "+newLocation);
			
			PortalGeometry geom = PortalGeometry.getPortalGeometry(customPortal).getPortal(customPortal, newLocation.add(zAxis?0:1,1,zAxis?1:0));

			DimensionsDebbuger.DEBUG.print("Identify built structure: "+(geom==null?"NOPE":"Yep"));
			if (geom==null) return null;
			destination = Dimensions.getCompletePortalManager().createNew(new CompletePortal(customPortal, newLocation.getWorld(), geom), null, CustomPortalIgniteCause.EXIT_PORTAL, null);
			DimensionsDebbuger.DEBUG.print("Created portal instance: "+(destination==null?"NOPE":"Yep"));
			if (destination==null) return null;
		}
		
		if (destination.getLinkedPortal()==null) {
			setLinkedPortal(destination);
			destination.setLinkedPortal(this);
		}
		
		DimensionsDebbuger.DEBUG.print("Final destination portal "+(destination==null?"Houston, we have a problem":destination.getCenter()));
		
		return destination;
	}
	
	/**
	 * Calculate the world ratio from the world sizes
	 * @param destinationWorld world to get the ratio from
	 * @return the world ratio
	 */
	public double getWorldRatio(World destinationWorld) {
		double currWorldSize = DimensionsSettings.getWorldConfiguration(world).getSize();
		double worldSize = DimensionsSettings.getWorldConfiguration(destinationWorld).getSize();
		double ratio = worldSize/currWorldSize;
		
		return ratio;
	}
	
	private Location getSafeLocation(Location newLocation, boolean zAxis, World destinationWorld, int height, int width) {
		Location backupLocation = null;
		Location backupLocation2 = null;
		Location checkLocation;
	
		WorldConfiguration destWorldConfig = DimensionsSettings.getWorldConfiguration(destinationWorld);
		int maxWorldHeight = destWorldConfig.getMaxHeight()-height;
		
		for (int m =0;m<DimensionsSettings.safeSpotSearchRadius;m++) {
			checkLocation = newLocation.clone();

			boolean isCenter = !DimensionsSettings.safeSpotSearchAllY || m<DimensionsSettings.safeSpotSearchRadius-1;
			//tp 3042.23 109.00 2005.12
			///tp 2039.49 108.00 1006.62
			int y = 0;
			int yAdd = 1;
			boolean step1 = true;
			while ((isCenter && y!=m+1) || !isCenter) {
				checkLocation.setY(newLocation.getY()+y);
				if (checkLocation.getY()>=destWorldConfig.getMinHeight() && checkLocation.getY()<=maxWorldHeight) {
					step1 = true;
					
					int dir = 0; 
	
					int x = 0;
					int z = 0;
					float travel = 1;
					if (!(y>=m || y<=-m)) {
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
							if (Dimensions.getCompletePortalManager().getCompletePortal(checkLocation, true, true)==null) {
								if (canBuildPortal(checkLocation, zAxis, destinationWorld, height, width, true)) return checkLocation;
								if (backupLocation==null && canBuildPortal(checkLocation, !zAxis, destinationWorld, height, width, true)) backupLocation = checkLocation.clone();
								if (backupLocation2==null && canBuildPortal(checkLocation, zAxis, destinationWorld, height, width, false)) backupLocation2 = checkLocation.clone();
							}
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
				} else if (!isCenter) {
					if ((step1 = !step1)) break;
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
	
	/**
	 * Set the block or summon the falling block entities for all the PortalEntities
	 * @param p null to broadcast the packets or the player to send the packets to
	 */
	public void fill(Player p) {
		if (p==null && customPortal.canSpawnEntities()) {

			Bukkit.getScheduler().cancelTask(entitiesTask);
			entitiesTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Dimensions.getInstance(), new Runnable() {
				
				@Override
				public void run() {
					if (!isActive()) return;
					
					EntityType type = customPortal.getNextSpawn();
					if (type==null) return;
					
					Location spawnLoc = getCenter().clone();
					spawnLoc.setY(portalGeometry.getInsideMin().getY());
					
					Entity en = world.spawnEntity(spawnLoc, type);
					pushToHold(en);
				}
			}, customPortal.getSpawnDelay(), customPortal.getSpawnDelay());
		}
		
		if (getTag("hidePortalInside") != null) return;
		if (p==null) {
			Bukkit.getScheduler().cancelTask(particlesTask);
			if (customPortal.isEnableParticles()) {
				particlesTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Dimensions.getInstance(), new Runnable() {
					
					@Override
					public void run() {
						
						
						if (!isActive() || getTag("hidePortalParticles")!=null) return;
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
			Bukkit.getScheduler().runTaskLater(Dimensions.getInstance(), new Runnable() {
				
				@Override
				public void run() {
					en.summon(p);
					
				}
			}, DimensionsSettings.portalInsideDelay);
		}
	}

	/**
	 * Despawn the entities or change the block to air inside the portal
	 * @param p null to stop the running tasks or the player to play the destroy packet
	 */
	public void destroy(Player p) {
		
		if (p==null) {
			Bukkit.getScheduler().cancelTask(particlesTask);
			Bukkit.getScheduler().cancelTask(entitiesTask);
			brokenPortal = true;
		}
		
		world.playSound(getCenter(), customPortal.getBreakSound(), 1, 8);
		
		Particle blockCrackParticle = Particle.valueOf("BLOCK_CRACK") == null ? Particle.valueOf("BLOCK_CRUMBLE") : Particle.valueOf("BLOCK_CRACK");
		for (PortalEntity en : spawnedEntities) {
			world.spawnParticle(blockCrackParticle, en.getLocation(), 10, customPortal.getInsideBlockData(false));
			if (p==null)
				en.destroyBroadcast();
			else
				en.destroy(p);
		}
	}
	
	/**
	 * Check if the portal is inside the given chunk
	 * @param world2 the world of the chunk
	 * @param x the X of the chunk
	 * @param z the Z of the chunk
	 * @return true if all match
	 */
	public boolean isInChunk(World world2, int x, int z) {
		
		return world.equals(world2) && chunkX==x && chunkZ==z;
	}

	/**
	 * Add the entity to hold so it won't be teleported
	 * @param en the entity to add
	 */
	public void pushToHold(Entity en) {
		hold.add(en);
	}

	/**
	 * Check if the entity is on hold or in the queue to teleport
	 * @param en the player to check
	 * @return true if the player is in hold or queue
	 */
	public boolean hasInHold(Entity en) {
		return hold.contains(en) || queue.containsKey(en);
	}
	
	/**
	 * Remove the entity from hold and cancel the teleport task
	 * @param en the entity to remove
	 */
	public void removeFromHold(Entity en) {
		hold.remove(en);
		try {
			Bukkit.getScheduler().cancelTask(queue.remove(en));
		} catch (NullPointerException e) {
			
		}
	}
	
	/**
	 * Set a portal tag (tags are stored even after restart)
	 * @param key the key of the tag
	 * @param value the value of the tag
	 */
	public void setTag(String key, Object value) {
		if (value==null)
			tags.remove(key);
		else
			tags.put(key, value);
	}
	
	/**
	 * Get the value of the tag
	 * @param key
	 * @return the value
	 */
	public Object getTag(String key) {
		return tags.get(key);
	}

	/**
	 * Get the portal tags
	 * @return the portal tags
	 */
	public HashMap<String, Object> getTags() {
		return tags;
	}

	/**
	 * Override the portal tags
	 * @param tags
	 */
	public void setTags(HashMap<String, Object> tags) {
		this.tags = tags;
	}
	
	/**
	 * Check if the chunk the portal is in is loaded
	 * @return true if chunk is loaded
	 */
	public boolean isActive() {
		return world.isChunkLoaded(chunkX, chunkZ);
	}
}
