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

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

public abstract class AbstractTransformation {

    public static final String VERSION_KEY = "config_version";
    public static final String VERSION_COMMENT = "DO NOT EDIT THIS VALUE or your configuration will break and you will lose data!";
    protected final ConfigurationTransformation.Versioned transformation;

    protected AbstractTransformation() {
        this.transformation = this.create();
    }

    public <N extends ConfigurationNode> int version(final N node) {
        return this.transformation.version(node);
    }

    public <N extends ConfigurationNode> N updateNode(final N node) throws ConfigurateException {
        if (!node.virtual()) {
            this.transformation.apply(node);
        }

        return node;
    }

    protected abstract ConfigurationTransformation.Versioned create();

    protected ConfigurationTransformation.VersionedBuilder defaultBuilder() {
        return ConfigurationTransformation.versionedBuilder().versionKey(VERSION_KEY);
    }
}
