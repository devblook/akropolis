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
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.util.ItemStackBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractInventory implements Listener {
    private final AkropolisPlugin plugin;
    private boolean refreshEnabled = false;
    private final List<UUID> openInventories;

    protected AbstractInventory(AkropolisPlugin plugin) {
        this.plugin = plugin;
        openInventories = new ArrayList<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void setInventoryRefresh(long value) {
        if (value <= 0)
            return;

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new InventoryTask(this), 0L, value);
        refreshEnabled = true;
    }

    public abstract void onEnable();

    protected abstract Inventory getInventory();

    protected AkropolisPlugin getPlugin() {
        return plugin;
    }

    public Inventory refreshInventory(Player player, Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = getInventory().getItem(i);

            if (item == null || item.getType() == Material.AIR) continue;
            if (item.getItemMeta() == null || !item.hasItemMeta()) continue;

            ItemStackBuilder newItem = new ItemStackBuilder(item.clone());

            if (item.getItemMeta().hasDisplayName()) {
                newItem.withName(item.getItemMeta().displayName(), player);
            }

            if (item.getItemMeta().hasLore() && item.getItemMeta().lore() != null) {
                newItem.withLore(item.getItemMeta().lore(), player);
            }

            if (item.getType() == Material.PLAYER_HEAD) {
                ItemMeta itemMeta = item.getItemMeta();
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();

                if (container.has(NamespacedKey.minecraft("player-head"), PersistentDataType.BOOLEAN)) {
                    newItem.setSkullOwner(player);
                }
            }

            inventory.setItem(i, newItem.build());
        }

        return inventory;
    }

    public void openInventory(Player player) {
        if (getInventory() == null)
            return;

        player.openInventory(refreshInventory(player, getInventory()));

        if (refreshEnabled && !openInventories.contains(player.getUniqueId())) {
            openInventories.add(player.getUniqueId());
        }
    }

    public List<UUID> getOpenInventories() {
        return openInventories;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof InventoryBuilder && refreshEnabled) {
            openInventories.remove(event.getPlayer().getUniqueId());
        }
    }
}
