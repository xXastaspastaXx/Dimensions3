package me.xxastaspastaxx.dimensions.addons.stylishportals.style;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class SidePattern {

	private ArrayList<StylePattern> patterns = new ArrayList<StylePattern>();
		
	public SidePattern(String stringPattern) {
		
		for (String str : stringPattern.split("\\|")) {
			ArrayList<BlockData> list = new ArrayList<BlockData>();
			for (String materialName : str.split("\\\\")) {
				list.add(Bukkit.getServer().createBlockData(materialName));
			}
			patterns.add(new StylePattern(list));
		}
	}
	
	public ArrayList<StylePattern> getPatterns() {
		return patterns;
	}

	public boolean isPortalBlock(Block block) {
		for (StylePattern pattern : patterns) {
			if (pattern.isPortalBlock(block)) return true;
		}
		return false;
	}

	public boolean isPortalBlock(Block block, int side, int max) {
		int i = 0;
		for (StylePattern pattern : patterns) {
			if (side>=i && side<=i+pattern.getSize()) {
				if (pattern.isPortalBlock(block)) return true;
			} else {
				i+= pattern.getSize();
				if (i>side) return false;
			}
		}
		
		return false;
	}

	public boolean isPortalBlock(Block[] blocks) {
		
		Iterator<StylePattern> patternsClone = new ArrayList<StylePattern>(patterns).iterator();
		
		while (patternsClone.hasNext()) {
			StylePattern pattern = patternsClone.next();
			
			if (!pattern.startsWith(blocks)) return false;
		}
		return true;
	}

	public boolean isPattern(Block[] blocks) {
		
		boolean isInside = patterns.get(0).startsWith(blocks);
		isInside = isInside && patterns.get(patterns.size()-1).endsWith(blocks);
		
		int max = blocks.length-patterns.get(patterns.size()-1).getSize();
		int blocksIndex = patterns.get(0).getSize();
		while (blocksIndex<max) {
			for (int i = Math.min(1, patterns.size()-1);i<Math.max(patterns.size()-1,Math.min(1, patterns.size()-1)+1);i++) {
				isInside = isInside && patterns.get(i).startsWith(blocksIndex,blocks);
				blocksIndex+=patterns.get(i).getSize();
			}
		}
		
		return isInside;
	}

	public void setPattern(Block[] blocks, boolean zAxis) {
		
		patterns.get(0).startWith(blocks, zAxis);
		patterns.get(patterns.size()-1).endWith(blocks, zAxis);
		
		int max = blocks.length-patterns.get(patterns.size()-1).getSize();
		int blocksIndex = patterns.get(0).getSize();
		while (blocksIndex<max) {
			for (int i = Math.min(1, patterns.size()-1);i<Math.max(patterns.size()-1,Math.min(1, patterns.size()-1)+1);i++) {
				patterns.get(i).startWith(blocksIndex,blocks,zAxis);
				blocksIndex+=patterns.get(i).getSize();
			}
		}
	}

	
}
