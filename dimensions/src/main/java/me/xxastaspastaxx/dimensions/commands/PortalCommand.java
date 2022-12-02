package me.xxastaspastaxx.dimensions.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;

public class PortalCommand extends DimensionsCommand {
	
	public PortalCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand) {
		super(command,args,aliases,description, permission, adminCommand);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (args.length==1) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§7[§cDimensions§7] This command without arguments can only be used from players.");
				return;
			}
			List<Block> los = ((Player) sender).getLineOfSight(null, 5);
			for (Block block : los) {
				if (!DimensionsUtils.isAir(block)) break;
				CompletePortal compl = Dimensions.getCompletePortalManager().getCompletePortal(block.getLocation(), false, false);
				if (compl!=null) {
					CustomPortal portal = compl.getCustomPortal();
					sender.sendMessage("§7[§cDimensions§7] "+portal.getDisplayName()+":§7 Is built from §c"+portal.getOutsideMaterial()+"§7, is ignited using §c"+portal.getLighterMaterial()+"§7 and this specific portal goes to §c"+(compl.getLinkedPortal()==null?portal.getWorld().getName():compl.getLinkedPortal().getWorld().getName())+"§7.");
					return;
				}
			}
			
			sender.sendMessage("§7[§cDimensions§7] Could not find a portal where you look at.");
		} else if (args.length==2) {
			CustomPortal portal = Dimensions.getCustomPortalManager().getCustomPortal(args[1]);
			if (portal!=null) {
				sender.sendMessage("§7[§cDimensions§7] "+portal.getDisplayName()+":§7 Is built from §c"+portal.getOutsideMaterial()+"§7, is ignited using §c"+portal.getLighterMaterial()+"§7 and goes to §c"+portal.getWorld().getName()+"§7.");
			} else {
				sender.sendMessage("§7[§cDimensions§7] Could not find specified portal.");
			}
		} else {
			sender.sendMessage("§7[§cDimensions§7] Missing argument. Please use /dim "+this.getCommand()+" "+this.getArgs());
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		ArrayList<String> res = new ArrayList<String>();

		if (args.length!=2) return res;
		
		Dimensions.getCustomPortalManager().getCustomPortals().forEach(p -> res.add(p.getPortalId()));
		
		return res;
	}
	
}
