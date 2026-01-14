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

package me.zetastormy.akropolis.module.modules.hotbar;

import java.util.*;

import me.zetastormy.akropolis.config.type.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.module.LifeCycle;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.module.modules.hotbar.items.CustomItem;
import me.zetastormy.akropolis.module.modules.hotbar.items.FightMode;
import me.zetastormy.akropolis.module.modules.hotbar.items.PlayerHider;
import me.zetastormy.akropolis.util.ItemStackBuilder;

public class HotbarManager extends Module implements LifeCycle {
    private List<HotbarItem> hotbarItems;
    private Set<UUID> players;

    public HotbarManager(AkropolisPlugin plugin) {
        super(plugin, ModuleType.HOTBAR_ITEMS);
    }

    @Override
    public void onEnable() {
        hotbarItems = new ArrayList<>();
        players = new HashSet<>();

        Settings config = getConfig(Settings.class);
        Settings.CustomJoinItems customItemsSections = config.customJoinItems();

        if (customItemsSections.enabled()) {
            registerCustomItems(customItemsSections);
        }

        Settings.FightMode fightModeSection = config.fightMode();

        if (fightModeSection.enabled()) {
            ItemStack item = ItemStackBuilder.getItemStack(fightModeSection.item()).build();
            FightMode fightMode = new FightMode(this, item, fightModeSection.slot(), "FIGHT_MODE_ITEM");

            fightMode.setDisableMovement(fightModeSection.disableInventoryMovement());
            registerHotbarItem(fightMode);
        }

        Settings.PlayerHider hiderSection = config.playerHider();
        Settings.JoinSettings joinSettings = config.joinSettings();

        if (hiderSection.enabled()) {
            boolean playersHidden = joinSettings.playersHidden();
            ItemStack item;

            if (playersHidden) {
                item = ItemStackBuilder.getItemStack(hiderSection.hidden()).build();
            } else {
                item = ItemStackBuilder.getItemStack(hiderSection.notHidden()).build();
            }

            PlayerHider playerHider = new PlayerHider(this, item, hiderSection.slot(), "PLAYER_HIDER");

            playerHider.setDisableMovement(hiderSection.disableInventoryMovement());
            registerHotbarItem(playerHider);
        }

        giveItems();
    }

    @Override
    public void onDisable() {
        removeItems();
    }

    private void registerCustomItems(Settings.CustomJoinItems customItemsSection) {
        Map<String, Settings.ItemRecord> itemsSection = customItemsSection.items();

        itemsSection.forEach((itemKey, itemRecord) -> {
            ItemStack item = ItemStackBuilder.getItemStack(itemRecord).build();
            CustomItem customItem = new CustomItem(this, item, itemRecord.slot(), itemKey);

            if (itemRecord.permission() != null) {
                customItem.setPermission(itemRecord.permission());
            }

            customItem.setConfigurationSection(itemRecord);
            customItem.setDisableMovement(customItemsSection.disableInventoryMovement());
            registerHotbarItem(customItem);
        });
    }

    public void registerHotbarItem(HotbarItem hotbarItem) {
        getPlugin().getServer().getPluginManager().registerEvents(hotbarItem, getPlugin());
        hotbarItems.add(hotbarItem);
    }

    private void giveItems() {
        Bukkit.getOnlinePlayers().forEach(this::giveItemsToPlayer);
    }

    private void removeItems() {
        Bukkit.getOnlinePlayers().forEach(this::removeItemsFromPlayer);
    }

    public void giveItemsToPlayer(Player player) {
        if (inDisabledWorld(player.getLocation())) return;

        hotbarItems.forEach(hotbarItem -> hotbarItem.giveItem(player));
    }

    public void removeItemsFromPlayer(Player player) {
        if (inDisabledWorld(player.getLocation())) return;

        hotbarItems.forEach(hotbarItem -> hotbarItem.removeItem(player));
    }

    public boolean hasHotbar(UUID playerUuid) {
        return players.contains(playerUuid);
    }

    public List<HotbarItem> getHotbarItems() {
        return hotbarItems;
    }

    public Set<UUID> getPlayers() {
        return players;
    }
}
