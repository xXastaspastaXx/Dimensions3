package me.xxastaspastaxx.dimensions.addons.patreoncosmetics;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.xxastaspastaxx.dimensions.commands.DimensionsCommand;

public class PatreonDisableCommand extends DimensionsCommand {

	DimensionsPatreonCosmetics main;

	public PatreonDisableCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand, DimensionsPatreonCosmetics main) {
		super(command,args,aliases,description, permission, adminCommand);
		this.main = main;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) return;

		main.getUsers().remove(((Player) sender).getUniqueId());
		sender.sendMessage("§7[§cDimensions§7] §aSuccesfully disabled portal effects for the current session.");
	}
}
