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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.zetastormy.akropolis.config.ConfigType;
import me.zetastormy.akropolis.module.modules.hotbar.HotbarItem;
import me.zetastormy.akropolis.module.modules.hotbar.HotbarManager;
import me.zetastormy.akropolis.util.ItemStackBuilder;

public class FightMode extends HotbarItem {
    private final Set<UUID> fighters;
    private final ItemStack fightItem;

    public FightMode(HotbarManager hotbarManager, ItemStack item, int slot, String keyValue) {
        super(hotbarManager, item, slot, keyValue);
        fighters = new HashSet<>();

        FileConfiguration config = getHotbarManager().getConfig(ConfigType.SETTINGS);

        ItemStack fightItem = ItemStackBuilder.getItemStack(config.getConfigurationSection("fight_mode.item")).build();

        ItemMeta fightMeta = item.getItemMeta();
        PersistentDataContainer fightContainer = fightMeta.getPersistentDataContainer();
        fightContainer.set(NamespacedKey.minecraft("hotbar-item"), PersistentDataType.STRING, keyValue);

        fightItem.setItemMeta(fightMeta);

        this.fightItem = fightItem;
    }

    @Override
    protected void onInteract(Player player) {

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawnEvent(PlayerRespawnEvent event) {

    }
}
