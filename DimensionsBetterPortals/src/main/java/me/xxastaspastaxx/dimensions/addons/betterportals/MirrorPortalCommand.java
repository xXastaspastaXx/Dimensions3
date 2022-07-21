package me.xxastaspastaxx.dimensions.addons.betterportals;


import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.commands.DimensionsCommand;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;

public class MirrorPortalCommand extends DimensionsCommand {

	DimensionsBetterPortals main;

	public MirrorPortalCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand, DimensionsBetterPortals main) {
		super(command,args,aliases,description, permission, adminCommand);
		this.main = main;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) return;
		
		List<Block> los = ((Player) sender).getLineOfSight(null, 5);
		for (Block block : los) {
			if (!DimensionsUtils.isAir(block)) break;
			CompletePortal compl = Dimensions.getCompletePortalManager().getCompletePortal(block.getLocation(), false, false);
			if (compl!=null) {
				CompletePortal link = compl.getLinkedPortal();

				main.unlink(compl);
				main.link(compl, link, !(compl.getTag("mirrored")!=null && ((boolean) compl.getTag("mirrored"))));
				
				
				sender.sendMessage("§7[§cDimensions§7] The portal has been mirrored");
				return;
			}
		}
	}

}
