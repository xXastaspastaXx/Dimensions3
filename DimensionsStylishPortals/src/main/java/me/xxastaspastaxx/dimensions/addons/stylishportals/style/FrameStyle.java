package me.xxastaspastaxx.dimensions.addons.stylishportals.style;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class FrameStyle {
	
	//roof, left, right, floor
	SidePattern[] patterns = new SidePattern[4];
	
	public FrameStyle(List<String> frameStyle) {
		
		/*
		WAXED_CUT_COPPER_SLAB_DOWN|WAXED_CUT_COPPER|WAXED_CUT_COPPER_SLAB_DOWN
		WAXED_CUT_COPPER_STAIRS_DOWN_RIGHT|WAXED_CUT_COPPER_STAIRS_UP_RIGHT,WAXED_CUT_COPPER_STAIRS_DOWN_RIGHT
		WAXED_CUT_COPPER_STAIRS_DOWN_LEFT|WAXED_CUT_COPPER_STAIRS_UP_RIGHT,WAXED_CUT_COPPER_STAIRS_DOWN_LEFT
		WAXED_CUT_COPPER_SLAB_UP|WAXED_CUT_COPPER|WAXED_CUT_COPPER_SLAB_UP
		WAXED_CUT_COPPER_SLAB_DOWN=WAXED_CUT_COPPER_SLAB[type=bottom]
		WAXED_CUT_COPPER_SLAB_UP=WAXED_CUT_COPPER_SLAB[type=top]
		WAXED_CUT_COPPER_STAIRS_DOWN_RIGHT=
		 * */

		for (int i = 0;i<4;i++) {
			patterns[i] = new SidePattern(frameStyle.get(i));
		}
		
	}
	
	public SidePattern[] getPatterns() {
		return patterns;
	}
	
	public boolean isPortalBlock(Block block) {
		for (SidePattern pattern : patterns) {
			if (pattern.isPortalBlock(block)) return true;
		}
		return false;
	}

	public boolean isPortal(World world, Vector min, Vector max, boolean zAxis) {
		
		int portalWidth = (int) (!zAxis?max.getX()-min.getX():max.getZ()-min.getZ());
		int portalHeight = (int) (max.getY()-min.getY());
		
		ArrayList<Block> top = new ArrayList<Block>();
		for (int x = 0;x<=portalWidth;x++) {
			top.add(new Location(world, !zAxis?min.getX()+x:min.getX(),max.getY(),!zAxis?min.getZ():min.getZ()+x).getBlock());
		}
		if (!patterns[0].isPattern(top.toArray(new Block[0]))) return false;
		
		ArrayList<Block> bottom = new ArrayList<Block>();
		for (int x = 0;x<=portalWidth;x++) {
			bottom.add(new Location(world, !zAxis?min.getX()+x:min.getX(),min.getY(),!zAxis?min.getZ():min.getZ()+x).getBlock());
		}
		if (!patterns[3].isPattern(bottom.toArray(new Block[0]))) return false;

		
		ArrayList<Block> side1 = new ArrayList<Block>();
		ArrayList<Block> side2 = new ArrayList<Block>();
		for (int y = 1;y<portalHeight;y++) {
			side1.add(new Location(world, !zAxis?min.getX():min.getX(),min.getY()+y,!zAxis?min.getZ():min.getZ()).getBlock());
			side2.add(new Location(world, !zAxis?max.getX():max.getX(),min.getY()+y,!zAxis?max.getZ():max.getZ()).getBlock());
		}

		return (patterns[1].isPattern(side1.toArray(new Block[0])) && patterns[2].isPattern(side2.toArray(new Block[0]))) || (patterns[1].isPattern(side2.toArray(new Block[0])) && patterns[2].isPattern(side1.toArray(new Block[0])));

	}
	
	public void setPortal(Location newLocation, World world, Vector min, Vector max, boolean zAxis) {
		
		int portalWidth = (int) (!zAxis?max.getX()-min.getX():max.getZ()-min.getZ());
		int portalHeight = (int) (max.getY()-min.getY());
		
		ArrayList<Block> top = new ArrayList<Block>();
		for (int x = 0;x<=portalWidth;x++) {
			top.add(new Location(world, !zAxis?newLocation.getX()+x:newLocation.getX(),newLocation.getY()+portalHeight,!zAxis?newLocation.getZ():newLocation.getZ()+x).getBlock());
		}

		ArrayList<Block> bottom = new ArrayList<Block>();
		ArrayList<Block> bottom1 = new ArrayList<Block>();
		ArrayList<Block> bottom2 = new ArrayList<Block>();
		for (int x = 0;x<=portalWidth;x++) {
			Block block = new Location(world, !zAxis?newLocation.getX()+x:newLocation.getX(),newLocation.getY(),!zAxis?newLocation.getZ():newLocation.getZ()+x).getBlock();
			bottom.add(block);
			
			if (!block.getRelative(BlockFace.DOWN).getType().isSolid()) {
				bottom1.add(block.getRelative(!zAxis?BlockFace.SOUTH:BlockFace.EAST));
				bottom2.add(block.getRelative(!zAxis?BlockFace.NORTH:BlockFace.WEST));
			}
		}

		ArrayList<Block> side1 = new ArrayList<Block>();
		ArrayList<Block> side2 = new ArrayList<Block>();
		for (int y = 1;y<portalHeight;y++) {
			side1.add(new Location(world, newLocation.getX(),newLocation.getY()+y,newLocation.getZ()).getBlock());
			side2.add(new Location(world, !zAxis?newLocation.getX()+portalWidth:newLocation.getX(),newLocation.getY()+y,!zAxis?newLocation.getZ():newLocation.getZ()+portalWidth).getBlock());
		}
		
		patterns[0].setPattern(top.toArray(new Block[0]), zAxis);
		patterns[1].setPattern(side2.toArray(new Block[0]), zAxis);
		patterns[2].setPattern(side1.toArray(new Block[0]), zAxis);
		patterns[3].setPattern(bottom.toArray(new Block[0]), zAxis);
		
		if (bottom1.isEmpty()) return;
		patterns[3].setPattern(bottom1.toArray(new Block[0]), zAxis);
		patterns[3].setPattern(bottom2.toArray(new Block[0]), zAxis);
		
	}

	
	
}
