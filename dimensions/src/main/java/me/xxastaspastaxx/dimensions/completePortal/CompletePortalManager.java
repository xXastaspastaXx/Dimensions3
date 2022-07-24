package me.xxastaspastaxx.dimensions.completePortal;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsSettings;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalDestroyCause;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalIgniteCause;
import me.xxastaspastaxx.dimensions.events.CustomPortalBreakEvent;
import me.xxastaspastaxx.dimensions.events.CustomPortalIgniteEvent;

public class CompletePortalManager {
	
	//private Dimensions pl;
	
	private CompletePortalLoader loader;
	
	private ArrayList<CompletePortal> completePortals = new ArrayList<CompletePortal>();
	
	public CompletePortalManager(Dimensions pl) {
		//this.pl = pl;
		this.loader = new CompletePortalLoader();
	}
	
	public ArrayList<CompletePortal> getCompletePortals() {
		return completePortals;
	}
	
	public CompletePortal getCompletePortal(Location location, boolean outside, boolean corner) {
		for (CompletePortal complete : completePortals) {
			if (complete.isInsidePortal(location, outside, corner)) return complete;
		}
		return null;
	}
	
	public List<CompletePortal> getCompletePortals(Location location, boolean outside, boolean corner) {
		List<CompletePortal> res = new ArrayList<CompletePortal>();
		
		for (CompletePortal complete : completePortals) {
			if (complete.isInsidePortal(location, outside, corner)) res.add(complete);
		}
		
		return res;
	}
	
	public ArrayList<CompletePortal> getCompletePortals(World world) {
		return (ArrayList<CompletePortal>) completePortals.stream().filter(complete -> complete.getWorld().equals(world)).collect(Collectors.toList());
	}
	
	public ArrayList<CompletePortal> getCompletePortals(World world, int x, int z) {
		return (ArrayList<CompletePortal>) completePortals.stream().filter(complete -> complete.isInChunk(world, x, z)).collect(Collectors.toList());
	}
	
	public ArrayList<CompletePortal> getCompletePortals(CustomPortal customPortal, World world) {
		return (ArrayList<CompletePortal>) completePortals.stream().filter(complete -> complete.getCustomPortal().equals(customPortal) && complete.getWorld().equals(world)).collect(Collectors.toList());
	}

	public ArrayList<CompletePortal> getCompletePortals(CustomPortal customPortal) {
		return (ArrayList<CompletePortal>) completePortals.stream().filter(complete -> complete.getCustomPortal().equals(customPortal)).collect(Collectors.toList());
	}

	public ArrayList<CompletePortal> getNearestPortals(Location loc, int searchRadius) {
		
		final int searchRadiusSquared = searchRadius*searchRadius;
		
		return (ArrayList<CompletePortal>) completePortals.stream().filter(complete -> complete.getWorld().equals(loc.getWorld()) && complete.getCenter().distanceSquared(loc)<=searchRadiusSquared).collect(Collectors.toList());
	}
	
	public ArrayList<CompletePortal> getNearestPortals(Location loc, int searchRadius, CustomPortal customPortal) {
		
		final int searchRadiusSquared = searchRadius*searchRadius;
		
		return (ArrayList<CompletePortal>) completePortals.stream().filter(complete -> complete.getWorld().equals(loc.getWorld()) && complete.getCustomPortal().equals(customPortal) && complete.getCenter().distanceSquared(loc)<=searchRadiusSquared).collect(Collectors.toList());
	}
	
	public CompletePortal getNearestPortal(Location teleportLocation, CustomPortal customPortal, double ratio) {
		
		int searchRadius = (int) Math.pow(DimensionsSettings.searchRadius, 2);
		double closestDistance = (searchRadius/2+1)+searchRadius*ratio*0.5;
		CompletePortal closestPortal = null;
		for(CompletePortal complete : completePortals) {
			if (!complete.getCustomPortal().equals(customPortal) || !complete.getWorld().equals(teleportLocation.getWorld())) continue;
			double dist = complete.getCenter().distanceSquared(teleportLocation);
			if (closestDistance>dist) {
	    		closestDistance = dist;
	    		closestPortal = complete;
	    	}
		}
		
		return closestPortal;
	}
	
	public CompletePortal getNearestPortal(Location teleportLocation, CompletePortal sample, double ratio, boolean sameAxis, boolean sameSize) {
		
		int searchRadius = (int) Math.pow(DimensionsSettings.searchRadius, 2);
		double closestDistance = (searchRadius/2+1)+searchRadius*ratio*0.5;
		CompletePortal closestPortal = null;
		for(CompletePortal complete : completePortals) {
			if (!complete.getCustomPortal().equals(sample.getCustomPortal()) || !complete.getCenter().getWorld().equals(teleportLocation.getWorld())) continue;
			if (sameAxis && sample.getPortalGeometry().iszAxis()!=complete.getPortalGeometry().iszAxis()) continue;
			if (sameSize && (sample.getPortalGeometry().getPortalWidth()!=complete.getPortalGeometry().getPortalWidth() || sample.getPortalGeometry().getPortalHeight()!=complete.getPortalGeometry().getPortalHeight())) continue;
			double dist = complete.getCenter().distanceSquared(teleportLocation);
			if (closestDistance>dist) {
	    		closestDistance = dist;
	    		closestPortal = complete;
	    	}
		}
		
		return closestPortal;
	}

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
	
	public boolean removePortal(CompletePortal completePortal, CustomPortalDestroyCause cause, Entity destroyer) {
		
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

	public void save() {
		this.loader.save(completePortals);
		
	}

	public void loadAll() {
		try {
			loader.loadAll();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
