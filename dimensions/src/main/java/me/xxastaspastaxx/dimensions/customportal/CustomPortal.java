package me.xxastaspastaxx.dimensions.customportal;

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
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;

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

	private float worldRatio;
	private float worldRatioReturn;
	
	boolean buildExitPortal;
	boolean spawnOnAir;
	
	List<String> disabledWorldsList;
	
	HashMap<EntityType,EntityType> entityTransformationList;
	int spawnDelayMin;
	int spawnDelayMax;
	HashMap<EntityType, Integer> entitySpawnList;
	public CustomPortal(String portalId, String displayName, boolean enabled, Material outsideMaterial, AxisOrFace outsideBlockDir,
			Material insideMaterial, int[] combinedID, BlockData[] insideBlockData, Material lighterMaterial, Color particlesColor, Sound breakSound, int minimumHeight,
			int maximumHeight, int maximumWidth, int minimumWidth, String worldName, float worldRatio, boolean buildExitPortal, boolean spawnOnAir,
			List<String> disabledWorldsList, HashMap<EntityType, EntityType> entityTransformationList,
			int spawnDelayMin, int spawnDelayMax, HashMap<EntityType, Integer> entitySpawnList) {
		this.portalId = portalId;
		this.displayName = displayName;
		this.enabled = enabled;
		this.outsideMaterial = outsideMaterial;
		this.outsideBlockDir = outsideBlockDir;
		this.insideMaterial = insideMaterial;
		this.combinedID = combinedID;
		this.insideBlockData = insideBlockData;
		this.lighterMaterial = lighterMaterial;
		this.particlesColor = particlesColor;
		this.breakSound = breakSound;
		this.minimumHeight = minimumHeight;
		this.maximumHeight = maximumHeight;
		this.maximumWidth = maximumWidth;
		this.minimumWidth = minimumWidth;
		this.worldName = worldName;
		this.worldRatio = worldRatio;
		this.worldRatioReturn = 1/worldRatio;
		this.buildExitPortal = buildExitPortal;
		this.spawnOnAir = spawnOnAir;
		this.disabledWorldsList = disabledWorldsList;
		this.entityTransformationList = entityTransformationList;
		this.spawnDelayMin = spawnDelayMin;
		this.spawnDelayMax = spawnDelayMax;
		this.entitySpawnList = entitySpawnList;
	}
	
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
	public int getCombinedID(boolean zAxis) {
		return combinedID[zAxis?1:0];
	}
	
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
	public float getWorldRatio() {
		return worldRatio;
	}
	public float getWorldRatioReturn() {
		return worldRatioReturn;
	}
	public boolean isBuildExitPortal() {
		return buildExitPortal;
	}
	public boolean isSpawnOnAir() {
		return spawnOnAir;
	}
	public List<String> getDisabledWorldsList() {
		return disabledWorldsList;
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
	
	public CompletePortal tryIgnite(Player player, ItemStack item, Location loc) {
		if (item==null || item.getType()!=lighterMaterial) return null;
		if (disabledWorldsList.contains(loc.getWorld().getName())) return null;
		PortalGeometry temp = PortalGeometry.getPortal(this, loc);
		if (temp==null) return null;
		
		return Dimensions.getCompletePortalManager().createNew(new CompletePortal(this, loc.getWorld(), temp), player, CustomPortalIgniteCause.PLAYER);
		
	}
	
	public boolean isPortalBlock(Block block) {
		return block.getType()==outsideMaterial && outsideBlockDir.isData(block.getBlockData());
	}
}
