package me.xxastaspastaxx.dimensions.commands;

import org.bukkit.command.CommandSender;

public abstract class DimensionsCommand {
	
	private String command;
	private String args;
	private String[] aliases;
	private String description;
	private String permission;
	private boolean adminCommand;
	
	public DimensionsCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand) {
		this.command = command;
		this.args = args;
		this.aliases = aliases;
		this.description = description;
		if (permission.equals("")) permission = "dimensions.command."+command;
		this.permission = permission;
		this.adminCommand = adminCommand;
	}
	
	public boolean isThisCommand(String contentRaw) {
		String start = command;
		if (contentRaw.equalsIgnoreCase(start)) return true;
		
		for (int i = 0; i<aliases.length;i++) {
			start = aliases[i];
			if (contentRaw.equalsIgnoreCase(start)) return true;
		}
		return false;
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getArgs() {
		return args;
	}
	
	public String[] getAliases() {
		return aliases;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getPermission() {
		return permission;
	}

	public boolean isAdminCommand() {
		return adminCommand;
	}

	public abstract void execute(CommandSender sender, String[] args);
	
}
