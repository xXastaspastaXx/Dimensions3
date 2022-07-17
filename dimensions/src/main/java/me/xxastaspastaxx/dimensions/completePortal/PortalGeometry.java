package me.xxastaspastaxx.dimensions.completePortal;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;

public class PortalGeometry {
	
	private Vector min;
	private Vector max;
	
	private Vector insideMin;
	private Vector insideMax;
	
	private Vector center;
	
	private byte portalWidth;
	private byte portalHeight;
	
	private boolean zAxis;

	protected PortalGeometry(Vector min, Vector max, Vector insideMin, Vector insideMax,
			boolean zAxis, Vector center) {
		this.min = min;
		this.max = max;
		this.insideMin = insideMin;
		this.insideMax = insideMax;
		this.portalWidth = (byte) (!zAxis?max.getX()-min.getX():max.getZ()-min.getZ());
		this.portalHeight = (byte) (max.getY()-min.getY());
		this.center = center;
		this.zAxis = zAxis;
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

	public static PortalGeometry getPortal(CustomPortal customPortal, Location loc) {
		
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
		
		
		return new PortalGeometry(min, max, min.clone().subtract(new Vector(zAxis?0:-1,-1,zAxis?-1:0)), max.clone().subtract(new Vector(zAxis?0:1,1,zAxis?1:0)), zAxis, min.getMidpoint(max));
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
	
}
