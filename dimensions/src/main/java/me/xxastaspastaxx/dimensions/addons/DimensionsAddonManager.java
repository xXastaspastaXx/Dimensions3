package me.xxastaspastaxx.dimensions.addons;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ServiceLoader;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsDebbuger;

public class DimensionsAddonManager {

	private final String ADDONS_PATH = "./plugins/Dimensions/Addons/";
	
	private Dimensions pl;

	private ServiceLoader<DimensionsAddon> loader;
	
	private ArrayList<DimensionsAddon> loadedAddons = new ArrayList<DimensionsAddon>();
	
	
	private URL[] urls;
	
	public DimensionsAddonManager(Dimensions pl) {
		this.pl = pl;
		
		
		File dir = new File(ADDONS_PATH);
	    if(!dir.exists()) dir.mkdirs();
	    
	    ArrayList<URL> urls = new ArrayList<URL>();
	    for(File file : dir.listFiles((file, name) -> name.endsWith(".jar"))) {
	    	try {
				urls.add(file.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
	    	
	    }
	    this.urls = urls.toArray(new URL[0]);
		loader = ServiceLoader.load(DimensionsAddon.class, URLClassLoader.newInstance(this.urls, Dimensions.class.getClassLoader()));
		
		for (DimensionsAddon addon : loader) {
			if (addon.onLoad(pl)) {
				DimensionsDebbuger.MEDIUM.print("Loaded addon: "+addon.getName()+" v"+addon.getVersion());
				loadedAddons.add(addon);
			} else {
				DimensionsDebbuger.MEDIUM.print("Failed to load addon: "+addon.getName()+" v"+addon.getVersion());
			}
		}
	}
	
	public void enableAddons() {
		for (DimensionsAddon addon : loadedAddons) {
			DimensionsDebbuger.MEDIUM.print("Enabling addon: "+addon.getName()+" v"+addon.getVersion());
			addon.onEnable(pl);
		}
	}

	/*public DimensionsAddon downloadAndExportAddon(String jarName, String updateJarURL) {
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
	    
	    return null;
		//return loader.load(new File(ADDONS_PATH+jarName), DimensionsAddon.class, urls);
		
	}*/

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
			addon.resetOptions();
		}
		
	}

	public void unloadAll() {

		for (DimensionsAddon addon : loadedAddons) {
			unload(addon);
		}
		
	}
	
	ArrayList<String> dontUnload = new ArrayList<String>(Arrays.asList(new String[] {"me.xxastaspastaxx.dimensions.listener.PortalListener", "me.xxastaspastaxx.dimensions.commands.AddonCommand"}));
	
	public boolean unload(DimensionsAddon plugin) {
		plugin.onDisable();
		plugin.resetOptions();
		
		for (RegisteredListener r : HandlerList.getRegisteredListeners(pl)) {
			String s = r.getListener().getClass().getName();
			if (dontUnload.contains(s)) continue;
			
			HandlerList.unregisterAll(r.getListener());
		}
		
		return true;
    }

	/*public boolean update(DimensionsAddon addon) {
		try {
			if (!addon.needsUpdate()) return false;
			DimensionsDebbuger.debug("Found new version for "+addon.getName()+". Updating addon...", DimensionsDebbuger.MEDIUM);
		
			addon = downloadAndExportAddon(jarFiles.get(addon).getName(), addon.getUpdateJarURL());
			DimensionsDebbuger.debug("Update complete for "+addon.getName()+".", DimensionsDebbuger.MEDIUM);
		} catch (Exception e) {
			DimensionsDebbuger.debug("Could not update", DimensionsDebbuger.MEDIUM);
			e.printStackTrace();
			return false;
		}
		
		return true;
	}*/
}
