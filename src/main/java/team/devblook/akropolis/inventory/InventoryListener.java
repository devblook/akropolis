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

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof InventoryBuilder) {
            event.setCancelled(true);

            if (event.getWhoClicked() instanceof Player player) {

                ItemStack itemStack = event.getCurrentItem();

                if (itemStack == null || itemStack.getType() == Material.AIR)
                    return;

                InventoryBuilder customHolder = (InventoryBuilder) event.getView().getTopInventory().getHolder();
                InventoryItem item = customHolder.getIcon(event.getRawSlot());

                if (item == null)
                    return;

                for (final ClickAction clickAction : item.getClickActions()) {
                    clickAction.execute(player);
                }
            }
        }
    }
}
