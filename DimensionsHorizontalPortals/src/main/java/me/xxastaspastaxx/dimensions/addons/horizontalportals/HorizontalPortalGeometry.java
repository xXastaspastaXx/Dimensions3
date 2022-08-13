package me.xxastaspastaxx.dimensions.addons.horizontalportals;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.completePortal.PortalGeometry;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;

public class HorizontalPortalGeometry extends PortalGeometry {
	
	protected HorizontalPortalGeometry(Vector min, Vector max) {
		super(min, max);
		
		if (min==null) return;
		getInsideMin().copy(min).subtract(new Vector(-1,0,-1));
		getInsideMax().copy(max).subtract(new Vector(1,0,1));
		
		getBoundingBox().copy(BoundingBox.of(getInsideMin(), getInsideMax()));
	}
	
	public PortalGeometry createGeometry(Vector min, Vector max) {
		
		return new HorizontalPortalGeometry(min, max);
	}
	
	public PortalGeometry getPortal(CustomPortal customPortal, Location loc) {
		 
		if (((String) DimensionsAddon.getOption(customPortal, "horizontalPortal")).equals("both")) {
			PortalGeometry temp = super.getPortal(customPortal, loc);
			if (temp!=null) return temp;
		}
		
		loc = loc.getBlock().getLocation();
			
		Vector min = new Vector();
		Vector max = new Vector();
			
		Location minLocation = loc.clone();
		Location maxLocation = loc.clone();
		
		for (int i = 0;i<customPortal.getMaximumHeight();i++) {
			if (!customPortal.isPortalBlock(minLocation.getBlock())) minLocation.add(-1,0,0);
			if (!customPortal.isPortalBlock(maxLocation.getBlock())) maxLocation.add(1,0,0);
		}
		
		if (!customPortal.isPortalBlock(minLocation.getBlock()) || !customPortal.isPortalBlock(maxLocation.getBlock())) return null;
		min.setX(minLocation.getX());	
		max.setX(maxLocation.getX());
		
		minLocation = loc.clone();
		maxLocation = loc.clone();
		for (int i = 0;i<customPortal.getMaximumHeight();i++) {
			if (!customPortal.isPortalBlock(minLocation.getBlock())) minLocation.add(0,0,-1);
			if (!customPortal.isPortalBlock(maxLocation.getBlock())) maxLocation.add(0,0,1);
		}
		min.setZ(minLocation.getZ());
		max.setZ(maxLocation.getZ());

		if (!customPortal.isPortalBlock(minLocation.getBlock()) || !customPortal.isPortalBlock(maxLocation.getBlock())) return null;

		min.setY(loc.getY());
		max.setY(loc.getY());
		
		if (max.getX()-min.getX()>customPortal.getMaximumWidth()-1 || max.getX()-min.getX()<customPortal.getMinimumWidth()-1) return null;
		if (max.getZ()-min.getZ()>customPortal.getMaximumWidth()-1 || max.getZ()-min.getZ()<customPortal.getMinimumWidth()-1) return null;
		
		World world = loc.getWorld();
		for (double x=min.getX();x<=max.getX();x++) {
			for (double z=min.getZ();z<=max.getZ();z++) {
				if ((x==min.getX() || x==max.getX()) && (z==min.getZ() || z==max.getZ())) continue; //skip corner check
				Block block = new Location(world, x, min.getY(), z).getBlock();
				if ((x==min.getX() || x==max.getX()) || (z==min.getZ() || z==max.getZ())) {
					if (!customPortal.isPortalBlock(block)) return null;
				} else if (!DimensionsUtils.isAir(block)) return null;
			}
		}
		return new HorizontalPortalGeometry(min, max);		 
	}

	public boolean isInside(Location location, boolean outside, boolean corner) {
		//TODO remove?
		location = location.getBlock().getLocation();
		
		Vector min = outside?getMin():getInsideMin();
		Vector max = outside?getMax():getInsideMax();
		
		if (outside && !corner) {
			
			if (min.getX()==location.getX() || max.getX()==location.getX()) {
				if ((min.getZ()==location.getZ() || max.getZ()==location.getZ())) return false;
			}
			
		}
		
		if (min.getX()<=location.getX() && max.getX()>=location.getX()) {
			if (min.getZ()<=location.getZ() && max.getZ()>=location.getZ() && min.getBlockY()==location.getBlockY()) {
				return true;
			}
		}

		return false;
	}
	
	@Override
	public void buildPortal(Location newLocation, World destinationWorld, CustomPortal customPortal) {
		
		double maxX = (newLocation.getX()+getPortalWidth());
		double maxZ = (newLocation.getZ()+getPortalWidth());
		
		for (double x=newLocation.getX();x<=maxX;x++) {
			for (double z=newLocation.getZ();z<=maxZ;z++) {
				Block block = new Location(destinationWorld, x, newLocation.getY(), z).getBlock();
				if ((x==newLocation.getX() || x==maxX) || (z==newLocation.getZ() || z==maxZ)) {
					block.setBlockData(customPortal.getAxisOrFace().getNewData(customPortal.getOutsideMaterial().createBlockData()));
					
				} else {
					block.setType(Material.AIR);
					
					//build platform
					if (!block.getRelative(BlockFace.DOWN).getType().isSolid()) {
						block.getRelative(BlockFace.DOWN).setBlockData(customPortal.getAxisOrFace().getNewData(customPortal.getOutsideMaterial().createBlockData()));
					}
				}
			}
		}
		
	}
}
