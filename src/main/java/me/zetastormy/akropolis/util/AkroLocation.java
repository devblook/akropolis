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

package me.zetastormy.akropolis.util;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.key.Key;

public record AkroLocation(
    @NotNull Key worldKey,
    double x,
    double y,
    double z,
    float yaw,
    float pitch
) {
    public static @NotNull AkroLocation fromBukkitLocation(final @NotNull Location bukkitLocation) {
        if (!bukkitLocation.isWorldLoaded()) {
            throw new IllegalArgumentException("The world is not present or loaded");
        }

        return new AkroLocation(
            bukkitLocation.getWorld().key(),
            bukkitLocation.getX(),
            bukkitLocation.getY(),
            bukkitLocation.getZ(),
            bukkitLocation.getYaw(),
            bukkitLocation.getPitch()
        );
    }

    /**
    * This should only be used when it's necessary to interact
    * with the Bukkit API.
    * The returned Bukkit's Location should not be stored long-term
    * to avoid problems when the world is unloaded.
    * @return a new Location instance representing the same location or null if the world doesn't exist
    */
    public @Nullable Location toBukkitLocation() {
        final @Nullable World world = Bukkit.getWorld(this.worldKey);
        if (world == null) {
            return null;
        }

        return new Location(world, this.x(), this.y(), this.z(), this.yaw(), this.pitch());
    }

    public int chunkX() {
        return (int) this.x >> 4;
    }

    public int chunkZ() {
        return (int) this.z >> 4;
    }

    public long chunkKey() {
        return Chunk.getChunkKey(this.chunkX(), this.chunkZ());
    }

    public boolean isWorldLoaded() {
        return this.getWorld() != null;
    }

    public boolean isChunkLoaded() {
        final World world = this.getWorld();
        return world != null && world.isChunkLoaded(this.chunkX(), this.chunkZ());
    }

    public @Nullable World getWorld() {
        return Bukkit.getWorld(this.worldKey);
    }

    public @NotNull AkroLocation add(
        double x,
        double y,
        double z,
        float yaw,
        float pitch
    ) {
        if (x == 0 && y == 0 && z == 0 && yaw == 0 && pitch == 0) return this;

        return new AkroLocation(
            this.worldKey(),
            this.x() + x,
            this.y() + y,
            this.z() + z,
            this.yaw() + yaw,
            this.pitch() + pitch
        );
    }

    public @NotNull AkroLocation add(
        double x,
        double y,
        double z
    ) {
        return this.add(x, y, z, 0, 0);
    }

    public @NotNull AkroLocation subtract(
        double x,
        double y,
        double z,
        float yaw,
        float pitch
    ) {
        if (x == 0 && y == 0 && z == 0 && yaw == 0 && pitch == 0) return this;

        return new AkroLocation(
            this.worldKey(),
            this.x() - x,
            this.y() - y,
            this.z() - z,
            this.yaw() - yaw,
            this.pitch() - pitch
        );
    }

    public @NotNull AkroLocation subtract(
        double x,
        double y,
        double z
    ) {
        return this.subtract(x, y, z, 0, 0);
    }

    @Override
    public String toString() {
        return "AkroLocation[world_key=" + this.worldKey().asString() + ", x=" + this.x() + ", y=" + this.y() + ", z=" + this.z() + ", yaw=" + this.yaw() + ", pitch=" + this.pitch() + "]";
    }
}
