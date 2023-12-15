package me.xxastaspastaxx.dimensions.customportal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.xxastaspastaxx.dimensions.AxisOrFace;
import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPlayerGUIAction;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;

/**
 * The class that contains all the data for a new portal type
 *
 */

public class CustomPortal {
	
	private String portalId;
	private String displayName;
	private boolean enabled;
	
	private Material outsideMaterial;
	private AxisOrFace outsideBlockDir;
	private Material insideMaterial;
	private int[] combinedID;
	private BlockData[] insideBlockData;
	private Material lighterMaterial;

	private Color particlesColor;
	private Sound breakSound;

	private int minimumHeight;
	private int maximumHeight;
	private int maximumWidth;
	private int minimumWidth;
	
	private String worldName;

//	private float worldRatio;
//	private float worldRatioReturn;

	private boolean buildExitPortal;
	private int fixedExitPortalWidth;
	private int fixedExitPortalHeight;

	private List<String> allowedWorldsList;
	
	private int teleportDelay;
	private boolean enableParticles;
	
	private HashMap<EntityType,EntityType> entityTransformationList;
	private int spawnDelayMin;
	private int spawnDelayMax;
	private HashMap<EntityType, Integer> entitySpawnList;
	
	private ArrayList<DimensionsAddonPlayerGUIAction> playerGUIActions = new ArrayList<DimensionsAddonPlayerGUIAction>();
	
	/**
	 * Constructor of CustomPortal
	 * @param portalId
	 * @param displayName
	 * @param enabled
	 * @param outsideMaterial
	 * @param outsideBlockDir
	 * @param insideMaterial
	 * @param lighterMaterial
	 * @param particlesColor
	 * @param breakSound
	 * @param minimumHeight
	 * @param maximumHeight
	 * @param maximumWidth
	 * @param minimumWidth
	 * @param worldName
	 * @param buildExitPortal
	 * @param fixedExitPortalWidth
	 * @param fixedExitPortalHeight
	 * @param allowedWorldsList
	 * @param teleportDelay
	 * @param enableParticles
	 * @param entityTransformationList
	 * @param spawnDelayMin
	 * @param spawnDelayMax
	 * @param entitySpawnList
	 */
	public CustomPortal(String portalId, String displayName, boolean enabled, Material outsideMaterial, AxisOrFace outsideBlockDir,
			Material insideMaterial, Material lighterMaterial, Color particlesColor, Sound breakSound, int minimumHeight,
			int maximumHeight, int maximumWidth, int minimumWidth, String worldName, boolean buildExitPortal, int fixedExitPortalWidth, int fixedExitPortalHeight,
			List<String> allowedWorldsList, int teleportDelay, boolean enableParticles, HashMap<EntityType, EntityType> entityTransformationList,
			int spawnDelayMin, int spawnDelayMax, HashMap<EntityType, Integer> entitySpawnList) {
		this.portalId = portalId;
		this.displayName = displayName;
		this.enabled = enabled;
		this.outsideMaterial = outsideMaterial;
		this.outsideBlockDir = outsideBlockDir;
		this.insideMaterial = insideMaterial;
		this.lighterMaterial = lighterMaterial;
		this.particlesColor = particlesColor;
		this.breakSound = breakSound;
		this.minimumHeight = minimumHeight;
		this.maximumHeight = maximumHeight;
		this.maximumWidth = maximumWidth;
		this.minimumWidth = minimumWidth;
		this.worldName = worldName;
//		this.worldRatio = worldRatio;
//		this.worldRatioReturn = 1/worldRatio;
		this.buildExitPortal = buildExitPortal;
		this.fixedExitPortalWidth = fixedExitPortalWidth;
		this.fixedExitPortalHeight = fixedExitPortalHeight;
		
		/*if (allowedWorldsList.contains("!")) {
			for (String str : allowedWorldsList) {
				if (str.startsWith("!")) disabledWorldsList.add(str.replace("!", ""));
			}
		}*/
		this.allowedWorldsList = allowedWorldsList;
		
		
		this.teleportDelay = teleportDelay;
		this.enableParticles = enableParticles;
		this.entityTransformationList = entityTransformationList;
		this.spawnDelayMin = spawnDelayMin;
		this.spawnDelayMax = spawnDelayMax;
		this.entitySpawnList = entitySpawnList;
	}
	
