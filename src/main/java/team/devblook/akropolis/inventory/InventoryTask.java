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
import org.bukkit.entity.Player;

import java.util.UUID;

public class InventoryTask implements Runnable {
    private final AbstractInventory inventory;

    InventoryTask(AbstractInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void run() {
        for (UUID uuid : inventory.getOpenInventories()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                inventory.refreshInventory(player, player.getOpenInventory().getTopInventory());
            }
        }
    }
}
