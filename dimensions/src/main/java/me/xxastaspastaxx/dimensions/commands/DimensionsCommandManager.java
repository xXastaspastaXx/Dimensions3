package me.xxastaspastaxx.dimensions.commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.xxastaspastaxx.dimensions.Dimensions;

/**
 * Class to register and manage the Dimensions commands
 *
 */

public class DimensionsCommandManager implements CommandExecutor {

    private static ArrayList<DimensionsCommand> commands = new ArrayList<DimensionsCommand>();
    
    /**
     * Contruct the manager and set the command executor for the command /dimensions
     * @param main the instance of the Dimensions lpugin
     */
    public DimensionsCommandManager(Dimensions main) {
    	commands.add(new HelpCommand("help", "", new String[] {"h"}, "List all commands", "none", false));
    	commands.add(new AdminHelpCommand("adminHelp", "", new String[] {"ah"}, "List all admin commands", "", false));
    	commands.add(new PermissionsCommand("permissions", "", new String[] {"perms"}, "List all commands with their permissions", "", true));
    	commands.add(new ReloadCommand("reload", "", new String[0], "Reload all configurations and addons", "", true));
    	commands.add(new AdminPermissionsCommand("adminPermissions", "", new String[] {"aperms"}, "List all admin commands with their permissions", "", true));
		//tempCommands.add(new AddonCommand("addons", "", new String[0], "Open the addon manager gui", "", true));
		//tempCommands.add(new ReloadCommand("reload", "", new String[0], "Reload the plugin", "", true));
		//tempCommands.add(new TestCommand("test", "", new String[] {"t"}, "test", "", true));

    	commands.add(new ClearCommand("clear", "<all/world/portal>", new String[] {"clr"}, "Delete all saved portals.", "", true));
    	commands.add(new PortalCommand("portal", "[portal]", new String[0], "Show info of specified portal or look at a portal", "", true));
    	commands.add(new PortalsCommand("portals", "", new String[0], "Show all portals", "", true));
		
		main.getCommand("dimensions").setExecutor(this);
		
	}
    
    /**
     * Check if the command is registered and execute it
     */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args.length==0) args = new String[] {"h"};
		
		for (DimensionsCommand command : commands) {
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
		
		commands.get(0).execute(sender, args);
		
		return true;
	}
	
	/**
	 * Get the list of the non-admin commands
	 * @return the list of the non-admin commands
	 */
	public static ArrayList<DimensionsCommand> getCommands() {
		ArrayList<DimensionsCommand> res = new ArrayList<DimensionsCommand>();
		for (DimensionsCommand cmd : commands) {
			if (cmd.isAdminCommand()) continue;
			res.add(cmd);
		}
		return res;
	}
	
	/**
	 * Get the list of the admin commands
	 * @return the list of the admin commands
	 */
	public static ArrayList<DimensionsCommand> getAdminCommands() {
		ArrayList<DimensionsCommand> res = new ArrayList<DimensionsCommand>();
		for (DimensionsCommand cmd : commands) {
			if (!cmd.isAdminCommand()) continue;
			res.add(cmd);
		}
		return res;
	}
	
	/**
	 * Register a new DimensionsCommand
	 * @param cmd the command instance
	 */
	public void registerCommand(DimensionsCommand cmd) {
		commands.add(cmd);
	}
	
	/**
	 * Unregister a command
	 * @param cmd the instance of the command
	 */
	public void unregisterCommand(DimensionsCommand cmd) {
		commands.remove(cmd);
	}
	
	
	
}
