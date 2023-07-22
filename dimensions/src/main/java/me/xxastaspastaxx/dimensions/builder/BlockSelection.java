package me.xxastaspastaxx.dimensions.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class BlockSelection {

	public ItemStack item;
	
	private Block one;
	private Block two;
	
	public BlockSelection(ItemStack item) {
		this.item = item;
	}
	
	public void updateItem(ItemStack held) {
		ItemMeta meta = item.getItemMeta();
		meta.setLore(new ArrayList<String>(Arrays.asList(new String[] {"§7Selection 1: "+(one==null?"none":one.getX()+", "+one.getY()+", "+one.getZ()),
																		"§7Selection 2: "+(two==null?"none":two.getX()+", "+two.getY()+", "+two.getZ())})));
		if (held!=null) held.setItemMeta(meta);
		item.setItemMeta(meta);
	}

	public Block getOne() {
		return one;
	}

	public void setOne(Block one) {
		this.one = one;
	}

	public Block getTwo() {
		return two;
	}

	public void setTwo(Block two) {
		this.two = two;
	}

	public HashMap<String, ArrayList<String>> getBlocks(boolean keepBlockData, String inventoryName) {
		HashMap<String, ArrayList<String>> res = new HashMap<String, ArrayList<String>>();
		BoundingBox box = BoundingBox.of(one, two);
		
		Map<String, Object> invMap = new HashMap<String, Object>();
		invMap.put("name", inventoryName);
		invMap.put("counter", 0);
		
		Vector center = new Vector(box.getCenter().getBlockX(),box.getCenter().getBlockY(),box.getCenter().getBlockZ());
		
		for (int i = box.getMin().getBlockX(); i < box.getMax().getBlockX();i++) {
		  for (int j = box.getMin().getBlockY(); j < box.getMax().getBlockY(); j++) {
		    for (int k = box.getMin().getBlockZ(); k < box.getMax().getBlockZ();k++) {
		    	Block block = one.getWorld().getBlockAt(i, j, k);
		    	Vector diff = diff(center,block.getLocation().toVector());
		    	
//		    	String stringData = BuildableMobsBlockData.toString(block, invMap, keepBlockData);
		    	
//		    	if (!res.containsKey(stringData)) res.put(stringData, new ArrayList<String>());
//		    	res.get(stringData).add(diff.getBlockX()+","+diff.getBlockY()+","+ diff.getBlockZ());
		    	
		    }
		  }
		}
		
		return res;
	}

	private Vector diff(Vector center, Vector blockVector) {

		double x = blockVector.getX()-center.getX();
		double y = blockVector.getY()-center.getY();
		double z = blockVector.getZ()-center.getZ();
		
		return new Vector(x,y,z);
	}

	public boolean isValid() {
		return one!=null && two!=null && one.getWorld().equals(two.getWorld());
	}

	public Block getCenter() {
		try {
			return BoundingBox.of(one, two).getCenter().toLocation(one.getWorld()).getBlock();
		} catch (NullPointerException | IllegalArgumentException e) {
			return null;
		}
	}
	
}