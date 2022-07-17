package me.xxastaspastaxx.dimensions.addons.unbreakable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddon;
import me.xxastaspastaxx.dimensions.addons.DimensionsAddonPriority;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalDestroyCause;
import me.xxastaspastaxx.dimensions.events.CustomPortalBreakEvent;

public class DimensionsUnbreakable extends DimensionsAddon implements Listener {
	
	//private Plugin pl;
	
	public DimensionsUnbreakable() {
		super("DimensionsUnbreakableAddon", "3.0.0", "Unbreakable portals", DimensionsAddonPriority.NORMAL);
	}
	
	@Override
	public void onEnable(Dimensions pl) {
		//this.pl = pl;
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void destroyPortal(CustomPortalBreakEvent e) {
		CustomPortal portal = e.getCompletePortal().getCustomPortal();
		Object option = getOption(portal, "unbreakableList");
		if (option==null) return;
		@SuppressWarnings("unchecked")
		ArrayList<CustomPortalDestroyCause> reasons = (ArrayList<CustomPortalDestroyCause>) option;
		
		
		if (reasons.contains(e.getCause())) e.setCancelled(true);
	}

	@Override
	public void registerPortal(YamlConfiguration portalConfig, CustomPortal portal) {

		String[] spl = portalConfig.getString("Addon.Unbreakable", "false").split(", ");

		if(spl[0].equalsIgnoreCase("false")) return;

		ArrayList<CustomPortalDestroyCause> list = new ArrayList<CustomPortalDestroyCause>();
		for (String str : spl) {
			list.add(CustomPortalDestroyCause.valueOf(str));
		}
		
		setOption(portal, "unbreakableList", list);
		
		return;
	}
	
	
	@Override
	public boolean needsUpdate() throws UnsupportedEncodingException, IOException {
		String id = "12aWmuVstvyfcKZg2ZKJ4pYX9J_PAicANwLyMOmvdE_A";
		
        URL url = new URL("https://docs.google.com/feeds/download/documents/export/Export?id="+id+"&exportFormat=txt");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		return !getVersion().equals(in.readLine().replace(String.valueOf((char) 65279),""));
	}

	@Override
	public String getUpdateJarURL() {
		String id = "1P_F8y6rZRQyilEM_x_V93qvML7fcsyO3";
		
		return "https://drive.google.com/uc?id="+id+"&export=download";
	}
	
}
