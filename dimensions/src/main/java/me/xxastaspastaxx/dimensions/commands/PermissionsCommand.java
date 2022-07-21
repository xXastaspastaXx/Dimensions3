package me.xxastaspastaxx.dimensions.commands;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import me.xxastaspastaxx.dimensions.DimensionsUtils;

public class PermissionsCommand extends DimensionsCommand {

	private int commandsPerPage = 5;
	
	public PermissionsCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand) {
		super(command,args,aliases,description, permission, adminCommand);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		String head = "§7[§cDimensions§7] Commands list:";
		int page = 0;
		if (args.length>1 && DimensionsUtils.isInt(args[1]) && !args[1].equals("0")) page = Integer.parseInt(args[1])-1;
		ArrayList<DimensionsCommand> commandList = DimensionsCommandManager.getCommands();
		for (int i =Math.max(page*commandsPerPage, 0);i<Math.min(page*commandsPerPage+(commandList.size()-commandsPerPage*page), commandsPerPage*(1+page));i++) {
			DimensionsCommand cmd = (DimensionsCommand) commandList.toArray()[i];
        	head += "\n/dim "+cmd.getCommand()+" "+cmd.getArgs()+" §c-§7 "+cmd.getPermission();
    	}
		if (Math.min(commandList.size()-(1+page)*commandsPerPage, commandsPerPage*(2+page)) > 0 || page!=0)
			head += "\n\n**Page "+(page+1)+"/"+((int) Math.ceil(commandList.size()/commandsPerPage))+"**";
		
		sender.sendMessage(head);
	}
	
}
