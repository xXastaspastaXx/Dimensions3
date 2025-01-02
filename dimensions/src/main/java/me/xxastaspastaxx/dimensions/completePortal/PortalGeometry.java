package me.xxastaspastaxx.dimensions.completePortal;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;

/**
 * The class contains all the info about the portal structure
 *
 */

public class PortalGeometry {
	
	private static HashMap<CustomPortal, PortalGeometry> customGeometry = new HashMap<CustomPortal, PortalGeometry>();
	
	private Vector min;
	private Vector max;
	
	private Vector insideMin;
	private Vector insideMax;
	
	private Vector center;
	
	private byte portalWidth;
	private byte portalHeight;
	
	private boolean zAxis;

	private BoundingBox box;
	
	/**
	 * DO NOT USE
	 */
	public static PortalGeometry instance;
	
	protected PortalGeometry(Vector min, Vector max) {
		if (min==null) return;
		this.min = min;
		this.max = max;
		this.zAxis = min.getX()==max.getX();
		this.center = min.getMidpoint(max).add(new Vector(0.5,0.5,0.5));
		this.insideMin = min.clone().subtract(new Vector(zAxis?0:-1,-1,zAxis?-1:0));
		this.insideMax = max.clone().subtract(new Vector(zAxis?0:1,1,zAxis?1:0));
		this.portalWidth = (byte) (!zAxis?max.getX()-min.getX():max.getZ()-min.getZ());
		this.portalHeight = (byte) (max.getY()-min.getY());
		
		box = BoundingBox.of(min, max.clone().add(new Vector(zAxis?1:0,0,zAxis?0:1)));
	}

	/**
	 * Get the bottom corner of the portal
	 */
	public Vector getMin() {
		return min;
	}

	/**
	 * Get the top corner of the portal
	 */
	public Vector getMax() {
		return max;
	}

	/**
	 * Get the bottom corner of the portal (inside the portal)
	 */
	public Vector getInsideMin() {
		return insideMin;
	}

	/**
	 * Get the top corner of the portal (inside the portal)
	 */
	public Vector getInsideMax() {
		return insideMax;
	}

	/**
	 * Check if the portal is built along the Z Axis
	 */
	public boolean iszAxis() {
		return zAxis;
	}
	
	/**
	 * Get the center of the portal
	 */
	public Vector getCenter() {
		return center;
	}
	
	/**
	 * Get the width of the portal
	 */
	public byte getPortalWidth() {
		return portalWidth;
	}
	
	/**
	 * Get the height of the portal including fixed height for exit portal
	 * @param customPortal the customPortal to get the fixed height
	 */
	public byte getPortalWidth(CustomPortal customPortal) {
		return (byte) (customPortal.getFixedExitPortalWidth()==-1?portalWidth:customPortal.getFixedExitPortalWidth());
	}
	
	/**
	 * Get the height of the portal
	 */
	public byte getPortalHeight() {
		return portalHeight;
	}
	
	/**
	 * Get the height of the portal including fixed height for exit portal
	 * @param customPortal the customPortal to get the fixed height
	 */
	public byte getPortalHeight(CustomPortal customPortal) {
		return (byte) (customPortal.getFixedExitPortalHeight()==-1?portalHeight:customPortal.getFixedExitPortalHeight());
	}
	
	/**
	 * Override the instance of the PortalGeometry
	 * @param portal the portal to override the geometry for
	 * @param geom the new instance of the portal geometry
	 */
	public static void setCustomGeometry(CustomPortal portal, PortalGeometry geom) {
		customGeometry.put(portal, geom);
	}
	
	/**
	 * Get the PortalGeometry instance for the portal
	 */
	public static PortalGeometry getPortalGeometry(CustomPortal portal) {
		if (customGeometry.containsKey(portal)) {
			return customGeometry.get(portal);
		 } else {
			 return instance;
		 }
	}
	
	/**
	 * Create PortalGeometry for a portal
	 * @param min the bottom corner of the portal
	 * @param max the top corner of the portal
	 */
	public PortalGeometry createGeometry(Vector min, Vector max) {
		return new PortalGeometry(min, max);
	}
	
	/**
	 * Check if there is a portal structure at the location
	 */
	public PortalGeometry getPortal(CustomPortal customPortal, Location loc) {
		PortalGeometry geom = getPortal(customPortal, loc, false);
		return geom!=null?geom:getPortal(customPortal,loc,true);
	}
	
