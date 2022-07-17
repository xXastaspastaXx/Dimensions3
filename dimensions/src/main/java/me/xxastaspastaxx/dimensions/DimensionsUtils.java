package me.xxastaspastaxx.dimensions;

import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;

public class DimensionsUtils {
	
	public static boolean isAir(Block block) {
		return block.getType()==Material.AIR ||block.getType()==Material.CAVE_AIR;
	}
	
	public static int getRandom(int min, int max) {
		return (int)(Math.random()*((max-min)+1))+min;
	}
	
	/*TODO REMOVE
	public static Object toObject( @SuppressWarnings("rawtypes") Class clazz, String value ) {
	    if( Boolean.class == clazz ) return Boolean.parseBoolean( value );
	    if( Byte.class == clazz ) return Byte.parseByte( value );
	    if( Short.class == clazz ) return Short.parseShort( value );
	    if( Integer.class == clazz ) return Integer.parseInt( value );
	    if( Long.class == clazz ) return Long.parseLong( value );
	    if( Float.class == clazz ) return Float.parseFloat( value );
	    if( Double.class == clazz ) return Double.parseDouble( value );
	    return value;
	}*/

	private static final Orientable netherPortalEffect = (Orientable) Material.NETHER_PORTAL.createBlockData();
	
	public static BlockData getNetherPortalEffect(boolean zAxis) {
		netherPortalEffect.setAxis(zAxis?Axis.Z:Axis.X);
		return netherPortalEffect;
	}
	
	public static Location parseLocationFromString(String str, String delim) {
		String[] spl = str.split(delim);
		return new Location(Bukkit.getWorld(spl[0]), Double.parseDouble(spl[1]), Double.parseDouble(spl[2]), Double.parseDouble(spl[3]));
	}
	
	public static String locationToString(Location loc, String delim) {
		return loc.getWorld().getName()+delim+loc.getX()+delim+loc.getY()+delim+loc.getZ();
	}
	
	public static boolean isInt(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e) {
			
		}
		return false;
	}

}
