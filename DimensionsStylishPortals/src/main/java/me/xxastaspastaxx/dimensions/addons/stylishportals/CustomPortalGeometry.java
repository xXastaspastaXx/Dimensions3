package me.xxastaspastaxx.dimensions.addons.stylishportals;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.addons.stylishportals.style.FrameStyle;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;

public class CustomPortalGeometry extends PortalGeometry {
	FrameStyle frameManager;
	
	CustomPortalGeometry(Vector min, Vector max, Vector insideMin, Vector insideMax,
			boolean zAxis, Vector center, FrameStyle frameManager) {
		super(min, max, insideMin, insideMax, zAxis, center);
		this.frameManager = frameManager;
	}
	
	public CustomPortalGeometry getPortal(CustomPortal customPortal, Location loc) {
		
		loc = loc.getBlock().getLocation();
		
		boolean zAxis = false;
		
		Vector min = new Vector();
		Vector max = new Vector();
		
		Location minLocation = loc.clone();
		Location maxLocation = loc.clone();
		
		for (int i = 0;i<customPortal.getMaximumHeight();i++) {
			if (!frameManager.isPortalBlock(minLocation.getBlock())) minLocation.add(0,-1,0);
			if (!frameManager.isPortalBlock(maxLocation.getBlock())) maxLocation.add(0,1,0);
		}
		if (!frameManager.isPortalBlock(minLocation.getBlock()) || !frameManager.isPortalBlock(maxLocation.getBlock())) return null;
		
		min.setY(minLocation.getY());
		max.setY(maxLocation.getY());
		
		minLocation = loc.clone();
		maxLocation = loc.clone();
		for (int i = 0;i<customPortal.getMaximumHeight();i++) {
			if (!frameManager.isPortalBlock(minLocation.getBlock())) minLocation.add(-1,0,0);
			if (!frameManager.isPortalBlock(maxLocation.getBlock())) maxLocation.add(1,0,0);
		}
		
		if (!frameManager.isPortalBlock(minLocation.getBlock()) || !frameManager.isPortalBlock(maxLocation.getBlock())) {
			
			minLocation = loc.clone();
			maxLocation = loc.clone();
			for (int i = 0;i<customPortal.getMaximumHeight();i++) {
				if (!frameManager.isPortalBlock(minLocation.getBlock())) minLocation.add(0,0,-1);
				if (!frameManager.isPortalBlock(maxLocation.getBlock())) maxLocation.add(0,0,1);
			}

			if (!frameManager.isPortalBlock(minLocation.getBlock()) || !frameManager.isPortalBlock(maxLocation.getBlock())) return null;
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
					if (!frameManager.isPortalBlock(block)) return null;
				} else if (!DimensionsUtils.isAir(block)) return null;
			}
		}
		if (!frameManager.isPortal(world, min,max, zAxis)) return null;
		
		
		return new CustomPortalGeometry(min, max, min.clone().subtract(new Vector(zAxis?0:-1,-1,zAxis?-1:0)), max.clone().subtract(new Vector(zAxis?0:1,1,zAxis?1:0)), zAxis, min.getMidpoint(max).add(new Vector(0.5,0.5,0.5)), frameManager);
	}
	
	@Override
	public void buildPortal(Location newLocation, World destinationWorld, CustomPortal customPortal) {
		
		frameManager.setPortal(newLocation, destinationWorld, getMin(), getMax(), iszAxis());

		
	}
	
}
