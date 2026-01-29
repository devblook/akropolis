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

package me.zetastormy.akropolis.inventory.inventories;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.inventory.AbstractInventory;
import me.zetastormy.akropolis.inventory.InventoryBuilder;
import me.zetastormy.akropolis.inventory.InventoryItem;
import me.zetastormy.akropolis.util.ItemStackBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CustomGUI extends AbstractInventory {
    private final FileConfiguration config;
    private final ConfigurationSection itemsSection;
    private InventoryBuilder inventory;

    public CustomGUI(AkropolisPlugin plugin, FileConfiguration config) {
        super(plugin);
        this.config = config;
        this.itemsSection = config.getConfigurationSection("items");
    }

    @Override
    public void onEnable() {
        InventoryBuilder inventoryBuilder = new InventoryBuilder(config.getInt("slots"),
                config.getString("title"));

        if (config.contains("refresh") && config.getBoolean("refresh.enabled")) {
            setInventoryRefresh(config.getLong("refresh.rate"));
        }

        if (itemsSection == null) {
            getPlugin().getLogger().severe("Items configuration section is missing!");
            return;
        }

        for (String item : itemsSection.getKeys(false)) {
            try {
                InventoryItem inventoryItem = build(item);
                setFiller(inventoryBuilder, item, inventoryItem);
            } catch (Exception e) {
                e.printStackTrace();
                getPlugin().getLogger().warning("There was an error loading GUI item ID '" + item + "', skipping..");
            }
        }

        inventory = inventoryBuilder;
    }

    private InventoryItem build(final @NotNull String itemKey) {
        // if the key exists then the section should exist too
        final ConfigurationSection itemConfig = Objects.requireNonNull(itemsSection.getConfigurationSection(itemKey));
        ItemStackBuilder itemStackBuilder = ItemStackBuilder
                .getItemStack(itemConfig);
        InventoryItem inventoryItem;

        if (!itemsSection.contains(itemKey + ".actions")) {
            inventoryItem = new InventoryItem(itemStackBuilder.build(), itemConfig.getString("permission"));
        } else {
            inventoryItem = new InventoryItem(itemStackBuilder.build(), itemConfig.getString("permission"))
                    .addClickAction(p -> getPlugin().getActionManager()
                            .executeActions(p, itemsSection.getStringList(itemKey + ".actions")));
        }

        return inventoryItem;
    }

    private void setFiller(InventoryBuilder inventoryBuilder, String item, InventoryItem inventoryItem) {
        if (itemsSection.contains(item + ".slots")) {
            for (String slot : itemsSection.getStringList(item + ".slots")) {
                inventoryBuilder.setItem(Integer.parseInt(slot), inventoryItem);
            }
        } else if (itemsSection.contains(item + ".slot")) {
            int slot = itemsSection.getInt(item + ".slot");

            if (slot == -1) {
                while (inventoryBuilder.getInventory().firstEmpty() != -1) {
                    inventoryBuilder.setItem(inventoryBuilder.getInventory().firstEmpty(), inventoryItem);
                }
            } else {
                inventoryBuilder.setItem(slot, inventoryItem);
            }
        }
    }

    @Override
    protected Inventory getInventory() {
        return inventory.getInventory();
    }
}
