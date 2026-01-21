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
import me.zetastormy.akropolis.config.ConfigurationContainer;
import me.zetastormy.akropolis.config.type.CustomInventory;
import me.zetastormy.akropolis.inventory.AbstractInventory;
import me.zetastormy.akropolis.inventory.InventoryBuilder;
import me.zetastormy.akropolis.inventory.InventoryItem;
import me.zetastormy.akropolis.util.ItemStackBuilder;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class CustomGUI extends AbstractInventory {
    private final @NotNull ConfigurationContainer<CustomInventory> configContainer;
    private final @NotNull String inventoryName;
    private InventoryBuilder inventory;

    public CustomGUI(
            final @NotNull AkropolisPlugin plugin,
            final @NotNull String inventoryName,
            final @NotNull ConfigurationContainer<CustomInventory> configContainer
    ) {
        super(plugin);
        this.inventoryName = inventoryName;
        this.configContainer = configContainer;
    }

    @Override
    public void onEnable() {
        final CustomInventory config = this.configContainer.getConfig();

        final InventoryBuilder inventoryBuilder = new InventoryBuilder(
                getPlugin().getSLF4JLogger(),
                this.inventoryName,
                config.slots(),
                config.title()
        );

        if (config.refresh().enabled()) {
            this.setInventoryRefresh(config.refresh().rate());
        }

        if (config.items().isEmpty()) {
            getPlugin().getSLF4JLogger()
                    .error("Items configuration section for menu '{}' is empty!", this.inventoryName);
            return;
        }

        config.items().forEach((key, item) -> {
            try {
                final InventoryItem inventoryItem = build(item);
                if (!selectAndFillSlots(inventoryBuilder, item, inventoryItem)) {
                    getPlugin().getSLF4JLogger().warn(
                            "Could not find any slot for item '{}' on menu '{}', please check your configuration.",
                            key,
                            this.inventoryName
                    );
                }
            } catch (Exception exception) {
                getPlugin().getSLF4JLogger().warn(
                        "There was an error loading GUI item ID '{}' on menu '{}', skipping...",
                        key,
                        this.inventoryName,
                        exception
                );
            }
        });

        inventory = inventoryBuilder;
    }

    private @NotNull InventoryItem build(final @NotNull CustomInventory.ItemRecord itemConfig) {
        final ItemStackBuilder itemStackBuilder = ItemStackBuilder
                .getItemStack(itemConfig);

        InventoryItem inventoryItem;

        if (itemConfig.actions() == null || itemConfig.actions().isEmpty()) {
            inventoryItem = new InventoryItem(itemStackBuilder.build());
        } else {
            inventoryItem = new InventoryItem(itemStackBuilder.build()).addClickAction(p -> getPlugin()
                    .getActionManager().executeActions(p, itemConfig.actions()));
        }

        return inventoryItem;
    }

    /**
     * Selects the slots that the item will fill based on the
     * configuration and sets the slots to the item.
     *
     * @return whether the item was set to at least one slot
     */
    private boolean selectAndFillSlots(
            final @NotNull InventoryBuilder inventoryBuilder,
            final @NotNull CustomInventory.ItemRecord itemConfig,
            final @NotNull InventoryItem inventoryItem
    ) {
        if (itemConfig.slots() != null && !itemConfig.slots().isEmpty()) {
            for (int slot : itemConfig.slots()) {
                inventoryBuilder.setItem(slot, inventoryItem);
            }
            return true;
        } else if (itemConfig.slot() != null) {
            int slot = itemConfig.slot();

            if (slot == -1) {
                while (inventoryBuilder.getInventory().firstEmpty() != -1) {
                    inventoryBuilder.setItem(inventoryBuilder.getInventory().firstEmpty(), inventoryItem);
                }
            } else {
                inventoryBuilder.setItem(slot, inventoryItem);
            }
            return true;
        }
        return false;
    }

    @Override
    protected Inventory getInventory() {
        return inventory.getInventory();
    }
}
