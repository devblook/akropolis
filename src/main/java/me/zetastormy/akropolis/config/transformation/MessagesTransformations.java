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

import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.TransformAction;
import org.spongepowered.configurate.util.NamingSchemes;

public class MessagesTransformations extends AbstractTransformation {
    public static final int LATEST_VERSION = 0;

    protected ConfigurationTransformation.Versioned create() {
        return this.defaultBuilder()
                .addVersion(LATEST_VERSION, initialTransform())
                .build();
    }

    private static ConfigurationTransformation initialTransform() {
        final TransformAction coerce = (path, value) -> {
            final Object[] arr = path.array();
            arr[arr.length-1] = NamingSchemes.SNAKE_CASE.coerce((String) arr[arr.length-1]);
            return arr;
        };

        final TransformAction moveOutAndCoerce = (path, value) -> {
            final Object[] arr = path.array();
            final String name = (String) arr[arr.length-1];
            return new Object[]{NamingSchemes.SNAKE_CASE.coerce(name)};
        };

        return ConfigurationTransformation.builder()
                .addAction(path("Messages", ConfigurationTransformation.WILDCARD_OBJECT), moveOutAndCoerce)
                .addAction(path(ConfigurationTransformation.WILDCARD_OBJECT, ConfigurationTransformation.WILDCARD_OBJECT), coerce)
                .build();
    }
}
