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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import me.xxastaspastaxx.dimensions.DimensionsDebbuger;

public class WrapperPlayServerEntityMetadata extends AbstractPacket {
	public static final PacketType TYPE =
			PacketType.Play.Server.ENTITY_METADATA;

	public WrapperPlayServerEntityMetadata() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerEntityMetadata(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve Entity ID.
	 * <p>
	 * Notes: entity's ID
	 * 
	 * @return The current Entity ID
	 */
	public int getEntityID() {
		return handle.getIntegers().read(0);
	}

	/**
	 * Set Entity ID.
	 * 
	 * @param value - new value.
	 */
	public void setEntityID(int value) {
		handle.getIntegers().write(0, value);
	}

	/**
	 * Retrieve the entity of the painting that will be spawned.
	 * 
	 * @param world - the current world of the entity.
	 * @return The spawned entity.
	 */
	public Entity getEntity(World world) {
		return handle.getEntityModifier(world).read(0);
	}

	/**
	 * Retrieve the entity of the painting that will be spawned.
	 * 
	 * @param event - the packet event.
	 * @return The spawned entity.
	 */
	public Entity getEntity(PacketEvent event) {
		return getEntity(event.getPlayer().getWorld());
	}

	/**
	 * Retrieve Metadata.
	 * 
	 * @return The current Metadata
	 */
	public List<WrappedWatchableObject> getMetadata() {
		return handle.getWatchableCollectionModifier().read(0);
	}

	/**
	 * Set Metadata.
	 * 
	 * @param value - new value.
	 */
	public void setMetadata(List<WrappedWatchableObject> value) {
		
		try {
			Class.forName("com.comphenix.protocol.wrappers.WrappedDataValue");
			final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();

			for(final WrappedWatchableObject entry : value) {
				if(entry == null) continue;

				final WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
				wrappedDataValueList.add(
						new WrappedDataValue(
								watcherObject.getIndex(),
								watcherObject.getSerializer(),
								entry.getRawValue()
								)
						);
			}

			handle.getDataValueCollectionModifier().write(0, wrappedDataValueList);
			
//			final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();
//			value.forEach(entry -> {
//	       		 final WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
//	       		 wrappedDataValueList.add(new WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(), entry.getRawValue()));
//	       	 });
//	       	 handle.getDataValueCollectionModifier().write(0, wrappedDataValueList);
		} catch (FieldAccessException | ClassNotFoundException | NullPointerException e) {
			DimensionsDebbuger.DEBUG.print("tttt");
			handle.getWatchableCollectionModifier().write(0, value);
		}
	}
}