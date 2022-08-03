package me.xxastaspastaxx.dimensions.addons.customlighter.commands;


import java.io.File;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.customlighter.DimensionsCustomLighter;
import me.xxastaspastaxx.dimensions.commands.DimensionsCommand;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;

public class CustomFrameCommand extends DimensionsCommand {

	DimensionsCustomLighter main;

	public CustomFrameCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand, DimensionsCustomLighter main) {
		super(command,args,aliases,description, permission, adminCommand);
		this.main = main;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) return;

		Player p = (Player) sender;


		try {
		CustomPortal portal = Dimensions.getCustomPortalManager().getCustomPortal(args[1]);
		if (portal==null) {
			p.sendMessage("§7[§cDimensions§7] No portal found.");
			return;
		}

		Block block = p.getTargetBlockExact(5);
		if  (block==null || block.getType()==Material.AIR) {
			p.sendMessage("§7[§cDimensions§7] Are you looking at a block?");
			return;
		}

		String data = block.getBlockData().getAsString();
	        
		main.getPortalConfig(portal).set("Addon.CustomLighter.FrameBlock", data);
		
		p.sendMessage("§7[§cDimensions§7] §aThe custom frame block data was succesfully updated, please reload Dimensions");

		try {
			main.getPortalConfig(portal).save(new File("plugins/Dimensions/Portals/"+portal.getPortalId()+".yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
