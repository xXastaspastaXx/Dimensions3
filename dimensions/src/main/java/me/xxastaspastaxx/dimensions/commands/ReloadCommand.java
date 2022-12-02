package me.xxastaspastaxx.dimensions.commands;

import org.bukkit.command.CommandSender;

import me.xxastaspastaxx.dimensions.Dimensions;

public class ReloadCommand extends DimensionsCommand {
	
	public ReloadCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand) {
		super(command,args,aliases,description, permission, adminCommand);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {

		try {
			Dimensions.getInstance().reload();
			sender.sendMessage("§7[§cDimensions§7]§a Reload complete");
		} catch (Exception e) {
			sender.sendMessage("§7[§cDimensions§7]§c There was a problem while trying to reload Dimensions. Please check console for more information");
			e.printStackTrace();
		}
	}
	
}
