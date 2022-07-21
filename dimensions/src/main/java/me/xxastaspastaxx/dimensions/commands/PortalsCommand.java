package me.xxastaspastaxx.dimensions.commands;

import org.bukkit.command.CommandSender;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;

public class PortalsCommand extends DimensionsCommand {
	
	public PortalsCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand) {
		super(command,args,aliases,description, permission, adminCommand);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {

		String msg = "§7[§cDimensions§7] Portals list:";
		for (CustomPortal portal : Dimensions.getCustomPortalManager().getCustomPortals()) {
			msg +="\n["+(portal.isEnabled()?"§aEnabled":"§cDisabled")+"§7] "+ portal.getPortalId();
		}
		
		sender.sendMessage(msg);
	}
	
}
