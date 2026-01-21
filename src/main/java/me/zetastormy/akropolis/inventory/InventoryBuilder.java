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

package me.zetastormy.akropolis.inventory;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import me.zetastormy.akropolis.util.text.PlaceholderUtil;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

public class InventoryBuilder implements InventoryHolder {
    private final Logger logger;
    private final String inventoryName;
    private final Map<Integer, InventoryItem> icons;
    private int size;
    private final String title;

    public InventoryBuilder(
            final @NotNull Logger logger,
            final @NotNull String inventoryName,
            final int size,
            final @NotNull String title
    ) {
        this.logger = logger;
        this.inventoryName = inventoryName;
        this.icons = new HashMap<>();
        this.size = size;
        this.title = title;
    }

    public void setItem(int slot, InventoryItem item) {
        icons.put(slot, item);
    }

    public InventoryItem getIcon(final int slot) {
        return icons.get(slot);
    }


    public @NonNull Inventory getInventory() {
        if (this.size > 54) {
            logger.warn(
                    "Configured inventory size '{}' for menu '{}' is greater than 54, using closest value 54",
                    this.size,
                    this.inventoryName
            );
            this.size = 54;
        }
        else if (this.size < 9) {
            logger.warn(
                    "Configured inventory size '{}' for menu '{}' is lower than 9, using closest value 9",
                    this.size,
                    this.inventoryName
            );
            this.size = 9;
        } else if (this.size % 9 != 0) {
            int newSize = 9 * Math.toIntExact(Math.round(this.size / 9.0));
            logger.warn(
                    "Configured inventory size '{}' for menu '{}' is not a multiple of 9, using closest value '{}'",
                    this.size,
                    this.inventoryName,
                    newSize
            );
            this.size = newSize;
        }

        Inventory inventory = Bukkit.createInventory(this, size, PlaceholderUtil.setPlaceholders(title));
        for (Map.Entry<Integer, InventoryItem> entry : icons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
        }
        return inventory;
    }
}
