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

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.MoveStrategy;

public class SettingsTransformations extends AbstractTransformation {
    public static final int LATEST_VERSION = 0;

    private final Logger logger;

    public SettingsTransformations(
        final @NotNull Logger logger
    ) {
        this.logger = logger;
    }

    protected ConfigurationTransformation.Versioned create() {
        return this.defaultBuilder()
                .addVersion(LATEST_VERSION, this.initialTransform())
                .build();
    }

    private @Nullable Object[] migrateStringToList(final NodePath path, final ConfigurationNode value) throws ConfigurateException {
        if (value.virtual() || value.isNull()) {
            this.logger.info("Skipping migration of key {} because the node is virtual or null", Arrays.toString(path.array()));
        }
        if (!value.isList()) {
            final @Nullable String originalValue = value.getString();
            if (originalValue != null) {
                value.set(List.of(originalValue));
            } else {
                value.set(List.of());
            }

            return null;
        } else {
            return null;
        }
    }

    private ConfigurationTransformation initialTransform() {
        return ConfigurationTransformation.builder().moveStrategy(MoveStrategy.OVERWRITE)
                .addAction(path("disabled-worlds"), (path, value) -> {
                    logger.info("Migrating disabled worlds...");
                    return new Object[]{"disabled_worlds"};
                })
                .addAction(path("groups"), (path, value) -> {
                    logger.info("Migrating groups...");

                    if (value.virtual() || value.isNull()) {
                        logger.warn("Skipping virtual or null groups section migration");
                        return null;
                    }

                    if (!value.isMap()) {
                        logger.warn("Skipping groups section migration because it's not a map");
                    }

                    final ConfigurationNode old = value.copy();
                    old.childrenMap().forEach((oldKey, oldNode) -> {
                        if (!oldKey.equals("enabled")) {
                            value.node("groups").node(oldKey).from(oldNode);
                            value.node(oldKey).raw(null);
                        }
                    });

                    return new Object[]{"chat_management"};
                })
                .build();
    }
}
