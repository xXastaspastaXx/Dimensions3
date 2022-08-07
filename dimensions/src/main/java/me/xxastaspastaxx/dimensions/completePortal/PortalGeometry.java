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
		
		box = BoundingBox.of(insideMin, insideMax);
	}

	public Vector getMin() {
		return min;
	}

	public Vector getMax() {
		return max;
	}

	public Vector getInsideMin() {
		return insideMin;
	}

	public Vector getInsideMax() {
		return insideMax;
	}

	public boolean iszAxis() {
		return zAxis;
	}
	
	public Vector getCenter() {
		return center;
	}
	
	public byte getPortalWidth() {
		return portalWidth;
	}

	public byte getPortalHeight() {
		return portalHeight;
	}
	
	public static void setCustomGeometry(CustomPortal portal, PortalGeometry geom) {
		customGeometry.put(portal, geom);
	}
	
	public static PortalGeometry getPortalGeometry(CustomPortal portal) {
		if (customGeometry.containsKey(portal)) {
			return customGeometry.get(portal);
		 } else {
			 return instance;
		 }
	}
	
	public PortalGeometry createGeometry(Vector min, Vector max) {
		return new PortalGeometry(min, max);
	}
	
	public PortalGeometry getPortal(CustomPortal customPortal, Location loc) {
		 
		 loc = loc.getBlock().getLocation();
			
		boolean zAxis = false;
			
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
		
		minLocation = loc.clone();
		maxLocation = loc.clone();
		for (int i = 0;i<customPortal.getMaximumHeight();i++) {
			if (!customPortal.isPortalBlock(minLocation.getBlock())) minLocation.add(-1,0,0);
			if (!customPortal.isPortalBlock(maxLocation.getBlock())) maxLocation.add(1,0,0);
		}
		
		if (!customPortal.isPortalBlock(minLocation.getBlock()) || !customPortal.isPortalBlock(maxLocation.getBlock())) {
			
			minLocation = loc.clone();
			maxLocation = loc.clone();
			for (int i = 0;i<customPortal.getMaximumHeight();i++) {
				if (!customPortal.isPortalBlock(minLocation.getBlock())) minLocation.add(0,0,-1);
				if (!customPortal.isPortalBlock(maxLocation.getBlock())) maxLocation.add(0,0,1);
			}
				if (!customPortal.isPortalBlock(minLocation.getBlock()) || !customPortal.isPortalBlock(maxLocation.getBlock())) return null;
			zAxis = true;
		}
		
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

	public void buildPortal(Location newLocation, World destinationWorld, CustomPortal customPortal) {
		
		double maxY = (newLocation.getY()+portalHeight);
		double maxSide = ((zAxis?newLocation.getZ():newLocation.getX())+portalWidth);
		
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
	}
}
