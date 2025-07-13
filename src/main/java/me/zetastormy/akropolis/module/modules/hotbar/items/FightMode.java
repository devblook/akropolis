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

package me.zetastormy.akropolis.module.modules.hotbar.items;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.module.modules.hotbar.HotbarItem;
import me.zetastormy.akropolis.module.modules.hotbar.HotbarManager;
import me.zetastormy.akropolis.module.modules.player.FightModeManager;

public class FightMode extends HotbarItem {
    private final FightModeManager fightModeManager;

    public FightMode(HotbarManager hotbarManager, ItemStack item, int slot, String keyValue) {
        super(hotbarManager, item, slot, keyValue);

        this.fightModeManager = (FightModeManager) getPlugin().getModuleManager().getModule(ModuleType.FIGHT_MODE);
    }

    @Override
    protected void onInteract(Player player) {
        // Not used.
    }

    @Override
    public void removeItem(Player player) {
        super.removeItem(player);
        fightModeManager.disableFightMode(player);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());

        if (fightModeManager.isFightModeActive(playerUuid)) {
            if (fightModeManager.isValidItem(newItem)) {
                fightModeManager.cancelHoldTask(playerUuid);
            } else if (!fightModeManager.hasHoldTask(playerUuid)) {
                fightModeManager.startDeactivationTimer(player);
            }

            return;
        }

        if (fightModeManager.isValidItem(newItem)) {
            fightModeManager.cancelHoldTask(playerUuid);
            fightModeManager.startActivationTimer(player);
        } else {
            fightModeManager.cancelHoldTask(playerUuid);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        fightModeManager.disableFightMode(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        fightModeManager.disableFightMode(event.getPlayer());
    }

    @EventHandler()
    public void onRespawnEvent(PlayerRespawnEvent event) {
        fightModeManager.disableFightMode(event.getPlayer());
    }
}
