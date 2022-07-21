package me.xxastaspastaxx.dimensions.addons.customlighter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.addons.customlighter.framemanager.FrameManager;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;

public class CustomPortalGeometry extends PortalGeometry {
	
	private FrameManager frameManager;
	
	CustomPortalGeometry(Vector min, Vector max, Vector insideMin, Vector insideMax,
			boolean zAxis, Vector center, FrameManager frameManager) {
		super(min, max, insideMin, insideMax, zAxis, center);
		this.frameManager = frameManager;
	}
	
	public PortalGeometry getPortal(CustomPortal customPortal, Location loc) {
		
		loc = loc.getBlock().getLocation();
		
		boolean zAxis = false;
		
		Vector min = new Vector();
		Vector max = new Vector();
		
		Location minLocation = loc.clone();
		Location maxLocation = loc.clone();
		
		for (int i = 0;i<customPortal.getMaximumHeight();i++) {
			if (!frameManager.isAccepted(minLocation.getBlock())) minLocation.add(0,-1,0);
			if (!frameManager.isAccepted(maxLocation.getBlock())) maxLocation.add(0,1,0);
		}
		if (!frameManager.isAccepted(minLocation.getBlock()) || !frameManager.isAccepted(maxLocation.getBlock())) return null;
		
		min.setY(minLocation.getY());
		max.setY(maxLocation.getY());
		
		minLocation = loc.clone();
		maxLocation = loc.clone();
		for (int i = 0;i<customPortal.getMaximumHeight();i++) {
			if (!frameManager.isAccepted(minLocation.getBlock())) minLocation.add(-1,0,0);
			if (!frameManager.isAccepted(maxLocation.getBlock())) maxLocation.add(1,0,0);
		}
		
		if (!frameManager.isAccepted(minLocation.getBlock()) || !frameManager.isAccepted(maxLocation.getBlock())) {
			
			minLocation = loc.clone();
			maxLocation = loc.clone();
			for (int i = 0;i<customPortal.getMaximumHeight();i++) {
				if (!frameManager.isAccepted(minLocation.getBlock())) minLocation.add(0,0,-1);
				if (!frameManager.isAccepted(maxLocation.getBlock())) maxLocation.add(0,0,1);
			}

			if (!frameManager.isAccepted(minLocation.getBlock()) || !frameManager.isAccepted(maxLocation.getBlock())) return null;
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
					if (!frameManager.isAccepted(block)) return null;
				} else if (!DimensionsUtils.isAir(block)) return null;
			}
		}
		
		
		return new CustomPortalGeometry(min, max, min.clone().subtract(new Vector(zAxis?0:-1,-1,zAxis?-1:0)), max.clone().subtract(new Vector(zAxis?0:1,1,zAxis?1:0)), zAxis, min.getMidpoint(max).add(new Vector(0.5,0.5,0.5)), frameManager);
	}
	
	@Override
	public void buildPortal(Location newLocation, World destinationWorld, CustomPortal customPortal) {
		
		/*Vector min = getMin();
		Vector max = getMax();
		
		for (double y=min.getY();y<=max.getY();y++) {
			for (double side=zAxis?min.getZ():min.getX();side<=(zAxis?max.getZ():max.getX());side++) {
				Block block = new Location(destinationWorld, zAxis?min.getX():side, y, !zAxis?min.getZ():side).getBlock();
				if ((y==min.getY() || y==max.getY()) || ((side==(zAxis?min.getZ():min.getX())) || side==(zAxis?max.getZ():max.getX()))) {
					System.out.println(block);
					frameManager.placeBlock(block);
					System.out.println(frameManager);
				} else {
					block.setType(Material.AIR);
				}
				
			}
		}*/

		boolean zAxis = iszAxis();
		double maxY = (newLocation.getY()+getPortalHeight());
		double maxSide = ((zAxis?newLocation.getZ():newLocation.getX())+getPortalWidth());
		
		for (double y=newLocation.getY();y<=maxY;y++) {
			for (double side=(zAxis?newLocation.getZ():newLocation.getX());side<=maxSide;side++) {
				Block block = new Location(destinationWorld, zAxis?newLocation.getX():side, y, !zAxis?newLocation.getZ():side).getBlock();
				if ((y==newLocation.getY() || y==maxY) || ((side==(zAxis?newLocation.getZ():newLocation.getX())) || side==maxSide)) {
					frameManager.placeBlock(block);
					//block.setBlockData(customPortal.getAxisOrFace().getNewData(customPortal.getOutsideMaterial().createBlockData()));
				} else {
					block.setType(Material.AIR);
				}
			}
		}
		
	}
	
}
