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

import me.zetastormy.akropolis.config.ConfigManager;
import me.zetastormy.akropolis.config.type.Messages;
import me.zetastormy.akropolis.util.MessagingUtil;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    private final ConfigManager configManager;

    public InventoryListener(final ConfigManager configManager) {
        this.configManager = configManager;
    }

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

                if (item.getPermission() != null && !player.hasPermission(item.getPermission())) {
                    final Messages messages = this.configManager.getFile(Messages.class).getConfig();
                    MessagingUtil.send(messages.general().noPermission(), player);
                    return;
                }

                for (final ClickAction clickAction : item.getClickActions()) {
                    clickAction.execute(player);
                }
            }
        }
    }
}
