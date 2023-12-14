package me.xxastaspastaxx.dimensions.commands;

import org.bukkit.command.CommandSender;

import me.xxastaspastaxx.dimensions.Dimensions;

public class DisableDevCommand extends DimensionsCommand {
	
	public DisableDevCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand) {
		super(command,args,aliases,description, permission, adminCommand);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		Dimensions.getSubscriptionManager().stopDevelopmentMode();
		sender.sendMessage("�adone!");
	}
	
	
	
}
