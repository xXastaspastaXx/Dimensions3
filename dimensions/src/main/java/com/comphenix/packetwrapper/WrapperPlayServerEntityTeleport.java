/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.comphenix.packetwrapper;

import java.lang.reflect.Constructor;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftReflection;

public class WrapperPlayServerEntityTeleport extends AbstractPacket {
	public static final PacketType TYPE =
			PacketType.Play.Server.ENTITY_TELEPORT;

	public WrapperPlayServerEntityTeleport() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerEntityTeleport(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve entity ID.
	 * 
	 * @return The current EID
	 */
	public int getEntityID() {
		return handle.getIntegers().read(0);
	}

	/**
	 * Set entity ID.
	 * 
	 * @param value - new value.
	 */
	public void setEntityID(int value) {
		handle.getIntegers().write(0, value);
	}

	/**
	 * Retrieve the entity.
	 * 
	 * @param world - the current world of the entity.
	 * @return The entity.
	 */
	public Entity getEntity(World world) {
		return handle.getEntityModifier(world).read(0);
	}

	/**
	 * Retrieve the entity.
	 * 
	 * @param event - the packet event.
	 * @return The entity.
	 */
	public Entity getEntity(PacketEvent event) {
		return getEntity(event.getPlayer().getWorld());
	}

	public double getX() {
		return handle.getDoubles().read(0);
	}

	public void setX(double value) {
		handle.getDoubles().write(0, value);
	}

	public double getY() {
		return handle.getDoubles().read(1);
	}

	public void setY(double value) {
		handle.getDoubles().write(1, value);
	}

	public double getZ() {
		return handle.getDoubles().read(2);
	}

	public void setZ(double value) {
		handle.getDoubles().write(2, value);
	}

	/**
	 * Retrieve the yaw of the current entity.
	 * 
	 * @return The current Yaw
	 */
	public float getYaw() {
		return (handle.getBytes().read(0) * 360.F) / 256.0F;
	}

	/**
	 * Set the yaw of the current entity.
	 * 
	 * @param value - new yaw.
	 */
	public void setYaw(float value) {
		handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
	}

	/**
	 * Retrieve the pitch of the current entity.
	 * 
	 * @return The current pitch
	 */
	public float getPitch() {
		return (handle.getBytes().read(1) * 360.F) / 256.0F;
	}

	/**
	 * Set the pitch of the current entity.
	 * 
	 * @param value - new pitch.
	 */
	public void setPitch(float value) {
		handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
	}

	public boolean getOnGround() {
		return handle.getBooleans().read(0);
	}

	public void setOnGround(boolean value) {
		handle.getBooleans().write(0, value);
	}

	public void setLocation(double x, double y, double z) {
			
		try {
		    // Step 1: Retrieve the Vec3D class
		    Class<?> vec3DClass = MinecraftReflection.getVec3DClass();

		    // Step 2: Retrieve the Vec3D constructor
		    Constructor<?> vec3DConstructor = vec3DClass.getDeclaredConstructor(double.class, double.class, double.class);
		    vec3DConstructor.setAccessible(true); // Make the constructor accessible if private

		    // Step 3: Create a new Vec3D instance with (x, y, z)
		    Object vec3DInstance = vec3DConstructor.newInstance(x, y, z);

		    // Step 4: Retrieve the PositionMoveRotation class
		    Class<?> positionMoveRotationClass = MinecraftReflection.getMinecraftClass("world.entity.PositionMoveRotation");

		    // Step 5: Retrieve the PositionMoveRotation constructor
		    Constructor<?> positionMoveRotationConstructor = positionMoveRotationClass.getDeclaredConstructor(
		        vec3DClass, vec3DClass, float.class, float.class
		    );
		    positionMoveRotationConstructor.setAccessible(true);

		    // Step 6: Create a PositionMoveRotation instance (we'll use default values for the Vec3D for the second one)
		    Object defaultVec3D = vec3DConstructor.newInstance(0, 0, 0); // Create another Vec3D instance (0, 0, 0)
		    float value2 = 0f; // Example value for the second float parameter
		    float value3 = 0f; // Example value for the third float parameter
		    Object positionMoveRotationInstance = positionMoveRotationConstructor.newInstance(
		        vec3DInstance, defaultVec3D, value2, value3
		    );

		    handle.getModifier().write(1, positionMoveRotationInstance);
		} catch (Exception e) {
		    e.printStackTrace();
			Integer.parseInt("error");
		}
		
	}
}