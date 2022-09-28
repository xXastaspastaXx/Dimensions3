package me.xxastaspastaxx.dimensions.commands;

import org.bukkit.command.CommandSender;

import me.xxastaspastaxx.dimensions.Dimensions;

public class InfoCommand extends DimensionsCommand {
	
	public InfoCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand) {
		super(command,args,aliases,description, permission, adminCommand);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		sender.sendMessage("§7[§cDimensions§7] Version "+ Dimensions.getInstance().getDescription().getVersion());
	}
	
	
	
	
}
