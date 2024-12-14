package me.xxastaspastaxx.dimensions.addons.patreoncosmetics.cosmetics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;

public class CosmeticEffectManager {
	
	public void play(CompletePortal completePortal, Player p, CosmeticEffect effect) {
		
		Location location = completePortal.getCenter();
		
		switch (effect) {
		
		case FINAL_SPARK:
			Particle fireworkParticle = Particle.valueOf("FIREWORKS_SPARK") == null ? Particle.valueOf("ELECTRIC_SPARK") : Particle.valueOf("FIREWORKS_SPARK");
			location.getWorld().spawnParticle(fireworkParticle, location, 100);
			location.getWorld().playSound(location, Sound.ENTITY_ENDER_DRAGON_HURT, 1, 1);
			break;
		
		case FILLING_THE_VOID:
			
			Particle smokeParticle = Particle.valueOf("SMOKE_NORMAL") == null ? Particle.valueOf("SMOKE") : Particle.valueOf("SMOKE_NORMAL");
			
			int amount = 200;
			double radius = 10;
			double increment = (2 * Math.PI) / amount;
	        for(int i = 0;i < amount; i++)
	        {
	            double angle = i * increment;
	            double x = location.getX() + (radius * Math.cos(angle));
	            double z = location.getZ() + (radius * Math.sin(angle));
	            Location loc = new Location(location.getWorld(), x, location.getY()+DimensionsUtils.getRandom(-5, 5), z);
	            Vector v = genVec(loc,location);
	            location.getWorld().spawnParticle(smokeParticle, loc, 0, v.getX(),v.getY(),v.getZ());
	        }
			
			location.getWorld().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
			break;
			
		case HEART_SEEKER:
			boolean zAxis = completePortal.getPortalGeometry().iszAxis();
			Location loc1 = location.clone().add(zAxis?0.5:0,0,zAxis?0:0.5);
			Location loc2 = location.clone().add(zAxis?-0.5:0,0,zAxis?0:-0.5);
			for (int j=0;j<3;j++) {
				final int i = j;
				Bukkit.getScheduler().scheduleSyncDelayedTask(Dimensions.getInstance(), new Runnable() {
					
					@Override
					public void run() {
						location.getWorld().spawnParticle(Particle.HEART, loc1, (int) (7*i),i/3,i/3,i/3);
						location.getWorld().spawnParticle(Particle.HEART, loc2, (int) (7*i),i/3,i/3,i/3);
						location.getWorld().playSound(location, Sound.ENTITY_CHICKEN_EGG, 3, (float) (i/3));
					}
				}, i*2);
			}
	        
			break;
			
		case HUNGRY_HOUNDS:
			location.getWorld().playSound(location, Sound.ENTITY_WOLF_GROWL, 0.2f, 1);
			location.getWorld().playSound(location, Sound.ENTITY_SHEEP_HURT, 0.2f, 0.8f);
			break;
			
		case GLOWING_AURA:
			location.getWorld().spawnParticle(Particle.GLOW, location, 50, 0.5,1,0.5);
			break;
			
		case ANGRY_LLAMA:
			Vector v = genVec(location,p.getEyeLocation());
			location.getWorld().spawnParticle(Particle.SPIT, location, 0, v.getX(),v.getY(),v.getZ());
			location.getWorld().playSound(location, Sound.ENTITY_LLAMA_SPIT, 0.2f, 1);
			break;
			
		case EXPLOSIONS:
			Particle explosionParticle = Particle.valueOf("EXPLOSION_LARGE") == null ? Particle.valueOf("EXPLOSION") : Particle.valueOf("EXPLOSION_LARGE");
            location.getWorld().spawnParticle(explosionParticle, location, 5,0.5,1,0.5);
			location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.2f, 1);
			break;
		
		case LIL_RING:
			

			for (int i2=0;i2<27;i2++) {
				int amount1 = 10;
				double radius1 = 2;
				double increment1 = (2 * Math.PI) / amount1;
						
				Location tempLoc = completePortal.getCenter();
						
				for(int i = 0;i < amount1; i++) {
					double angle = i * increment1;
				    double x = tempLoc.getX() + (radius1 * Math.cos(angle));
				    double z = tempLoc.getZ() + (radius1 * Math.sin(angle));
				    double a = i2%4==0?(Math.cos(angle)):i2%3==0?(-Math.sin(angle)):i2%2==0?(-Math.cos(angle)):(Math.sin(angle));
				    Location loc = new Location(tempLoc.getWorld(), x, tempLoc.getY()+1+((a/10)*i2%4), z);
				    tempLoc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 0, 0,Math.random()<0.5?-1:0,0);
				}
			}
			break;
			
		default:
			break;
		
		
		}
		
	}
	
    public static Vector genVec(Location a, Location b) {
        double dX = a.getX() - b.getX();
        double dY = a.getY() - b.getY();
        double dZ = a.getZ() - b.getZ();
        double yaw = Math.atan2(dZ, dX);
        double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        Vector vector = new Vector(x, z, y);

        return vector;
    }
	
}
