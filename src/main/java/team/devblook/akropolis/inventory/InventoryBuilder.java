/*
 * This file is part of Akropolis
 *
 * Copyright (c) 2024 DevBlook Team and others
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

package team.devblook.akropolis.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import team.devblook.akropolis.util.TextUtil;

import java.util.HashMap;
import java.util.Map;

public class InventoryBuilder implements InventoryHolder {
    private final Map<Integer, InventoryItem> icons;
    private int size;
    private final String title;

    public InventoryBuilder(int size, String title) {
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

    @SuppressWarnings("NullableProblems")
    public Inventory getInventory() {
        if (size > 54)
            size = 54;
        else if (size < 9)
            size = 9;

        Inventory inventory = Bukkit.createInventory(this, size, TextUtil.parse(title));
        for (Map.Entry<Integer, InventoryItem> entry : icons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
        }
        return inventory;
    }
}
