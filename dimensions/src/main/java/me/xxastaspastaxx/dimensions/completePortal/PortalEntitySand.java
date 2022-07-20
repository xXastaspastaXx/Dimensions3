package me.xxastaspastaxx.dimensions.completePortal;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class PortalEntitySand extends PortalEntity {

	private int fallingBlockId;
	
	private WrapperPlayServerSpawnEntity spawnPacket;
	private WrapperPlayServerEntityTeleport teleportPacket;
	private WrapperPlayServerEntityMetadata metaPacket;
	private WrappedDataWatcher dataWatcher;
	private WrapperPlayServerEntityDestroy destroyPacket;
	
	public PortalEntitySand(Location location, int combinedID) {
		super(location);
		fallingBlockId =  (int) (Math.random() * Integer.MAX_VALUE);
		
		spawnPacket = new WrapperPlayServerSpawnEntity();
		
		spawnPacket.setEntityID(fallingBlockId);
		spawnPacket.setUniqueId(UUID.randomUUID());

		spawnPacket.setTypeFallingBlock(combinedID);
		
		/*try {
			spawnPacket.setType(EntityType.FALLING_BLOCK);
			spawnPacket.setObjectData(combinedID);
		} catch (FieldAccessException e) {
			try {
				spawnPacket.getHandle().getIntegers().write(6, 70);
				spawnPacket.getHandle().getIntegers().write(7, combinedID);
			} catch (FieldAccessException e1) {
				spawnPacket.getHandle().getIntegers().write(3, 70);
				spawnPacket.getHandle().getIntegers().write(4, combinedID);
			}
			
		}*/
		
		spawnPacket.setX(location.getX());
		spawnPacket.setY(location.getY());
		spawnPacket.setZ(location.getZ());
		
		metaPacket = new WrapperPlayServerEntityMetadata();
		metaPacket.setEntityID(fallingBlockId);
		dataWatcher = new WrappedDataWatcher();
		
		WrappedDataWatcher.WrappedDataWatcherObject noGravity = new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class));
		WrappedDataWatcher.WrappedDataWatcherObject ticksLived = new WrappedDataWatcher.WrappedDataWatcherObject(1, WrappedDataWatcher.Registry.get(Integer.class));

		dataWatcher.setObject(noGravity, true);
		dataWatcher.setObject(ticksLived, Integer.MAX_VALUE);
		
		metaPacket.setMetadata(dataWatcher.getWatchableObjects());
		
		teleportPacket = new WrapperPlayServerEntityTeleport();
		teleportPacket.setEntityID(fallingBlockId);
		teleportPacket.setX(location.getX()+0.5f);
		teleportPacket.setY(location.getY());
		teleportPacket.setZ(location.getZ()+0.5f);
		
		destroyPacket = new WrapperPlayServerEntityDestroy();
		if (destroyPacket.getHandle().getIntegerArrays().size()==1)
			destroyPacket.getHandle().getIntegerArrays().write(0, new int[] {fallingBlockId});
		else if (destroyPacket.getHandle().getIntegers().size()==1)
			destroyPacket.getHandle().getIntegers().write(0, fallingBlockId);
		else
			destroyPacket.setEntityIds(fallingBlockId);
	}

	public void summon(Player p) {
		spawnPacket.sendPacket(p);
		teleportPacket.sendPacket(p);
		metaPacket.sendPacket(p);
	}
	
	public void destroy(Player p) {
		destroyPacket.sendPacket(p);
	}

	public void destroyBroadcast() {
		destroyPacket.broadcastPacket();
	}
	
}
