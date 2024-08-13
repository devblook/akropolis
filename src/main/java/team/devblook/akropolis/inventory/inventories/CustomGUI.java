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

package team.devblook.akropolis.inventory.inventories;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.inventory.AbstractInventory;
import team.devblook.akropolis.inventory.InventoryBuilder;
import team.devblook.akropolis.inventory.InventoryItem;
import team.devblook.akropolis.util.ItemStackBuilder;

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

    private InventoryItem build(String item) {
        ItemStackBuilder itemStackBuilder = ItemStackBuilder
                .getItemStack(itemsSection.getConfigurationSection(item));
        InventoryItem inventoryItem;

        if (!itemsSection.contains(item + ".actions")) {
            inventoryItem = new InventoryItem(itemStackBuilder.build());
        } else {
            inventoryItem = new InventoryItem(itemStackBuilder.build()).addClickAction(p -> getPlugin()
                    .getActionManager().executeActions(p, itemsSection.getStringList(item + ".actions")));
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
