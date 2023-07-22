package me.xxastaspastaxx.dimensions.addons.patreoncosmetics;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.xxastaspastaxx.dimensions.commands.DimensionsCommand;

public class PatreonCommand extends DimensionsCommand {

	DimensionsPatreonCosmetics main;

	public PatreonCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand, DimensionsPatreonCosmetics main) {
		super(command,args,aliases,description, permission, adminCommand);
		this.main = main;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) return;

		Player p = (Player) sender;
		
		if (args.length>=2) {
			Player p2;
			if ((p2 = Bukkit.getPlayer(args[1]))!=null) {
				p.sendMessage("§7[§cDimensions§7] "+getStatusString(p2));
			} else {
				p.sendMessage("§7[§cDimensions§7] §cCould not find player "+args[1]);
			}
		} else {
			p.sendMessage("§7[§cDimensions§7] "+getStatusString(p));
		}
	}
	
	public String getStatusString(Player p) {
		String res = "§7Player: §a"+p.getName()+" §7\nUUID: §a"+p.getUniqueId().toString()+"\n";
		res+="§7Supporter: "+(main.getUsers().containsKey(p.getUniqueId())?"§atrue":"§cfalse")+"\n";
		res+="§7Effects:\n";
		if (main.getUsers().containsKey(p.getUniqueId())) {
			for (String s : main.getUsers().get(p.getUniqueId()).keySet()) {
				res+="  §a"+s+": "+main.getUsers().get(p.getUniqueId()).get(s)+"\n";
			}
		} else {
			res+="  §cNo active effects";
		}
		return res;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		ArrayList<String> res = new ArrayList<String>();
		
		if (args.length!=2) return res;
		
		Bukkit.getOnlinePlayers().forEach(p -> res.add(p.getName()));
		
		return res;
	}
}
