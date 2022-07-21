package me.xxastaspastaxx.dimensions.commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.xxastaspastaxx.dimensions.Dimensions;

public class DimensionsCommandManager implements CommandExecutor {

    private static HashMap<String, ArrayList<DimensionsCommand>> commands = new HashMap<String, ArrayList<DimensionsCommand>>();
    
    public DimensionsCommandManager(Dimensions main) {
		ArrayList<DimensionsCommand> tempCommands = new ArrayList<DimensionsCommand>();
		tempCommands.add(new HelpCommand("help", "", new String[] {"h"}, "List all commands", "none", false));
		tempCommands.add(new AdminHelpCommand("adminHelp", "", new String[] {"ah"}, "List all admin commands", "", false));
		tempCommands.add(new PermissionsCommand("permissions", "", new String[] {"perms"}, "List all commands with their permissions", "", true));
		tempCommands.add(new ReloadCommand("reload", "", new String[0], "Reload all configurations and addons", "", true));
		tempCommands.add(new AdminPermissionsCommand("adminPermissions", "", new String[] {"aperms"}, "List all admin commands with their permissions", "", true));
		//tempCommands.add(new AddonCommand("addons", "", new String[0], "Open the addon manager gui", "", true));
		//tempCommands.add(new ReloadCommand("reload", "", new String[0], "Reload the plugin", "", true));
		//tempCommands.add(new TestCommand("test", "", new String[] {"t"}, "test", "", true));
		commands.put("General commands", tempCommands);
		

		tempCommands = new ArrayList<DimensionsCommand>();
		tempCommands.add(new ClearCommand("clear", "<all/world/portal>", new String[] {"clr"}, "Delete all saved portals.", "", true));
		tempCommands.add(new PortalCommand("portal", "[portal]", new String[0], "Show info of specified portal or look at a portal", "", true));
		tempCommands.add(new PortalsCommand("portals", "", new String[0], "Show all portals", "", true));
		commands.put("Portal commands", tempCommands);
		
		main.getCommand("dimensions").setExecutor(this);
		
	}
    
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args.length==0) args = new String[] {"h"};
		
		for (ArrayList<DimensionsCommand> commandList : commands.values()) {
			for (DimensionsCommand command : commandList) {
				if (!command.isThisCommand(args[0])) continue;
				if (!command.getPermission().contentEquals("none") && !sender.hasPermission(command.getPermission())) {
					sender.sendMessage("§7[§cDimensions§7] §4You do not have permission to execute this command");
					return true;
				}
				try {
					command.execute(sender, args);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}	
		}
		
		commands.get("General commands").get(0).execute(sender, args);
		
		return true;
	}
	
	public static ArrayList<DimensionsCommand> getCommands() {
		ArrayList<DimensionsCommand> res = new ArrayList<DimensionsCommand>();
		for (ArrayList<DimensionsCommand> commandList : DimensionsCommandManager.commands.values()) {
			for (DimensionsCommand cmd : commandList) {
				if (cmd.isAdminCommand()) continue;
				res.add(cmd);
			}
		}
		return res;
	}
	
	public static ArrayList<DimensionsCommand> getAdminCommands() {
		ArrayList<DimensionsCommand> res = new ArrayList<DimensionsCommand>();
		for (ArrayList<DimensionsCommand> commandList : DimensionsCommandManager.commands.values()) {
			for (DimensionsCommand cmd : commandList) {
				if (!cmd.isAdminCommand()) continue;
				res.add(cmd);
			}
		}
		return res;
	}
	
	public void registerCommand(String commandCategory, DimensionsCommand cmd) {
		if (!commands.containsKey(commandCategory)) commands.put(commandCategory, new ArrayList<DimensionsCommand>());
		commands.get(commandCategory).add(cmd);
	}
	
	public void unregisterCommand(String commandCategory, DimensionsCommand cmd) {
		commands.get(commandCategory).remove(cmd);
	}
	
	
	
}
