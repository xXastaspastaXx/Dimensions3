package me.xxastaspastaxx.dimensions.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.settings.DimensionsSettings;

public class InfoCommand extends DimensionsCommand {
	
	public InfoCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand) {
		super(command,args,aliases,description, permission, adminCommand);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (DimensionsSettings.showPortalsToPlayers) {
			Dimensions.getCreatePortalManager().handle((Player) sender);
		} else {
			sender.sendMessage("§7[§cDimensions§7] Version "+ Dimensions.getInstance().getDescription().getVersion());
		}
	}
	
	
	
	
}
