/*
 * This file is part of Akropolis
 *
 * Copyright (c) 2025 DevBlook Team and others
 *
 * Akropolis free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akropolis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akropolis. If not, see <http://www.gnu.org/licenses/>.
 */

package me.zetastormy.akropolis.config.serializer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

public class LocationSerializer implements TypeSerializer<Location> {
    public static final LocationSerializer INSTANCE = new LocationSerializer();

    private ConfigurationNode nonVirtualNode(
            final ConfigurationNode source,
            final Object... path
    ) throws SerializationException {
        if (!source.hasChild(path)) {
            throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node.");
        }
        return source.node(path);
    }

    @Override
    public Location deserialize(
            final @NotNull Type type,
            final @NotNull ConfigurationNode source
    ) throws SerializationException {
        final @Nullable String worldKey = source.node("world_key").getString();
        final @Nullable String worldName = source.node("world").getString();

        final double x = this.nonVirtualNode(source, "x").getDouble();
        final double y = this.nonVirtualNode(source, "y").getDouble();
        final double z = this.nonVirtualNode(source, "z").getDouble();
        final float yaw = this.nonVirtualNode(source, "yaw").getFloat();
        final float pitch = this.nonVirtualNode(source, "pitch").getFloat();

        World world = null;
        if (worldKey != null) {
            final @Nullable NamespacedKey key = NamespacedKey.fromString(worldKey);
            if (key == null) {
                throw new SerializationException("World key " + worldKey + " is invalid");
            } else {
                world = Bukkit.getWorld(key);
            }
        } else if (worldName != null) {
            world = Bukkit.getWorld(worldName);
        } else {
            throw new SerializationException("Location missing both 'world' and 'world_key' fields");
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public void serialize(
            final @NotNull Type type,
            final @Nullable Location loc,
            final @NotNull ConfigurationNode target
    ) throws SerializationException {
        if (loc == null) {
            target.raw(null);
            return;
        }

        target.node("world_key").set(loc.getWorld().key().asString());
        target.node("x").set(loc.getX());
        target.node("y").set(loc.getY());
        target.node("z").set(loc.getZ());
        target.node("yaw").set(loc.getYaw());
        target.node("pitch").set(loc.getPitch());
    }
}