	/**
	 * The id of the portal (the file name without the .yml)
	 */
	public String getPortalId() {
		return portalId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public Material getOutsideMaterial() {
		return outsideMaterial;
	}
	public AxisOrFace getAxisOrFace() {
		return outsideBlockDir;
	}
	public Material getInsideMaterial() {
		return insideMaterial;
	}
	/**
	 * Get the combined id to summon the fake blocks
	 * @param zAxis true to get the ids for the Z axis
	 */
	public int getCombinedID(boolean zAxis) {
		return combinedID[zAxis?1:0];
	}
	
	/**
	 * Get the BlockData to place the block
	 * @param zAxis true to get the blockData for the Z axis
	 */
	public BlockData getInsideBlockData(boolean zAxis) {
		return insideBlockData[zAxis?1:0];
	}
	public Material getLighterMaterial() {
		return lighterMaterial;
	}
	public Color getParticlesColor() {
		return particlesColor;
	}
	public Sound getBreakSound() {
		return breakSound;
	}
	public int getMinimumHeight() {
		return minimumHeight;
	}
	public int getMaximumHeight() {
		return maximumHeight;
	}
	public int getMaximumWidth() {
		return maximumWidth;
	}
	public int getMinimumWidth() {
		return minimumWidth;
	}
	public String getWorldName() {
		return worldName;
	}
	public World getWorld() {
		return Bukkit.getWorld(worldName);
	}
	
	public boolean canBuildExitPortal() {
		return buildExitPortal;
	}
	
	public int getFixedExitPortalWidth() {
		return fixedExitPortalWidth;
	}
	
	public int getFixedExitPortalHeight() {
		return fixedExitPortalHeight;
	}
	
	public List<String> getAllowWorldsList() {
		return allowedWorldsList;
	}
	
	public boolean isAllowedWorld(World world) {
		String worldName = world.getName();
		if (allowedWorldsList.contains("!"+worldName)) return false;
		if (allowedWorldsList.contains("all") || allowedWorldsList.contains(worldName)) return true;
		
		return false;
	}
	
	public int getTeleportDelay() {
		return teleportDelay;
	}

	public boolean isEnableParticles() {
		return enableParticles;
	}

	public int getMinSpawnTime() {
		return spawnDelayMin;
	}
	
	public int getMaxSpawnTime() {
		return spawnDelayMax;
	}
	
	public HashMap<EntityType, EntityType> getEntityTransformationList() {
		return entityTransformationList;
	}
	public int getSpawnDelayMin() {
		return spawnDelayMin;
	}
	public int getSpawnDelayMax() {
		return spawnDelayMax;
	}
	public HashMap<EntityType, Integer> getEntitySpawnList() {
		return entitySpawnList;
	}
	
	/**
	 * Check if there is a portal structure at the location and ignite it
	 * @param player the player igniting the portal
	 * @param item the item used to ignite the portal
	 * @param loc the location of the portal
	 * @return null if there is no portal or the CompletePortal that was ignited
	 */
	public CompletePortal tryIgnite(Player player, ItemStack item, Location loc) {
		if (item==null || (lighterMaterial!=null && item.getType()!=lighterMaterial)) return null;
		if (!isAllowedWorld(loc.getWorld())) return null;
		PortalGeometry temp = PortalGeometry.getPortalGeometry(this).getPortal(this, loc);
		if (temp==null) return null;
		
		return Dimensions.getCompletePortalManager().createNew(new CompletePortal(this, loc.getWorld(), temp), player, CustomPortalIgniteCause.PLAYER, item);
		
	}
	
	/**
	 * Check if the block is portal block
	 * @param block the block to check
	 * @return true if the block is accepted
	 */
	public boolean isPortalBlock(Block block) {
		return block.getType()==outsideMaterial && outsideBlockDir.isData(block.getBlockData());
	}
	
	/**
	 * Override the inside block data
	 * @param blockData
	 */
	public void setInsideBlockData(BlockData blockData) {
		insideBlockData = new BlockData[] {CustomPortalLoader.getInsideBlockData(false, blockData.clone()),CustomPortalLoader.getInsideBlockData(true, blockData.clone())};
		combinedID = CustomPortalLoader.createCombinedID(insideBlockData, blockData.getMaterial());
		
	}

	/**
	 * Get the next entity type to spawn from the list
	 */
	public EntityType getNextSpawn() {
		return entitySpawnList.keySet().stream().filter(type -> entitySpawnList.get(type)>=DimensionsUtils.getRandom(0, 100)).findAny().orElse(null);
	}
	
	/**
	 * Get if the portal spawns entities
	 */
	public boolean canSpawnEntities() {
		return !entitySpawnList.isEmpty();
	}
	
	/**
	 * Get a random spawn delay for the spawn task
	 */
	public int getSpawnDelay() {
		return DimensionsUtils.getRandom(spawnDelayMin, spawnDelayMax)/50;
	}

	/**
	 * Get entitity transformatioion
	 * @param the type of entity using the portal
	 */
	public EntityType getEntityTransformation(EntityType type) {
		return entityTransformationList.get(type);
	}

	/**
	 * @return list of the actions that can be executed by addons
	 */
	public ArrayList<DimensionsAddonPlayerGUIAction> getAddonPlayerGUIActions() {
		return playerGUIActions;
	}
	
	public void add(ArrayList<DimensionsAddonPlayerGUIAction> newPlayerGUIActions) {
		playerGUIActions.addAll(newPlayerGUIActions);
	}
	
	public void add(DimensionsAddonPlayerGUIAction newPlayerGUIAction) {
		playerGUIActions.add(newPlayerGUIAction);
	}
}
