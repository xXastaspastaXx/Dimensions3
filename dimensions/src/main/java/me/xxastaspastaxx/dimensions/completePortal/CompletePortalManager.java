package me.xxastaspastaxx.dimensions.completePortal;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsDebbuger;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalDestroyCause;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;
import me.xxastaspastaxx.dimensions.events.CustomPortalBreakEvent;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;
import me.xxastaspastaxx.dimensions.settings.DimensionsSettings;

/**
 * Manages all the complete portals
 *
 */

public class CompletePortalManager {
	
	//private Dimensions pl;
	
	private CompletePortalLoader loader;
	
	private ArrayList<CompletePortal> completePortals = new ArrayList<CompletePortal>();
	
	/**
	 * Constructor of the manager
	 * @param pl
	 */
	public CompletePortalManager(Dimensions pl) {
		//this.pl = pl;
		this.loader = new CompletePortalLoader();
	}
	
	/**
	 * Get all the complete portals that have been built
	 * @return all the complete portals that have been built
	 */
	public ArrayList<CompletePortal> getCompletePortals() {
		return completePortals;
	}
	/**
	 * Get the complete portal that matches the location given
	 * @param location the location to check at
	 * @param outside true to check if the location is at the frame of the portal
	 * @param corner true to check if the location is at the corners of the portal
	 */
	public CompletePortal getCompletePortal(Location location, boolean outside, boolean corner) {
		for (CompletePortal complete : completePortals) {
			if (complete.isInsidePortal(location, outside, corner)) return complete;
		}
		return null;
	}
	
	/**
	 * Get the complete portals that match the location given
	 * @param location the location to check at
	 * @param outside true to check if the location is at the frame of the portals
	 * @param corner true to check if the location is at the corners of the portals
	 */
	public List<CompletePortal> getCompletePortals(Location location, boolean outside, boolean corner) {
		List<CompletePortal> res = new ArrayList<CompletePortal>();
		
		for (CompletePortal complete : completePortals) {
			if (complete.isInsidePortal(location, outside, corner)) res.add(complete);
		}
		
		return res;
	}
	
	/**
	 * Get the complete portals in the given world
	 * @param world the world the check in
	 */
	public ArrayList<CompletePortal> getCompletePortals(World world) {
		return (ArrayList<CompletePortal>) completePortals.stream().filter(complete -> complete.getWorld().equals(world)).collect(Collectors.toList());
	}
	
	/**
	 * Get the complete portals in the given world in the specific chunk
	 * @param world the world the check in
	 * @param x chunk X
	 * @param z chunk Z
	 */
	public ArrayList<CompletePortal> getCompletePortals(World world, int x, int z) {
		return (ArrayList<CompletePortal>) completePortals.stream().filter(complete -> complete.isInChunk(world, x, z)).collect(Collectors.toList());
	}
	
	/**
	 * Get the complete portals in the given world that are specific CustomPortal
	 * @param customPortal the type of the portal
	 * @param world the world the check in
	 */
	public ArrayList<CompletePortal> getCompletePortals(CustomPortal customPortal, World world) {
		return (ArrayList<CompletePortal>) completePortals.stream().filter(complete -> complete.getCustomPortal().equals(customPortal) && complete.getWorld().equals(world)).collect(Collectors.toList());
	}

	/**
	 * Get the complete portals that are specific CustomPortal
	 * @param customPortal the type of the portal
	 */
	public ArrayList<CompletePortal> getCompletePortals(CustomPortal customPortal) {
		return (ArrayList<CompletePortal>) completePortals.stream().filter(complete -> complete.getCustomPortal().equals(customPortal)).collect(Collectors.toList());
	}

	/**
	 * Get the nearest portals inside a radius
	 * @param loc the center of the search
	 * @param searchRadius the radius
	 */
	public ArrayList<CompletePortal> getNearestPortals(Location loc, int searchRadius) {
		
		final int searchRadiusSquared = searchRadius*searchRadius;
		
		return (ArrayList<CompletePortal>) completePortals.stream().filter(complete -> complete.getWorld().equals(loc.getWorld()) && complete.getCenter().distanceSquared(loc)<=searchRadiusSquared).collect(Collectors.toList());
	}
	
	/**
	 * Get the nearest portals inside a radius that are specific CustomPortal
	 * @param loc the center of the search
	 * @param searchRadius the radius
	 * @param customPortal the CustomPortal
	 */
	public ArrayList<CompletePortal> getNearestPortals(Location loc, int searchRadius, CustomPortal customPortal) {
		
		final int searchRadiusSquared = searchRadius*searchRadius;
		
		return (ArrayList<CompletePortal>) completePortals.stream().filter(complete -> complete.getWorld().equals(loc.getWorld()) && complete.getCustomPortal().equals(customPortal) && complete.getCenter().distanceSquared(loc)<=searchRadiusSquared).collect(Collectors.toList());
	}
	
