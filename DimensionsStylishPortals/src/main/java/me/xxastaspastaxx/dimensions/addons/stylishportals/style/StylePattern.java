package me.xxastaspastaxx.dimensions.addons.stylishportals.style;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class StylePattern {
	
	private List<BlockData> blocks = new ArrayList<BlockData>();

	public StylePattern(ArrayList<BlockData> list) {
		this.blocks = list;
	}

	public List<BlockData> getBlocks() {
		return blocks;
	}

	public boolean isPortalBlock(Block block) {
		for (BlockData blockData : blocks) {
			if (matches(blockData,block.getBlockData())) return true;
		}
		return false;
	}

	private boolean matches(BlockData blockData, BlockData blockData2) {
		if (blockData.getMaterial()!=blockData2.getMaterial()) return false;
		return blockData.matches(blockData2) || blockData.matches(Bukkit.getServer().createBlockData(blockData2.getAsString().replace("facing=north", "facing=TOWEST").replace("facing=south", "facing=TOEAST").replace("facing=west", "facing=north").replace("facing=east", "facing=south").replace("facing=TOWEST", "facing=west").replace("facing=TOEAST", "facing=east")));		
		
	}

	public int getSize() {
		return blocks.size();
	}

	public boolean startsWith(int offset, Block[] blocks2) {
		for (int i =0;i<blocks.size();i++) {
			if (!matches(blocks.get(i),blocks2[offset+i].getBlockData())) return false;
		}
		return true;
	}
	
	public boolean startsWith(Block[] blocks2) {
		for (int i =0;i<blocks.size();i++) {
			if (!matches(blocks.get(i),blocks2[i].getBlockData())) return false;
		}
		return true;
	}
	
	public boolean endsWith(Block[] blocks2) {
		for (int i =0;i<blocks.size();i++) {
			if (!matches(blocks.get(i),blocks2[blocks2.length-blocks.size()+i].getBlockData())) return false;
		}
		return true;
	}
	
	public void startWith(int offset, Block[] blocks2, boolean zAxis) {
		for (int i =0;i<blocks.size();i++) {
			setBlock(blocks2[offset+i], blocks.get(i), zAxis);
		}
	}
	
	public void startWith(Block[] blocks2, boolean zAxis) {
		for (int i =0;i<blocks.size();i++) {
			setBlock(blocks2[i], blocks.get(i), zAxis);
		}
	}
	
	private void setBlock(Block block, BlockData blockData2, boolean zAxis) {
		block.setType(blockData2.getMaterial());
		if (zAxis) {
			block.setBlockData(Bukkit.getServer().createBlockData(blockData2.getAsString().replace("facing=west", "facing=north").replace("facing=east", "facing=south")));
		} else {
			block.setBlockData(blockData2);
		}
		
	}

	public void endWith(Block[] blocks2, boolean zAxis) {
		for (int i =0;i<blocks.size();i++) {
			setBlock(blocks2[blocks2.length-blocks.size()+i], blocks.get(i), zAxis);
		}
	}
	
	
}