	/**
	 * Check if there is a portal structure at the location
	 */
	public PortalGeometry getPortal(CustomPortal customPortal, Location loc, boolean zAxis) {
		 
		 loc = loc.getBlock().getLocation();
			
		Vector min = new Vector();
		Vector max = new Vector();
			
		Location minLocation = loc.clone();
		Location maxLocation = loc.clone();
			
		for (int i = 0;i<customPortal.getMaximumHeight();i++) {
			if (!customPortal.isPortalBlock(minLocation.getBlock())) minLocation.add(0,-1,0);
			if (!customPortal.isPortalBlock(maxLocation.getBlock())) maxLocation.add(0,1,0);
		}
		if (!customPortal.isPortalBlock(minLocation.getBlock()) || !customPortal.isPortalBlock(maxLocation.getBlock())) return null;
		
		min.setY(minLocation.getY());
		max.setY(maxLocation.getY());
		
		if (!zAxis) {
			minLocation = loc.clone();
			maxLocation = loc.clone();
			for (int i = 0;i<customPortal.getMaximumHeight();i++) {
				if (!customPortal.isPortalBlock(minLocation.getBlock())) minLocation.add(-1,0,0);
				if (!customPortal.isPortalBlock(maxLocation.getBlock())) maxLocation.add(1,0,0);
			}
		} else {
			minLocation = loc.clone();
			maxLocation = loc.clone();
			for (int i = 0;i<customPortal.getMaximumHeight();i++) {
				if (!customPortal.isPortalBlock(minLocation.getBlock())) minLocation.add(0,0,-1);
				if (!customPortal.isPortalBlock(maxLocation.getBlock())) maxLocation.add(0,0,1);
			}
		}


		if (!customPortal.isPortalBlock(minLocation.getBlock()) || !customPortal.isPortalBlock(maxLocation.getBlock())) return null;
		
		min.setX(minLocation.getX());
		min.setZ(minLocation.getZ());
		
		max.setX(maxLocation.getX());
		max.setZ(maxLocation.getZ());
		
		if (max.getY()-min.getY()>customPortal.getMaximumHeight()-1 || max.getY()-min.getY()<customPortal.getMinimumHeight()-1) return null;
		if (!zAxis && (max.getX()-min.getX()>customPortal.getMaximumWidth()-1 || max.getX()-min.getX()<customPortal.getMinimumWidth()-1)) return null;
		if (zAxis && (max.getZ()-min.getZ()>customPortal.getMaximumWidth()-1 || max.getZ()-min.getZ()<customPortal.getMinimumWidth()-1)) return null;
		
		World world = loc.getWorld();
		for (double y=min.getY();y<=max.getY();y++) {
			for (double side=zAxis?min.getZ():min.getX();side<=(zAxis?max.getZ():max.getX());side++) {
				if ((y==min.getY() || y==max.getY()) && ((side==(zAxis?min.getZ():min.getX())) || (side==(zAxis?max.getZ():max.getX())))) continue; //skip corner check
				Block block = new Location(world, zAxis?min.getX():side, y, !zAxis?min.getZ():side).getBlock();
				if ((y==min.getY() || y==max.getY()) || ((side==(zAxis?min.getZ():min.getX())) || (side==(zAxis?max.getZ():max.getX())))) {
					if (!customPortal.isPortalBlock(block)) return null;
				} else if (!DimensionsUtils.isAir(block)) return null;
			}
		}
		
		
		return new PortalGeometry(min, max);		 
	}

	/**
	 * Check if the location is inside the portal
	 * @param location the location
	 * @param outside true to include the frame of the portal
	 * @param corner true to include the corners of the portal
	 */
	public boolean isInside(Location location, boolean outside, boolean corner) {
		//TODO remove?
		location = location.getBlock().getLocation();
		
		Vector min = outside?this.min:this.insideMin;
		Vector max = outside?this.max:this.insideMax;
		
		if (outside && !corner) {
			
			if (min.getY()==location.getY() || max.getY()==location.getY()) {
				if (zAxis && (min.getZ()==location.getZ() || max.getZ()==location.getZ())) return false;
				if (!zAxis && (min.getX()==location.getX() || max.getX()==location.getX())) return false;
			}
			
		}
		
		if (min.getY()<=location.getY() && max.getY()>=location.getY()) {
			if (zAxis) {
				if (min.getZ()<=location.getZ() && max.getZ()>=location.getZ() && min.getBlockX()==location.getBlockX()) {
					return true;
				}
			} else {
				if (min.getX()<=location.getX() && max.getX()>=location.getX() && min.getBlockZ()==location.getBlockZ()) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Build a portal
	 * @param newLocation the location to build the portal
	 * @param destinationWorld the world to build the portal at
	 * @param customPortal
	 */
	public void buildPortal(Location newLocation, World destinationWorld, CustomPortal customPortal) {
		
		newLocation = newLocation.getBlock().getLocation();
		
		double maxY = (newLocation.getY()+getPortalHeight(customPortal));
		double maxSide = ((zAxis?newLocation.getZ():newLocation.getX())+getPortalWidth(customPortal));
		
		for (double y=newLocation.getY();y<=maxY;y++) {
			for (double side=(zAxis?newLocation.getZ():newLocation.getX());side<=maxSide;side++) {
				Block block = new Location(destinationWorld, zAxis?newLocation.getX():side, y, !zAxis?newLocation.getZ():side).getBlock();
				if ((y==newLocation.getY() || y==maxY) || ((side==(zAxis?newLocation.getZ():newLocation.getX())) || side==maxSide)) {
					block.setBlockData(customPortal.getAxisOrFace().getNewData(customPortal.getOutsideMaterial().createBlockData()));
					
					//build platform
					if (y==newLocation.getY() && !((side==(zAxis?newLocation.getZ():newLocation.getX())) || side==maxSide) && !block.getRelative(BlockFace.DOWN).getType().isSolid()) {
						block.getRelative(!zAxis?BlockFace.NORTH:BlockFace.WEST).setBlockData(customPortal.getAxisOrFace().getNewData(customPortal.getOutsideMaterial().createBlockData()));
						block.getRelative(!zAxis?BlockFace.SOUTH:BlockFace.EAST).setBlockData(customPortal.getAxisOrFace().getNewData(customPortal.getOutsideMaterial().createBlockData()));
					}
				} else {
					block.setType(Material.AIR);
				}
			}
		}
		
	}

	public static PortalGeometry nullGeometry() {
		return new PortalGeometry(null, null);
	}

	public BoundingBox getBoundingBox() {
		return box;
//		if (zAxis) {
//			return box.clone().expand(new Vector(0,1,-1));
//		} else {
//			return box.clone().expand(new Vector(1,1,0));
//		}
	}
}
