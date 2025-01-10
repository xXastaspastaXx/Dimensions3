package me.xxastaspastaxx.dimensions.customportal;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Axis;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Orientable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.utility.MinecraftReflection;

import me.xxastaspastaxx.dimensions.AxisOrFace;
import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;

/**
 * Loads all the custom portals
 */

public class CustomPortalLoader {
	
	public static final String DIRECTORY_PATH = "./plugins/Dimensions/Portals";
	public static final File PORTALS_DIRECTORY = new File(DIRECTORY_PATH);
	public static final String CONFIG_VERSION = "3.0.1";
	
	private static Class<?> blockClass;
	private static Class<?> craftBlockDataClass;
	private static Method getCombinedIdMethod;
	private static Method getStateMethod;
	
	/**
	 * Cunstructor of the loader
	 */
	public CustomPortalLoader() {
		try {
			
			blockClass = MinecraftReflection.getBlockClass();
			craftBlockDataClass = MinecraftReflection.getCraftBukkitClass("block.data.CraftBlockData");
			try {
				getCombinedIdMethod = blockClass.getMethod("i",MinecraftReflection.getIBlockDataClass());
			} catch (NoSuchMethodException e) {
				try {
					getCombinedIdMethod = blockClass.getMethod("getCombinedId",MinecraftReflection.getIBlockDataClass());
				} catch (NoSuchMethodException e2) {
					getCombinedIdMethod = blockClass.getMethod("j",MinecraftReflection.getIBlockDataClass());
				}
			}
			getStateMethod = craftBlockDataClass.getMethod("getState");
			
		} catch (NoSuchMethodException | IllegalArgumentException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Load all the custom portals
	 */
	public ArrayList<CustomPortal> loadAll() {
		ArrayList<CustomPortal> res = new ArrayList<CustomPortal>();
		
		File portalFolder = new File(DIRECTORY_PATH);
		if (!portalFolder.exists()) portalFolder.mkdir();
		
		PortalGeometry.instance = PortalGeometry.nullGeometry();
		
		for (File f : PORTALS_DIRECTORY.listFiles()) {
			String portalID = f.getName().replace(".yml", "");
			if (portalID.contains(" ")) continue;
			
			YamlConfiguration portalConfig = YamlConfiguration.loadConfiguration(f);
			
			String fVersion = portalConfig.getString("configVersion", "pre3");
			if (!fVersion.equals(CONFIG_VERSION)) {
				
				if (portalConfig.contains("Options.BuildExitPortal")) {
					portalConfig.set("Options.ExitPortal.Enable", portalConfig.getBoolean("Options.BuildExitPortal"));
				} else {
					portalConfig.set("Options.ExitPortal.Enable", true);
				}
				
				portalConfig.set("Options.BuildExitPortal", null);
				
				portalConfig.set("Options.ExitPortal.FixedWidth", -1);
				portalConfig.set("Options.ExitPortal.FixedHeight", -1);
				
				portalConfig.set("configVersion", CONFIG_VERSION);
				
				try {
					portalConfig.save(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			boolean enabled = portalConfig.getBoolean("Enable", false);
			String displayName = portalConfig.getString("DisplayName", "Unnamed");

			Material outsideMaterial = Material.matchMaterial(portalConfig.getString("Portal.Frame.Material", "COBBLESTONE"));
			AxisOrFace outsideBlockDir = new AxisOrFace(portalConfig.getString("Portal.Frame.Face", "all"));
			Material insideMaterial = Material.matchMaterial(portalConfig.getString("Portal.InsideMaterial", "NEHTER_PORTAL"));

//			BlockData[] insideBlockData = new BlockData[] {getInsideBlockData(false, tempBlockData),getInsideBlockData(true, tempBlockData)};
//			int[] combinedId = createCombinedID(insideBlockData, insideMaterial);
			
			String ligherMaterialString = portalConfig.getString("Portal.LighterMaterial", "FLINT_AND_STEEL");
			Material lighterMaterial = ligherMaterialString.equalsIgnoreCase("null")?null:Material.matchMaterial(ligherMaterialString);
			String[] particlesColorString = portalConfig.getString("Portal.ParticlesColor", "0;0;0").split(";");
			Color particlesColor = Color.fromBGR(Integer.parseInt(particlesColorString[2]), Integer.parseInt(particlesColorString[1]), Integer.parseInt(particlesColorString[0]));
			
			Sound breakEffect = Sound.valueOf(portalConfig.getString("Portal.BreakEffect", "BLOCK_GLASS_BREAK"));
			

			int minimumHeight = portalConfig.getInt("Portal.MinimumHeight", 4);
			int maximumHeight = portalConfig.getInt("Portal.MaximumHeight", 15);
			

			int maximumWidth = portalConfig.getInt("Portal.MaximumWidth", 14);
			int minimumWidth = portalConfig.getInt("Portal.MinimumWidth", 3);
			
			String worldName = portalConfig.getString("World.Name", "world");
			/*if (DimensionsSettings.generateNewWorlds && !Bukkit.getServer().getWorlds().contains(Bukkit.getWorld(worldName))) {
				Bukkit.getServer().createWorld(new WorldCreator(worldName));
			}*/
			
//			String[] ratioString = portalConfig.getString("World.Ratio", "1:1").split(":");
//			int ratio0 = Integer.parseInt(ratioString[0]);
//			int ratio1 = Integer.parseInt(ratioString[1]);
//			int ratio = ratio1/ratio0;
			
			List<String> allowedWorlds = portalConfig.getStringList("Options.AllowedWorlds");
			if (allowedWorlds.size()==0) allowedWorlds.add("all");
			

			boolean buildExitPortal = portalConfig.getBoolean("Options.ExitPortal.Enable", true);
			int fixedExitPortalWidth = portalConfig.getInt("Options.ExitPortal.FixedWidth", -1);
			int fixedExitPortalHeight = portalConfig.getInt("Options.ExitPortal.FixedHeight", -1);
			
			int teleportDelay = portalConfig.getInt("Options.TeleportDelay", 4);
			boolean enableParticles = portalConfig.getBoolean("Options.EnableParticles", true);
			
			HashMap<EntityType,EntityType> entityTransformation = new HashMap<EntityType,EntityType>();
			for (String entity : portalConfig.getStringList("Entities.Transformation")) {
				String[] spl = entity.toUpperCase().split("->");
				entityTransformation.put(EntityType.valueOf(spl[0]), EntityType.valueOf(spl[1]));
			}
			
			String s = portalConfig.getString("Entities.Spawning.Delay", "5000-10000");
			int[] spawningDelay = new int[2]; 
			if (s.contains("-")) {
				String[] spawningDelayString = s.split("-");
				spawningDelay = new int[] {Integer.parseInt(spawningDelayString[0]),Integer.parseInt(spawningDelayString[1])};
			} else {
				int delay = Integer.parseInt(s);
				spawningDelay = new int[] {delay, delay};
			}
			HashMap<EntityType,Integer> entitySpawning = new HashMap<EntityType,Integer>();
			for (String entity : portalConfig.getStringList("Entities.Spawning.List")) {
				String[] spl = entity.toUpperCase().split(";");
				entitySpawning.put(EntityType.valueOf(spl[0]), Integer.parseInt(spl[1]));
			}
			CustomPortal portal = new CustomPortal(portalID, displayName, enabled, outsideMaterial, outsideBlockDir, insideMaterial, lighterMaterial, particlesColor,breakEffect,minimumHeight,maximumHeight, maximumWidth, minimumWidth,
					worldName,buildExitPortal, fixedExitPortalWidth, fixedExitPortalHeight, allowedWorlds, teleportDelay, enableParticles, entityTransformation, spawningDelay[0], spawningDelay[1], entitySpawning);
			portal.setInsideBlockData(insideMaterial.createBlockData());
			for (DimensionsAddon addon : Dimensions.getAddonManager().getAddons()) {
				addon.registerPortal(portalConfig, portal);
			}
			res.add(portal);
		}
		
		return res;
	}

	/**
	 * Creates combinedID for the block data inside the portal
	 * @param insideBlockData
	 * @param insideMaterial
	 * @return
	 */
	public static int[] createCombinedID(BlockData[] insideBlockData, Material insideMaterial) {
		int combinedId[] = new int[2];
		if (insideMaterial.isSolid() || insideMaterial==Material.NETHER_PORTAL || insideMaterial==Material.END_GATEWAY) {
			try {
				Object nmsBlockData = getStateMethod.invoke(insideBlockData[0]);
				combinedId[0] = (int) getCombinedIdMethod.invoke(blockClass,nmsBlockData);
				
				nmsBlockData = getStateMethod.invoke(insideBlockData[1]);
				combinedId[1] = (int) getCombinedIdMethod.invoke(blockClass,nmsBlockData);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				e1.printStackTrace();
			}
		}
		return combinedId;
	}

	/**
	 * Creates BlockData in the correct Z Axis
	 * @param zAxis
	 * @param blockData
	 * @return
	 */
	public static BlockData getInsideBlockData(boolean zAxis, BlockData blockData) {
		if (zAxis) {
			if (blockData instanceof Orientable) {
				Orientable orientable = (Orientable) blockData;
				orientable.setAxis(Axis.Z);
				blockData = orientable;
			} else if (blockData instanceof Directional) {
				Directional directional = (Directional) blockData;
				directional.setFacing(BlockFace.NORTH);
				blockData = directional;
			} else if (blockData instanceof MultipleFacing) {
				MultipleFacing face = (MultipleFacing) blockData;
				face.setFace(BlockFace.NORTH, true);
				face.setFace(BlockFace.SOUTH, true);
				blockData = face;
			}
		} else {
			if (blockData instanceof MultipleFacing) {
				MultipleFacing face = (MultipleFacing) blockData;
				face.setFace(BlockFace.EAST, true);
				face.setFace(BlockFace.WEST, true);
				blockData = face;
			}
		}
		
		return blockData;
	}
	
}
