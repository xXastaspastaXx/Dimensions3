package me.xxastaspastaxx.dimensions.addons.chargeonuse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.events.CustomPortalUseEvent;
import net.milkbowl.vault.economy.Economy;

public class DimensionsChargeOnUse extends DimensionsAddon implements Listener {
	
	private Plugin pl;
	
	private Economy econ;
	
	public DimensionsChargeOnUse() {
		super("DimensionsChargeOnUseAddon", "3.0.0", "Charge players for using a portal", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public boolean onLoad(Dimensions main) {
		this.pl = main;

		return main.getServer().getPluginManager().getPlugin("Vault") != null;
	}
	
	@Override
	public void onEnable(Dimensions main) {
		this.pl = main;
		
		RegisteredServiceProvider<Economy> rsp = main.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) econ = rsp.getProvider();
        

		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPortalUse(CustomPortalUseEvent e) {
		
		CompletePortal complete = e.getCompletePortal();
		CustomPortal portal = complete.getCustomPortal();
		Object chargeAmount = getOption(portal, "chargeAmount");
		if (chargeAmount==null) return;
		Entity entity = e.getEntity();
		
		if (!shouldPlayerPay(complete, (Player) entity)) return;
		
		if (entity instanceof Player && econ.getBalance((Player) entity)<((int) chargeAmount)) {
			entity.sendMessage((String) getOption(portal, "chargeDenyMessage"));
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void postPortalUse(CustomPortalUseEvent e) {
		CompletePortal completePortal = e.getCompletePortal();
		CustomPortal portal = completePortal.getCustomPortal();
		Entity entity = e.getEntity();
		
		if (entity instanceof Player && shouldPlayerPay(completePortal, (Player) entity)) {
			entity.sendMessage((String) getOption(portal, "chargeAcceptMessage"));
			econ.withdrawPlayer((Player) entity, (double) getOption(portal, "chargeAmount"));
			completePortal.setTag("PAID_"+entity.getUniqueId().toString(), true);
		}
		
	}
	
	public boolean shouldPlayerPay(CompletePortal complete, Player p) {
		CustomPortal portal = complete.getCustomPortal();
		boolean returns = complete.getWorld().equals(portal.getWorld());
		
		if (!((boolean) getOption(portal, "chargeOnReturn")) && returns) return false;
		
		if ((boolean) getOption(portal, "chargeOneTime")) {
			if (complete.getTag("PAID_"+p.getUniqueId().toString()) == null)
				complete.setTag("PAID_"+p.getUniqueId().toString(), false);
			
			if ((boolean) complete.getTag("PAID_"+p.getUniqueId().toString())) return false;
		}
		
		return true;
	}

	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {

		int node = portalConfig.getInt("Addon.ChargeOnUse.Amount", 0);
		if (node==0) return;

		setOption(portal, "chargeAmount", node);
		setOption(portal, "chargeOnReturn", portalConfig.getBoolean("Addon.ChargeOnUse.ChargeOnReturn", false));
		setOption(portal, "chargeOneTime", portalConfig.getBoolean("Addon.ChargeOnUse.OneTimePayment", true));
		setOption(portal, "chargeDenyMessage", portalConfig.getString("Addon.ChargeOnUse.DenyMessage", "You do not have enough money to use this portal.").replace("&", "§"));
		setOption(portal, "chargeAcceptMessage", portalConfig.getString("Addon.ChargeOnUse.ChargeMessage", "You have been charged for using this portal.").replace("&", "§"));
		
		return;
	}
	
	@Override
	public boolean needsUpdate() throws MalformedURLException, IOException {
		String id = "1sTjeVz05pP6mjHn5X-uU7OV1_oCewyhAbGVtRw4lVvk";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "1dU8fhc5r0P9XmP8GbHv_yTM-cP8wkRVo";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}
	
}
