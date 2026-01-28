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

package me.zetastormy.akropolis.module.modules.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;

public class PlayerOffHandSwap extends Module {

    public PlayerOffHandSwap(AkropolisPlugin plugin) {
        super(plugin, ModuleType.PLAYER_OFFHAND_LISTENER);
    }

    @EventHandler
    public void onPlayerSwapItem(PlayerSwapHandItemsEvent event) {
        if (inDisabledWorld(event.getPlayer().getLocation())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (inDisabledWorld(event.getWhoClicked().getLocation())) {
            return;
        }

        if (event.getRawSlot() != event.getSlot() && event.getSlot() == 40) {
            event.setCancelled(true);
        }
    }
}
