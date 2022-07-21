package me.xxastaspastaxx.dimensions.customportal;

import java.io.File;
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

public class CustomPortalLoader {
	
	private static final String DIRECTORY_PATH = "./plugins/Dimensions/Portals";
	private static final File PORTALS_DIRECTORY = new File(DIRECTORY_PATH);
	private static final String CONFIG_VERSION = "3.0.0";
	
	private static Class<?> blockClass;
	private static Class<?> craftBlockDataClass;
	private static Method getCombinedIdMethod;
	private static Method getStateMethod;
	
	public CustomPortalLoader() {
		try {
			blockClass = MinecraftReflection.getBlockClass();
			craftBlockDataClass = MinecraftReflection.getCraftBukkitClass("block.data.CraftBlockData");
			try {
				getCombinedIdMethod = blockClass.getMethod("i",MinecraftReflection.getIBlockDataClass());
			} catch (NoSuchMethodException e) {
				getCombinedIdMethod = blockClass.getMethod("getCombinedId",MinecraftReflection.getIBlockDataClass());
			}
			getStateMethod = craftBlockDataClass.getMethod("getState");
			
		} catch (NoSuchMethodException | IllegalArgumentException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public ArrayList<CustomPortal> loadAll() {
		ArrayList<CustomPortal> res = new ArrayList<CustomPortal>();
		
		File portalFolder = new File(DIRECTORY_PATH);
		if (!portalFolder.exists()) portalFolder.mkdir();
		
		PortalGeometry.instance = new PortalGeometry(null, null, null, null, false, null);
		
		for (File f : PORTALS_DIRECTORY.listFiles()) {
			String portalID = f.getName().replace(".yml", "");
			if (portalID.contains(" ")) continue;
			
			YamlConfiguration portalConfig = YamlConfiguration.loadConfiguration(f);	
			
			String fVersion = portalConfig.getString("configVersion", "pre3");
			if (!fVersion.equals(CONFIG_VERSION)) {
				//TODO
			}
			
			boolean enabled = portalConfig.getBoolean("Enable", false);
			String displayName = portalConfig.getString("DisplayName", "Unnamed");

			Material outsideMaterial = Material.matchMaterial(portalConfig.getString("Portal.Frame.Material", "COBBLESTONE"));
			AxisOrFace outsideBlockDir = new AxisOrFace(portalConfig.getString("Portal.Frame.Face", "all"));
			Material insideMaterial = Material.matchMaterial(portalConfig.getString("Portal.InsideMaterial", "NEHTER_PORTAL"));

//			BlockData[] insideBlockData = new BlockData[] {getInsideBlockData(false, tempBlockData),getInsideBlockData(true, tempBlockData)};
//			int[] combinedId = createCombinedID(insideBlockData, insideMaterial);
			
			Material lighterMaterial = Material.matchMaterial(portalConfig.getString("Portal.LighterMaterial", "FLINT_AND_STEEL"));
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
			
			String[] ratioString = portalConfig.getString("World.Ratio", "1:1").split(":");
			int ratio0 = Integer.parseInt(ratioString[0]);
			int ratio1 = Integer.parseInt(ratioString[1]);
			int ratio = ratio1/ratio0;

			boolean buildExitPortal = portalConfig.getBoolean("Options.BuildExitPortal", true);
			boolean spawnOnAir = portalConfig.getBoolean("Options.SpawnOnAir", false);
			
			List<String> disabledWorlds = portalConfig.getStringList("Options.DisabledWorlds");
			
			HashMap<EntityType,EntityType> entityTransformation = new HashMap<EntityType,EntityType>();
			for (String entity : portalConfig.getStringList("Entities.Transformation")) {
				String[] spl = entity.toUpperCase().split("->");
				entityTransformation.put(EntityType.valueOf(spl[0]), EntityType.valueOf(spl[1]));
			}
			
			String[] spawningDelayString = portalConfig.getString("Entities.Spawning.Delay", "5000-10000").split("-");
			int[] spawningDelay = {Integer.parseInt(spawningDelayString[0]),Integer.parseInt(spawningDelayString[1])};
			HashMap<EntityType,Integer> entitySpawning = new HashMap<EntityType,Integer>();
			for (String entity : portalConfig.getStringList("Entities.Spawning.List")) {
				String[] spl = entity.toUpperCase().split(";");
				entitySpawning.put(EntityType.valueOf(spl[0]), Integer.parseInt(spl[1]));
			}
			
			CustomPortal portal = new CustomPortal(portalID, displayName, enabled, outsideMaterial, outsideBlockDir, insideMaterial, lighterMaterial, particlesColor,breakEffect,minimumHeight,maximumHeight, maximumWidth, minimumWidth,
					worldName, ratio, buildExitPortal, spawnOnAir, disabledWorlds, entityTransformation, spawningDelay[0], spawningDelay[1], entitySpawning);
			portal.setInsideBlockData(insideMaterial.createBlockData());
			for (DimensionsAddon addon : Dimensions.getAddonManager().getAddons()) {
				addon.registerPortal(portalConfig, portal);
			}
			res.add(portal);
		}
		
		return res;
	}

	public static int[] createCombinedID(BlockData[] insideBlockData, Material insideMaterial) {
		int combinedId[] = new int[2];
		if (insideMaterial.isSolid() || insideMaterial==Material.NETHER_PORTAL) {
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
