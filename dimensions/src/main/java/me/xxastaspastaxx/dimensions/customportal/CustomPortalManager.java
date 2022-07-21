package me.xxastaspastaxx.dimensions.customportal;

import java.util.ArrayList;

import me.xxastaspastaxx.dimensions.Dimensions;

public class CustomPortalManager {
	
	Dimensions pl;
	
	ArrayList<CustomPortal> customPortals = new ArrayList<CustomPortal>();
	
	public CustomPortalManager(Dimensions pl) {
		this.pl = pl;
		
		customPortals.addAll((new CustomPortalLoader()).loadAll());
		
		//customPortals.add(new CustomPortal("test", "", true, Material.DIAMOND_BLOCK.createBlockData(),
		//		Material.BLACK_STAINED_GLASS_PANE.createBlockData(), new ItemStack(Material.FLINT_AND_STEEL), Sound.BLOCK_GLASS_BREAK, (byte) 4,
		//		(byte) 15, (byte) 14, (byte) 3, "world_nether", (float) 1, (byte)  255, (byte) 1, true, false, new ArrayList<String>(), new HashMap<EntityType, EntityType>(),
		//		10000, 10001, new HashMap<EntityType, Byte>()));
	}



	public ArrayList<CustomPortal> getCustomPortals() {
		return customPortals;
	}



	public CustomPortal getCustomPortal(String name) {
		for (CustomPortal portal : customPortals) {
			if (portal.getPortalId().contentEquals(name)) return portal;
		}
		return null;
	}



	public void reload() {
		customPortals.clear();
		customPortals.addAll((new CustomPortalLoader()).loadAll());
	}
	
}
