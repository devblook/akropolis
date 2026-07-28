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

import java.lang.reflect.Type;
import java.util.Arrays;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import me.zetastormy.akropolis.util.AkroLocation;
import net.kyori.adventure.key.Key;

public class AkroLocationSerializer implements TypeSerializer<AkroLocation> {
    public static final AkroLocationSerializer INSTANCE = new AkroLocationSerializer();

    private ConfigurationNode getNode(
                final ConfigurationNode source,
                final Object... path
        ) throws SerializationException {
            if (!source.hasChild(path)) {
                throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node.");
            }
            if (source.node(path).empty()) {
                throw new SerializationException("Required field " + Arrays.toString(path) + " is empty");
            }
            return source.node(path);
        }

    @Override
    public AkroLocation deserialize(
        final Type type,
        final ConfigurationNode source
    ) throws SerializationException {
        final @Nullable String worldKey = this.getNode(source, "world_key").getString();
        if (worldKey == null) {
            throw new SerializationException("Can't get String from location key 'world_key'");
        }
        final double x = this.getNode(source, "x").getDouble();
        final double y = this.getNode(source, "y").getDouble();
        final double z = this.getNode(source, "z").getDouble();
        final float yaw = this.getNode(source, "yaw").getFloat();
        final float pitch = this.getNode(source, "pitch").getFloat();

        return new AkroLocation(Key.key(worldKey), x, y, z, yaw, pitch);
    }

    @Override
    public void serialize(
        final Type type,
        final @Nullable AkroLocation location,
        final ConfigurationNode target
    ) throws SerializationException {
        if (location == null) {
            target.raw(null);
            return;
        }

        target.node("world_key").set(location.worldKey().asString());
        target.node("x").set(location.x());
        target.node("y").set(location.y());
        target.node("z").set(location.z());
        target.node("yaw").set(location.yaw());
        target.node("pitch").set(location.pitch());
    }
}
