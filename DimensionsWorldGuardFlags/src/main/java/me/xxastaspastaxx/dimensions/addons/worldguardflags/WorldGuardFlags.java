package me.xxastaspastaxx.dimensions.addons.worldguardflags;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class WorldGuardFlags {

	public static WorldGuardPlatform worldGuard;
	public static boolean enabled = false;

	public static StateFlag IgniteCustomPortal;
	public static StateFlag UseCustomPortal;
	public static StateFlag DestroyCustomPortal;
	
	public WorldGuardFlags() {
    	enabled = true;
		
        //WORLD GUARD\\
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag ignite = new StateFlag("ignite-custom-portal", true);
            registry.register(ignite);
            IgniteCustomPortal = ignite;
            
            StateFlag use = new StateFlag("use-custom-portal", true);
            registry.register(use);
            UseCustomPortal = use;
            
            StateFlag destroy = new StateFlag("destroy-custom-portal", true);
            registry.register(destroy);
            DestroyCustomPortal = destroy;
        } catch (FlagConflictException e) {
            Flag<?> igniteExisting = registry.get("ignite-custom-portal");
            if (igniteExisting instanceof StateFlag) {
            	IgniteCustomPortal = (StateFlag) igniteExisting;
            } else {
                System.out.println("Some other plugin is using dimensions' custom flags. This could cause errors. Please remove this plugin.");
            }
            
            Flag<?> useExisting = registry.get("use-custom-portal");
            if (useExisting instanceof StateFlag) {
            	UseCustomPortal = (StateFlag) useExisting;
            } else {
                System.out.println("Some other plugin is using dimensions' custom flags. This could cause errors. Please remove this plugin.");
            }
            
            Flag<?> destroyExisting = registry.get("destroy-custom-portal");
            if (destroyExisting instanceof StateFlag) {
            	DestroyCustomPortal = (StateFlag) destroyExisting;
            } else {
                System.out.println("Some other plugin is using dimensions' custom flags. This could cause errors. Please remove this plugin.");
            }
        }
	}
	
	public void enablePlatform() {
    	worldGuard = WorldGuard.getInstance().getPlatform();
	}
	
	public boolean testState(Player p, Location loc, Object flag) {
		if (!enabled || !(flag instanceof StateFlag) || worldGuard==null) return true;
		
        RegionContainer container = worldGuard.getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));
        
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);
        State ignite = set.queryValue(localPlayer, (StateFlag) flag);
        
        return ignite==State.ALLOW || worldGuard.getSessionManager().hasBypass(localPlayer, localPlayer.getWorld());
	}
 }