	/**
	 * Get the nearest portal
	 * @param teleportLocation the cetner of the search
	 * @param sample the portal to check for similarities
	 * @param ratio the world ratio
	 * @param sameAxis true to allow only portals with the same axis
	 * @param sameSize true to allow only portals with the same size
	 * @param predicate if it tests true, then the portal will be included in the search
	 */
	public CompletePortal getNearestPortal(Location teleportLocation, CompletePortal sample, double ratio, boolean sameAxis, boolean sameSize, Predicate<CompletePortal> predicate) {
		double closestDistance =  Math.pow(DimensionsSettings.searchRadius*ratio,2);
		CompletePortal closestPortal = null;
		for(CompletePortal complete : completePortals) {
			if (!complete.getCustomPortal().equals(sample.getCustomPortal()) || !complete.getCenter().getWorld().equals(teleportLocation.getWorld())) continue;
			if (DimensionsSettings.ignoreLinkedPortals && complete.getLinkedPortal()!=null) continue;
			if (sameAxis && sample.getPortalGeometry().iszAxis()!=complete.getPortalGeometry().iszAxis()) continue;
			if (sameSize && (sample.getPortalGeometry().getPortalWidth()!=complete.getPortalGeometry().getPortalWidth() || sample.getPortalGeometry().getPortalHeight()!=complete.getPortalGeometry().getPortalHeight())) continue;
			if (!predicate.test(closestPortal)) continue;
			Location temp = complete.getCenter();
			double dist = NumberConversions.square(temp.getX() - teleportLocation.getX()) + NumberConversions.square(temp.getZ() - teleportLocation.getZ());
			
			if (closestDistance>dist) {
	    		closestDistance = dist;
	    		closestPortal = complete;
	    	}
		}
		
		return closestPortal;
	}
	
	/**
	 * Get the nearest portal
	 * @param teleportLocation the cetner of the search
	 * @param sample the portal to check for similarities
	 * @param ratio the world ratio
	 * @param sameAxis true to allow only portals with the same axis
	 * @param sameSize true to allow only portals with the same size
	 */
	public CompletePortal getNearestPortal(Location teleportLocation, CompletePortal sample, double ratio, boolean sameAxis, boolean sameSize) {
		return getNearestPortal(teleportLocation, sample, ratio, sameAxis, sameSize, (portal) -> true);
	}
	
	/**
	 * Register a new CompletePortal
	 * @param completePortal the portal to register
	 * @param igniter the entity causing the ignite
	 * @param cause the cause of the ignite
	 * @param item the item used to ignite
	 * @return the complete portal <b>THE COMPLETE PORTAL MIGHT DIFFERENT THAN THE PORTAL SUPPLIED</b>
	 * 
	 * @see CustomPortalIgniteEvent#replaceCompletePortal(CompletePortal)
	 */
	public CompletePortal createNew(CompletePortal completePortal, Entity igniter, CustomPortalIgniteCause cause, ItemStack item) {

		if (completePortal.getPortalGeometry()==null) return null;
		
		CustomPortalIgniteEvent igniteEvent = new CustomPortalIgniteEvent(completePortal, cause, igniter, item);
		Bukkit.getPluginManager().callEvent(igniteEvent);
		
		if (igniteEvent.isCancelled()) return null;
		completePortal = igniteEvent.getCompletePortal();
		
		completePortals.add(completePortal);
		completePortal.fill(null);
		
		return completePortal;
	}
	
	/**
	 * Unregister the complete portal
	 * @param completePortal the portal to remove
	 * @param cause the cause of the removal
	 * @param destroyer the entity causing the removel
	 * @return true if the portal was removed
	 */
	public boolean removePortal(CompletePortal completePortal, CustomPortalDestroyCause cause, Entity destroyer) {
		
		DimensionsDebbuger.DEBUG.print("DESTROYED BY "+cause+" "+destroyer);
		CustomPortalBreakEvent breakEvent = new CustomPortalBreakEvent(completePortal, cause, destroyer);
		Bukkit.getPluginManager().callEvent(breakEvent);
		
		if (!breakEvent.isCancelled()) {
			completePortals.remove(completePortal);
			if (completePortal.getLinkedPortal()!=null) {
				completePortal.getLinkedPortal().unlinkPortal();
			}
			completePortal.destroy(null);
			return true;
		}
		return false;
	}

	/**
	 * Save the portals
	 */
	public void save() {
		this.loader.save(completePortals);
		
	}

	/**
	 * Load the portals
	 */
	public void loadAll() {
		try {
			loader.loadAll();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
