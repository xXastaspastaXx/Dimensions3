package me.xxastaspastaxx.dimensions.addons.stylishportals;


import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.xxastaspastaxx.dimensions.commands.DimensionsCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class StylishPortalCreateCommand extends DimensionsCommand {

	DimensionsStylishPortals main;
	
	public StylishPortalCreateCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand, DimensionsStylishPortals main) {
		super(command,args,aliases,description, permission, adminCommand);
		this.main = main;
		
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) return;

		Player p = (Player) sender;
		
		
		Block block = p.getTargetBlockExact(5);
		if (block == null || block.getBlockData()==null) {
			p.sendMessage("§7[§cDimensions§7] §aBlock not found");
			return;
		}
		
		String str = block.getBlockData().getAsString();
		
		TextComponent message = new TextComponent("§7[§cDimensions§7] §aClick here to copy block data");
		message.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, str));
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,  new Text(str)));
		p.spigot().sendMessage(message);
		

		
	}

}
