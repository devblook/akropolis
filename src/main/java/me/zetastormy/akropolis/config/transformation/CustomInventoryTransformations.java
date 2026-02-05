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

import org.spongepowered.configurate.transformation.ConfigurationTransformation;

public class CustomInventoryTransformations extends AbstractTransformation {
    public static final int LATEST_VERSION = 0;


    protected ConfigurationTransformation.Versioned create() {
        return this.defaultBuilder()
                .addVersion(LATEST_VERSION, initialTransform())
                .build();
    }

    private static ConfigurationTransformation initialTransform() {
        return ConfigurationTransformation.builder().build();
    }
}
