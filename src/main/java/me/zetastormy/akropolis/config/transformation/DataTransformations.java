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

package me.zetastormy.akropolis.config.transformation;

import static org.spongepowered.configurate.NodePath.path;
import static org.spongepowered.configurate.transformation.ConfigurationTransformation.WILDCARD_OBJECT;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.MoveStrategy;

import net.kyori.adventure.key.Key;

public class DataTransformations extends AbstractTransformation {
    public static final int LATEST_VERSION = 0;
    private final Logger logger;

    public DataTransformations(final Logger logger) {
        this.logger = logger;
    }

    protected ConfigurationTransformation.Versioned create() {
        return this.defaultBuilder()
                .addVersion(0, initialTransform())
                .build();
    }

    private Object[] migrateLocation(
        final NodePath locationPath,
        final ConfigurationNode locationNode
    ) throws ConfigurateException {
        if (locationNode.virtual() || locationNode.empty()) {
            this.logger.warn(
                "Could not migrate location in key '{}' because the node is virtual or empty",
                Arrays.toString(locationPath.array())
            );
            return null;
        }

        if (locationNode.hasChild("world_key")) {
            if (locationNode.hasChild("world")) {
                this.logger.warn(
                    "Location in path '{}' has both 'world_key' and 'world' keys",
                    Arrays.toString(locationPath.array())
                );
            }
            return null;
        } else if (locationNode.hasChild("world")) {
            final ConfigurationNode worldNode = locationNode.node("world");
            final String worldName = worldNode.getString();

            final ConfigurationNode classNode = locationNode.node("==");
            if (!classNode.virtual() && !classNode.isNull()) {
                final String className = classNode.getString();
                if (!className.equals("org.bukkit.Location")) {
                    this.logger.warn(
                        "Unknown serialized location class name '{}' in path '{}'",
                        className,
                        Arrays.toString(locationPath.array())
                    );
                }
                this.logger.info(
                    "Removing serialized class name '{}' from location in path '{}'",
                    className,
                    Arrays.toString(locationPath.array())
                );
                classNode.raw(null);
            }

            if (worldName == null) {
                this.logger.warn(
                    "Skipping location migration of path '{}' because world name is null",
                    Arrays.toString(locationPath.array())
                );
                return null;
            }

            final @Nullable World world = Bukkit.getWorld(worldName);
            if (world == null) {
                throw new ConfigurateException(
                    locationNode,
                    "Couldn't migrate location with world name '" + worldName +
                    "' because the world does not exist or is not loaded, aborting migration. " +
                    "Make sure all worlds used by the plugin are loaded on startup and try again. "
                );
            }
            final Key worldKey = world.key();

            this.logger.info(
                "Found legacy 'world' key with value '{}' in location path '{}', migrating to '{}'",
                worldName,
                Arrays.toString(locationPath.array()),
                worldKey.asString()
            );

            final ConfigurationNode worldKeyNode = locationNode.node("world_key");
            worldKeyNode.set(worldKey.asString());
            worldNode.raw(null);
        }
        return null;
    }

    private ConfigurationTransformation initialTransform() {
        return ConfigurationTransformation.builder().moveStrategy(MoveStrategy.OVERWRITE)
        .addAction(path("spawn"), (path, value) -> {
            this.logger.info("Migrating spawn location...");
            return this.migrateLocation(path, value);
        })
        .addAction(path("holograms", WILDCARD_OBJECT), (path, value) -> {
            this.logger.info(
                "Migrating location of hologram key '{}'...",
                Arrays.toString(path.array())
            );
            return this.migrateLocation(path, value.node("location"));
        })
        .addAction(path("song_player"), (path, value) -> {
            this.logger.info("Migrating location of song player...");
            return this.migrateLocation(path, value.node("location"));
        })
        .build();
    }
}
