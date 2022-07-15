package me.xxastaspastaxx.dimensions.addons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

import com.github.mrivanplays.jarloader.api.JarLoader;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsDebbuger;
import me.xxastaspastaxx.dimensions.DimensionsSettings;

public class DimensionsAddonManager {

	private final String ADDONS_PATH = "./plugins/Dimensions/Addons/";
	
	private Dimensions pl;

	private ArrayList<DimensionsAddon> loadedAddons = new ArrayList<DimensionsAddon>();
	private HashMap<DimensionsAddon, File> jarFiles = new HashMap<DimensionsAddon, File>();
	
	private JarLoader loader;
	
	public DimensionsAddonManager(Dimensions pl) {
		this.pl = pl;
		
		loader = new JarLoader();
		
		loadedAddons.addAll(loadAll(new File(ADDONS_PATH), false));
		
		ArrayList<DimensionsAddon> res = new ArrayList<DimensionsAddon>();
		for (DimensionsAddonPriority priority : DimensionsAddonPriority.values()) {
			for (DimensionsAddon addon : loadedAddons) {
				if (addon.getAddonPriority()==priority) res.add(addon);
			}
		}
		
		loadedAddons = res;
	}
	
	public void enableAddons() {
		for (DimensionsAddon addon : loadedAddons) {
			DimensionsDebbuger.debug("Enabling addon: "+addon.getName()+" v"+addon.getVersion(), DimensionsDebbuger.MEDIUM);
			addon.onEnable(pl);
		}
	}
	
	public List<DimensionsAddon> loadAll(File dir, boolean isReload) {
	    if(!dir.exists()) {
	    	dir.mkdirs();
	    }
	    if(!dir.isDirectory()) {
		      return Collections.emptyList();
		    }
	    List<DimensionsAddon> loaded = new ArrayList<>();
	    for(File file : dir.listFiles((file, name) -> name.endsWith(".jar"))) {
	    	try {
		    	DimensionsAddon addon = loader.load(file, DimensionsAddon.class);
				DimensionsDebbuger.debug("Loading addon: "+addon.getName()+" v"+addon.getVersion(), DimensionsDebbuger.MEDIUM);
				
				boolean con = false;
				for (DimensionsAddon addon2 : loaded) {
					if (addon2.getName().contentEquals(addon.getName())) {
						DimensionsDebbuger.debug("Addon already exists: "+addon.getName()+" v"+addon.getVersion(), DimensionsDebbuger.MEDIUM);
						con = true;
						break;
					}
				}
				if (con) continue;
				if (DimensionsSettings.checkForUpdatesOnStartup) {
					if (addon.needsUpdate()) {
						DimensionsDebbuger.debug("Found new version for "+addon.getName()+". Updating addon...", DimensionsDebbuger.MEDIUM);
						try {
							
							addon = downloadAndExportAddon(file.getName(), addon.getUpdateJarURL());
							DimensionsDebbuger.debug("Update complete for "+addon.getName()+".", DimensionsDebbuger.MEDIUM);
						} catch (Exception e) {

							DimensionsDebbuger.debug("Could not update", DimensionsDebbuger.MEDIUM);
							continue;
						}
					}
				}
				
				jarFiles.put(addon, file);
		    	if (addon.onLoad(pl)) {
			    	loaded.add(addon);
			    } 
	    	} catch (Exception | Error e) {
				DimensionsDebbuger.debug("Could not load addon "+file.getName()+". More info bellow:", DimensionsDebbuger.HIGH);
				e.printStackTrace();
	    	}
	    }
	    return loaded;
	  }

	public DimensionsAddon downloadAndExportAddon(String jarName, String updateJarURL) {
	    try {
	    	URLConnection urlConn = new URL(updateJarURL).openConnection();
	    	urlConn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
	    	
	    	File file = new File(ADDONS_PATH+jarName);
	    	
	    	if (file.exists()) {
	    		FileOutputStream fos = new FileOutputStream(file);
		    	fos.flush();
		    	fos.write(urlConn.getInputStream().readAllBytes());
		    	fos.close();
	    	} else {
	    		Files.copy(urlConn.getInputStream(), Paths.get(ADDONS_PATH+jarName), StandardCopyOption.REPLACE_EXISTING);
	    	}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	    
		return loader.load(new File(ADDONS_PATH+jarName), DimensionsAddon.class);
		
	}

	public DimensionsAddon getAddonByName(String addonName) {
		for (DimensionsAddon addon : loadedAddons) {
			if (addon.getName().contentEquals(addonName)) return addon;
		}
		return null;
	}

	public ArrayList<DimensionsAddon> getAddons() {
		return loadedAddons;
	}
	
	public void onDisable() {
		for (DimensionsAddon addon : loadedAddons) {
			addon.onDisable();
		}

	}

	public void reloadAddon(DimensionsAddon addo) {
		if (addo==null) {
			for (DimensionsAddon ad : getAddons()) {
				reloadAddon(ad);
			}
		}
		
	    if (unload(addo)) {
	    	try {
		    	DimensionsAddon addon = loader.load(jarFiles.get(addo), DimensionsAddon.class);
				DimensionsDebbuger.debug("Loading addon: "+addon.getName()+" v"+addon.getVersion(), DimensionsDebbuger.MEDIUM);
				
		    	if (addon.onLoad(pl)) {
		    		loadedAddons.add(addon);
		    		addon.onEnable(pl);
			    } 
	    	} catch (Exception | Error e) {
				DimensionsDebbuger.debug("Could not load addon "+jarFiles.get(addo).getName()+". More info bellow:", DimensionsDebbuger.HIGH);
				e.printStackTrace();
	    	}
	    }
	    
		
	}

	public void unloadAll() {

		Iterator<DimensionsAddon> iter = loadedAddons.iterator();
		while (iter.hasNext()) {
			DimensionsAddon add = iter.next();
			iter.remove();
			unload(add);
		}
		
	}
	
	ArrayList<String> dontUnload = new ArrayList<String>(Arrays.asList(new String[] {"me.xxastaspastaxx.dimensions.listener.BaseListener", "me.xxastaspastaxx.commands.AddonCommand"}));
	
	public boolean unload(DimensionsAddon plugin) {
		plugin.onDisable();
		plugin.onUnLoad();
		loadedAddons.remove(plugin);
		
		for (RegisteredListener r : HandlerList.getRegisteredListeners(pl)) {
			String s = r.getListener().getClass().getName();
			if (dontUnload.contains(s)) {
				continue;
			}
			
			HandlerList.unregisterAll(r.getListener());
		}
		
		return true;
    }

	public boolean update(DimensionsAddon addon) {
		try {
			if (!addon.needsUpdate()) return false;
			DimensionsDebbuger.debug("Found new version for "+addon.getName()+". Updating addon...", DimensionsDebbuger.MEDIUM);
		
			addon = downloadAndExportAddon(jarFiles.get(addon).getName(), addon.getUpdateJarURL());
			reloadAddon(addon);
			DimensionsDebbuger.debug("Update complete for "+addon.getName()+".", DimensionsDebbuger.MEDIUM);
		} catch (Exception e) {
			DimensionsDebbuger.debug("Could not update", DimensionsDebbuger.MEDIUM);
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
