package me.xxastaspastaxx.dimensions.addons.forcelink;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.commands.DimensionsCommand;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;

public class LinkPortalsCommand extends DimensionsCommand {
	
	DimensionsForceLink main;

	public LinkPortalsCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand, DimensionsForceLink main) {
		super(command,args,aliases,description, permission, adminCommand);
		this.main = main;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) return;
		
		if (args.length!=2) {
			sender.sendMessage("§7[§cDimensions§7] §cPlease use (while looking at a portal) §n/dim forceLink select§c to select a portal or §n/dim forceLink set§c to link with selected portal");
			return;
		}
		boolean select = args[1].equalsIgnoreCase("select");
		if (!select && !main.savedPortal.containsKey(sender)) {
			sender.sendMessage("§7[§cDimensions§7]§c You cannot link the portals because you have not selected one yet.");
			return;
		}
		
		CompletePortal compl = null;
		List<Block> los = ((Player) sender).getLineOfSight(null, 5);
		for (Block block : los) {
			if ((compl = Dimensions.getCompletePortalManager().getCompletePortal(block.getLocation(), false, false))!=null) break;
		}
	    if (compl==null) {
		    sender.sendMessage("§7[§cDimensions§7]§c No portal found");
	    	return;
	    }
		
		
	    
	    if (select) {
	    	main.savedPortal.put((Player) sender, compl);
		    sender.sendMessage("§7[§cDimensions§7]§c Selected portal!");
	    } else {
	    	CompletePortal selected = main.savedPortal.remove(sender);
	    	selected.setLinkedPortal(compl);
	    	compl.setLinkedPortal(selected);
	    	sender.sendMessage("§7[§cDimensions§7]§c Portals have been linked.");
	    }
	    
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		ArrayList<String> res = new ArrayList<String>();

		if (args.length!=2) return res;

		res.add("select");
		res.add("set");
		
		return res;
	}
}
