package me.xxastaspastaxx.dimensions.completePortal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;

/**
 * Load all saved complete portals
 *
 */

public class CompletePortalLoader {
	
	private static final String FILE_PATH = "./plugins/Dimensions/data/savedPortals.json";
	
	private Gson gson;
	
	
	/**
	 * Constructor of the portal loader
	 */
	public CompletePortalLoader() {
		gson = new GsonBuilder().setPrettyPrinting().create();
		
		File f = new File(FILE_PATH);
		if (!f.exists()) {
			try {
				f.getParentFile().mkdirs();
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Load all the portals from the .json file
	 * @throws FileNotFoundException
	 */
	public void loadAll() throws FileNotFoundException {
		Reader reader = new BufferedReader((new FileReader(FILE_PATH)));
		ArrayList<HashMap<String, Object>> portals = gson.fromJson(reader, new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
		try {

			for (HashMap<String, Object> portal : portals) {
				CustomPortal customPortal = Dimensions.getCustomPortalManager().getCustomPortal((String) portal.get("customPortal"));
				World world = Bukkit.getWorld((String) portal.get("world"));
				Location loc = new Location(world, (double) portal.get("centerX"), (double) portal.get("centerY"), (double) portal.get("centerZ"));
				PortalGeometry geom = PortalGeometry.getPortalGeometry(customPortal).getPortal(customPortal, loc);
				if (geom==null) continue;
				
				CompletePortal linked = null;
				
				if (portal.containsKey("linkedPortalWorld")) {
					World linkedWorld = Bukkit.getWorld((String) portal.get("linkedPortalWorld"));
					Location linkedLoc = new Location(linkedWorld, (double) portal.get("linkedPortalCenterX"), (double) portal.get("linkedPortalCenterY"), (double) portal.get("linkedPortalCenterZ"));
					linked = Dimensions.getCompletePortalManager().getCompletePortal(linkedLoc, false, false);
					
				}
				
				CompletePortal completePortal = new CompletePortal(customPortal, world, geom, linked);
				completePortal.setTags(gson.fromJson((String) portal.get("portalTags"), new TypeToken<HashMap<String, Object>>() { }.getType()));
				
				Dimensions.getCompletePortalManager().createNew(completePortal, null, CustomPortalIgniteCause.LOAD_PORTAL, null);
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save all portals inside the .json file
	 * @param portals
	 */
	public void save(List<CompletePortal> portals) {
		
		ArrayList<HashMap<String, Object>> res = new ArrayList<HashMap<String, Object>>();
		
		for (CompletePortal portal : portals) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("customPortal", portal.getCustomPortal().getPortalId());
			map.put("world", portal.getWorld().getName());
			map.put("centerX", portal.getCenter().getX());
			map.put("centerY", portal.getCenter().getY());
			map.put("centerZ", portal.getCenter().getZ());
			
			CompletePortal linked = portal.getLinkedPortal();
			if (linked!=null) {
				map.put("linkedPortalWorld", linked.getWorld().getName());
				map.put("linkedPortalCenterX", linked.getCenter().getX());
				map.put("linkedPortalCenterY", linked.getCenter().getY());
				map.put("linkedPortalCenterZ", linked.getCenter().getZ());
			}
			
			map.put("portalTags", gson.toJson(portal.getTags()));
			

			portal.destroy(null);
			res.add(map);
		}
		
		try {
			PrintWriter writer = new PrintWriter(FILE_PATH, "UTF-8");
		    writer.println(gson.toJson(res));
		    writer.close();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
}
