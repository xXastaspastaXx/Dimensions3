package me.xxastaspastaxx.dimensions.addons.customlighter.commands;


import java.io.File;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.customlighter.DimensionsCustomLighter;
import me.xxastaspastaxx.dimensions.commands.DimensionsCommand;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;

public class CustomLighterCommand extends DimensionsCommand {

	DimensionsCustomLighter main;

	public CustomLighterCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand, DimensionsCustomLighter main) {
		super(command,args,aliases,description, permission, adminCommand);
		this.main = main;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) return;

		Player p = (Player) sender;

		CustomPortal portal = Dimensions.getCustomPortalManager().getCustomPortal(args[1]);
		if (portal==null) {
			p.sendMessage("§7[§cDimensions§7] No portal found.");
			return;
		}

		ItemStack item = p.getInventory().getItemInMainHand();
		if  (item==null || item.getType()==Material.AIR) {
			p.sendMessage("§7[§cDimensions§7] No item found in your hand.");
			return;
		}

		
		try {
			YamlConfiguration conf = new YamlConfiguration();
	        conf.set( "item", item );
	        String data = conf.saveToString();
	        
			main.getPortalConfig(portal).set("Addon.CustomLighter.Item", "MINECRAFT:"+data);
			main.getPortalConfig(portal).set("Portal.LighterMaterial", item.getType().name());
			
			p.sendMessage("§7[§cDimensions§7] §aThe custom lighter was succesfully updated, please reload Dimensions");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			main.getPortalConfig(portal).save(new File("plugins/Dimensions/Portals/"+portal.getPortalId()+".yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
