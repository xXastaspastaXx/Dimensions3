package me.xxastaspastaxx.dimensions.commands;

import org.bukkit.command.CommandSender;

import me.xxastaspastaxx.dimensions.Dimensions;

public class ValidateCommand extends DimensionsCommand {
	
	public ValidateCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand) {
		super(command,args,aliases,description, permission, adminCommand);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {

		sender.sendMessage("§7Click the following link to validate your subscription.");
		sender.sendMessage("§7When you complete the process run the command again and wait for the verification to complete (a message will be sent to let you know).");
		if (Dimensions.getSubscriptionManager().isFullAccess()) {
			sender.sendMessage("§aAlready validated");
		} else {
			sender.sendMessage("§7"+Dimensions.getSubscriptionManager().getValidateSubscriptionURL());
			Dimensions.getSubscriptionManager().validateSubscriptionOnlineAsync(sender);
			sender.sendMessage("§7After validating, you will need to restart your server in order for addons to load properly");
		}
	}
}
