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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.config.ConfigType;
import me.zetastormy.akropolis.module.LifeCycle;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.module.modules.hotbar.items.CustomItem;
import me.zetastormy.akropolis.module.modules.hotbar.items.FightMode;
import me.zetastormy.akropolis.module.modules.hotbar.items.PlayerHider;
import me.zetastormy.akropolis.util.ItemStackBuilder;

public class HotbarManager extends Module implements LifeCycle {
    private List<HotbarItem> hotbarItems;
    private Map<UUID, List<HotbarItem>> players;

    public HotbarManager(AkropolisPlugin plugin) {
        super(plugin, ModuleType.HOTBAR_ITEMS);
    }

    @Override
    public void onEnable() {
        hotbarItems = new ArrayList<>();
        players = new HashMap<>();

        FileConfiguration config = getConfig(ConfigType.SETTINGS);
        ConfigurationSection customItemsSections = config.getConfigurationSection("custom_join_items");

        if (customItemsSections == null) {
            getPlugin().getLogger().severe("Custom join items configuration section is missing!");
            return;
        }

        if (customItemsSections.getBoolean("enabled")) {
            registerCustomItems(customItemsSections);
        }

        ConfigurationSection fightModeSection = config.getConfigurationSection("fight_mode");

        if (fightModeSection == null) {
            getPlugin().getLogger().severe("Fight mode item configuration section is missing!");
        } else if (fightModeSection.getBoolean("enabled")) {
            ItemStack item = ItemStackBuilder.getItemStack(fightModeSection.getConfigurationSection("item")).build();
            FightMode fightMode = new FightMode(this, item, fightModeSection.getInt("slot"), "FIGHT_MODE_ITEM");

            fightMode.setDisableMovement(fightModeSection.getBoolean("disable_inventory_movement"));
            registerHotbarItem(fightMode);
        }

        ConfigurationSection hiderSection = config.getConfigurationSection("player_hider");

        if (hiderSection == null) {
            getPlugin().getLogger().severe("Player hider item configuration section is missing!");
        } else if (hiderSection.getBoolean("enabled")) {
            boolean playersHidden = config.getBoolean("join_settings.players_hidden", false);
            ItemStack item;

            if (playersHidden) {
                item = ItemStackBuilder.getItemStack(hiderSection.getConfigurationSection("hidden")).build();
            } else {
                item = ItemStackBuilder.getItemStack(hiderSection.getConfigurationSection("not_hidden")).build();
            }

            PlayerHider playerHider = new PlayerHider(this, item, hiderSection.getInt("slot"), "PLAYER_HIDER");

            playerHider.setDisableMovement(hiderSection.getBoolean("disable_inventory_movement"));
            registerHotbarItem(playerHider);
        }

        giveItems();
    }

    @Override
    public void onDisable() {
        removeItems();
    }

    private void registerCustomItems(ConfigurationSection customItemsSection) {
        ConfigurationSection itemsSection = customItemsSection.getConfigurationSection("items");
        boolean refreshCustomJoinItems = customItemsSection.getBoolean("refresh.enabled", true);
        long refreshRateCustomJoinItems = customItemsSection.getLong("refresh.rate", 20);
        boolean disableInventoryMovement = customItemsSection.getBoolean("disable_inventory_movement");

        if (itemsSection == null) {
            getPlugin().getLogger().severe("Items of custom join items configuration section is missing!");
            return;
        }

        for (String itemEntry : itemsSection.getKeys(false)) {
            ItemStack item = ItemStackBuilder.getItemStack(itemsSection.getConfigurationSection(itemEntry)).build();
            CustomItem customItem = new CustomItem(this, item, itemsSection.getInt(itemEntry + ".slot"), itemEntry);

            if (itemsSection.contains(itemEntry + ".permission")) {
                customItem.setPermission(itemsSection.getString(itemEntry + ".permission"));
            }

            customItem.setRefresh(refreshCustomJoinItems);
            customItem.setRefreshRate(refreshRateCustomJoinItems);
            customItem.setDisableMovement(disableInventoryMovement);
            customItem.setConfigurationSection(itemsSection.getConfigurationSection(itemEntry));
            registerHotbarItem(customItem);
        }
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
        return players.containsKey(playerUuid);
    }

    public List<HotbarItem> getHotbarItems() {
        return hotbarItems;
    }

    public Map<UUID, List<HotbarItem>> getPlayers() {
        return players;
    }
}
