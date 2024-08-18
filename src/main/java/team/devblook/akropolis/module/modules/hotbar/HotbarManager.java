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

package team.devblook.akropolis.module.modules.hotbar;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.module.modules.hotbar.items.CustomItem;
import team.devblook.akropolis.module.modules.hotbar.items.PlayerHider;
import team.devblook.akropolis.util.ItemStackBuilder;

import java.util.ArrayList;
import java.util.List;

public class HotbarManager extends Module {
    private List<HotbarItem> hotbarItems;

    public HotbarManager(AkropolisPlugin plugin) {
        super(plugin, ModuleType.HOTBAR_ITEMS);
    }

    @Override
    public void onEnable() {
        hotbarItems = new ArrayList<>();
        FileConfiguration config = getConfig(ConfigType.SETTINGS);
        ConfigurationSection customItemsSections = config.getConfigurationSection("custom_join_items");

        if (customItemsSections == null) {
            getPlugin().getLogger().severe("Custom join items configuration section is missing!");
            return;
        }

        if (customItemsSections.getBoolean("enabled")) {
            registerCustomItems(customItemsSections);
        }

        ConfigurationSection hiderSection = config.getConfigurationSection("player_hider");

        if (hiderSection == null) {
            getPlugin().getLogger().severe("Player hider item configuration section is missing!");
            return;
        }

        if (hiderSection.getBoolean("enabled")) {
            ItemStack item = ItemStackBuilder.getItemStack(hiderSection.getConfigurationSection("not_hidden")).build();
            PlayerHider playerHider = new PlayerHider(this, item, hiderSection.getInt("slot"), "PLAYER_HIDER");

            playerHider.setAllowMovement(hiderSection.getBoolean("disable_inventory_movement"));
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

            customItem.setConfigurationSection(itemsSection.getConfigurationSection(itemEntry));
            customItem.setAllowMovement(customItemsSection.getBoolean("disable_inventory_movement"));
            registerHotbarItem(customItem);
        }
    }

    public void registerHotbarItem(HotbarItem hotbarItem) {
        getPlugin().getServer().getPluginManager().registerEvents(hotbarItem, getPlugin());
        hotbarItems.add(hotbarItem);
    }

    private void giveItems() {
        Bukkit.getOnlinePlayers().stream().filter(player -> !inDisabledWorld(player.getLocation()))
                .forEach(player -> hotbarItems.forEach(hotbarItem -> hotbarItem.giveItem(player)));
    }

    private void removeItems() {
        Bukkit.getOnlinePlayers().stream().filter(player -> !inDisabledWorld(player.getLocation()))
                .forEach(player -> hotbarItems.forEach(hotbarItem -> hotbarItem.removeItem(player)));
    }

    public List<HotbarItem> getHotbarItems() {
        return hotbarItems;
    }
}
