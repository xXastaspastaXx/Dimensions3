package me.xxastaspastaxx.dimensions.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

/**
 * Parent class of commands that are being executed with <b>/dim [command]</b>
 *
 */

public abstract class DimensionsCommand {
	
	private String command;
	private String args;
	private String[] aliases;
	private String description;
	private String permission;
	private boolean adminCommand;
	
	/**
	 * Constructor of DimensionsCommand
	 * @param command the command
	 * @param args a string showing what arguments are required to execute the ocmmand
	 * @param aliases String array with aliases for the command
	 * @param description Short description of the command
	 * @param permission empty string to allow the default permission or the permission to run the command
	 * @param adminCommand true if this command should not be viewed by non-admin users
	 */
	public DimensionsCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand) {
		this.command = command;
		this.args = args;
		this.aliases = aliases;
		this.description = description;
		if (permission.equals("")) permission = "dimensions.command."+command;
		this.permission = permission;
		this.adminCommand = adminCommand;
	}
	
	/**
	 * Check if the command run was this instance
	 * @param contentRaw the command
	 * @return true if its the command or an alias of the command
	 */
	public boolean isThisCommand(String contentRaw) {
		String start = command;
		if (contentRaw.equalsIgnoreCase(start)) return true;
		
		for (int i = 0; i<aliases.length;i++) {
			start = aliases[i];
			if (contentRaw.equalsIgnoreCase(start)) return true;
		}
		return false;
	}
	
	/**
	 * Get command
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * Get the args
	 * @return the args
	 */
	public String getArgs() {
		return args;
	}
	
	/**
	 * Get the aliases
	 * @return the aliases
	 */
	public String[] getAliases() {
		return aliases;
	}
	
	/**
	 * Get the description
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Get the permission
	 * @return the permission
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * If its an admin command
	 * @return true if its an admin command
	 */
	public boolean isAdminCommand() {
		return adminCommand;
	}

	/**
	 * Execute the command
	 * @param sender the sender of the command
	 * @param args the arguments used
	 */
	public abstract void execute(CommandSender sender, String[] args);
	
	/**
	 * Requests a list of possible completions for a command argument.
	 * @param sender the sender of the command
	 * @param args the arguments used
	 */
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
	
}
