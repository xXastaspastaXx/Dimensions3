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

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.reflect.FieldAccessException;

public class WrapperPlayServerSpawnEntity extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY;

	private static PacketConstructor entityConstructor;


	public WrapperPlayServerSpawnEntity() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerSpawnEntity(PacketContainer packet) {
		super(packet, TYPE);
	}

	public WrapperPlayServerSpawnEntity(Entity entity, int type, int objectData) {
		super(fromEntity(entity, type, objectData), TYPE);
	}

	// Useful constructor
	private static PacketContainer fromEntity(Entity entity, int type,
			int objectData) {
		if (entityConstructor == null)
			entityConstructor =
					ProtocolLibrary.getProtocolManager()
							.createPacketConstructor(TYPE, entity, type,
									objectData);
		return entityConstructor.createPacket(entity, type, objectData);
	}

	/**
	 * Retrieve entity ID of the Object.
	 * 
	 * @return The current EID
	 */
	public int getEntityID() {
		return handle.getIntegers().read(0);
	}

	/**
	 * Retrieve the entity that will be spawned.
	 * 
	 * @param world - the current world of the entity.
	 * @return The spawned entity.
	 */
	public Entity getEntity(World world) {
		return handle.getEntityModifier(world).read(0);
	}

	/**
	 * Retrieve the entity that will be spawned.
	 * 
	 * @param event - the packet event.
	 * @return The spawned entity.
	 */
	public Entity getEntity(PacketEvent event) {
		return getEntity(event.getPlayer().getWorld());
	}

	/**
	 * Set entity ID of the Object.
	 * 
	 * @param value - new value.
	 */
	public void setEntityID(int value) {
		handle.getIntegers().write(0, value);
	}

	public UUID getUniqueId() {
		return handle.getUUIDs().read(0);
	}

	public void setUniqueId(UUID value) {
		handle.getUUIDs().write(0, value);
	}

	/**
	 * Retrieve the x position of the object.
	 * <p>
	 * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
	 * 
	 * @return The current X
	 */
	public double getX() {
		return handle.getDoubles().read(0);
	}

	/**
	 * Set the x position of the object.
	 * 
	 * @param value - new value.
	 */
	public void setX(double value) {
		handle.getDoubles().write(0, value);
	}

	/**
	 * Retrieve the y position of the object.
	 * <p>
	 * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
	 * 
	 * @return The current y
	 */
	public double getY() {
		return handle.getDoubles().read(1);
	}

	/**
	 * Set the y position of the object.
	 * 
	 * @param value - new value.
	 */
	public void setY(double value) {
		handle.getDoubles().write(1, value);
	}

	/**
	 * Retrieve the z position of the object.
	 * <p>
	 * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
	 * 
	 * @return The current z
	 */
	public double getZ() {
		return handle.getDoubles().read(2);
	}

	/**
	 * Set the z position of the object.
	 * 
	 * @param value - new value.
	 */
	public void setZ(double value) {
		handle.getDoubles().write(2, value);
	}

	/**
	 * Retrieve the optional speed x.
	 * <p>
	 * This is ignored if {@link #getObjectData()} is zero.
	 * 
	 * @return The optional speed x.
	 */
	public double getOptionalSpeedX() {
		return handle.getIntegers().read(1) / 8000.0D;
	}

	/**
	 * Set the optional speed x.
	 * 
	 * @param value - new value.
	 */
	public void setOptionalSpeedX(double value) {
		handle.getIntegers().write(1, (int) (value * 8000.0D));
	}

	/**
	 * Retrieve the optional speed y.
	 * <p>
	 * This is ignored if {@link #getObjectData()} is zero.
	 * 
	 * @return The optional speed y.
	 */
	public double getOptionalSpeedY() {
		return handle.getIntegers().read(2) / 8000.0D;
	}

	/**
	 * Set the optional speed y.
	 * 
	 * @param value - new value.
	 */
	public void setOptionalSpeedY(double value) {
		handle.getIntegers().write(2, (int) (value * 8000.0D));
	}

	/**
	 * Retrieve the optional speed z.
	 * <p>
	 * This is ignored if {@link #getObjectData()} is zero.
	 * 
	 * @return The optional speed z.
	 */
	public double getOptionalSpeedZ() {
		return handle.getIntegers().read(3) / 8000.0D;
	}

	/**
	 * Set the optional speed z.
	 * 
	 * @param value - new value.
	 */
	public void setOptionalSpeedZ(double value) {
		handle.getIntegers().write(3, (int) (value * 8000.0D));
	}

	/**
	 * Retrieve the pitch.
	 * 
	 * @return The current pitch.
	 */
	public float getPitch() {
		return (handle.getIntegers().read(4) * 360.F) / 256.0F;
	}

	/**
	 * Set the pitch.
	 * 
	 * @param value - new pitch.
	 */
	public void setPitch(float value) {
		handle.getIntegers().write(4, (int) (value * 256.0F / 360.0F));
	}

	/**
	 * Retrieve the yaw.
	 * 
	 * @return The current Yaw
	 */
	public float getYaw() {
		return (handle.getIntegers().read(5) * 360.F) / 256.0F;
	}

	/**
	 * Set the yaw of the object spawned.
	 * 
	 * @param value - new yaw.
	 */
	public void setYaw(float value) {
		handle.getIntegers().write(5, (int) (value * 256.0F / 360.0F));
	}

	/**
	 * Retrieve the type of object. See {@link ObjectTypes}
	 * 
	 * @return The current Type
	 */
	public EntityType getType() {
		return handle.getEntityTypeModifier().read(0);
	}

	/**
	 * Set the type of object. See {@link ObjectTypes}.
	 * 
	 * @param value - new value.
	 */
	public void setTypeFallingBlock(int combinedID) {
		
		try {
			handle.getEntityTypeModifier().write(0, EntityType.FALLING_BLOCK);
			handle.getIntegers().write(6, combinedID);
		} catch (FieldAccessException e) {
			try {
				handle.getIntegers().write(6, 70);
				handle.getIntegers().write(7, combinedID);
			} catch (FieldAccessException e1) {
				handle.getIntegers().write(3, 70);
				handle.getIntegers().write(4, combinedID);
			}
			
		}
		
		
	}

	/**
	 * Retrieve object data.
	 * <p>
	 * The content depends on the object type:
	 * <table border="1" cellpadding="4">
	 * <tr>
	 * <th>Object Type:</th>
	 * <th>Name:</th>
	 * <th>Description</th>
	 * </tr>
	 * <tr>
	 * <td>ITEM_FRAME</td>
	 * <td>Orientation</td>
	 * <td>0-3: South, West, North, East</td>
	 * </tr>
	 * <tr>
	 * <td>FALLING_BLOCK</td>
	 * <td>Block Type</td>
	 * <td>BlockID | (Metadata << 0xC)</td>
	 * </tr>
	 * <tr>
	 * <td>Projectiles</td>
	 * <td>Entity ID</td>
	 * <td>The entity ID of the thrower</td>
	 * </tr>
	 * <tr>
	 * <td>Splash Potions</td>
	 * <td>Data Value</td>
	 * <td>Potion data value.</td>
	 * </tr>
	 * </table>
	 * 
	 * @return The current object Data
	 */
	public int getObjectData() {
		return handle.getIntegers().read(6);
	}

	/**
	 * Set object Data.
	 * <p>
	 * The content depends on the object type. See {@link #getObjectData()} for
	 * more information.
	 * 
	 * @param value - new object data.
	 */
	public void setObjectData(int value) {
		handle.getIntegers().write(6, value);
	}
}