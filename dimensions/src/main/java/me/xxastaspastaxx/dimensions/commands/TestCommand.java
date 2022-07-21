package me.xxastaspastaxx.dimensions.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

public class TestCommand extends DimensionsCommand {
	
	public TestCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand) {
		super(command,args,aliases,description, permission, adminCommand);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		//Location loc = ((Entity) sender).getLocation();
		//spiralSearch(loc, 32);
	}
	
	public Location spiralSearch(Location centerBlock, int radius) {
		int bX = centerBlock.getBlockX();
		int bY = centerBlock.getBlockY();
		int bZ = centerBlock.getBlockZ();
		
		for (int x = bX - radius; x <= bX + radius; x++) {
			for (int y = bY - radius; y <= bY + radius; y++) {
				for (int z = bZ - radius; z <= bZ + radius; z++) {
					double distance = ((bX - x) * (bX - x) + ((bZ - z) * (bZ - z)) + ((bY - y) * (bY - y)));
					
					if (distance < radius * radius) {
						Location l = new Location(centerBlock.getWorld(),x,y,z);
						l.getBlock().setType(Material.GLASS);
					}
				}
			}
		}
		return null;
	}
	
	
	
	
}
