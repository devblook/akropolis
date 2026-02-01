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

package me.zetastormy.akropolis.inventory;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.config.ConfigurationContainer;
import me.zetastormy.akropolis.config.type.CustomInventory;
import me.zetastormy.akropolis.inventory.inventories.CustomGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.util.NamingSchemes;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class InventoryManager {
    private AkropolisPlugin plugin;
    private final Map<String, AbstractInventory> inventories;
    private final Map<String, ConfigurationContainer<CustomInventory>> configurations;
    private static final String DIRECTORY_NAME = "menus";
    private static final String CONFIGURATION_FORMAT_EXTENSION = ".yml";
    private static final String DEFAULT_INVENTORY_NAME = "serverselector";

    public InventoryManager() {
        this.inventories = new HashMap<>();
        this.configurations = new HashMap<>();
    }

    public void onEnable(final AkropolisPlugin plugin) {
        this.plugin = plugin;

        this.loadCustomMenus();
        this.inventories.values().forEach(AbstractInventory::onEnable);

        plugin.getServer().getPluginManager().registerEvents(new InventoryListener(plugin.getConfigManager()), plugin);
    }

    private void loadCustomMenus() {
        final File directory = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + DIRECTORY_NAME);

        if (!directory.exists()) {
            if (!directory.mkdir()) {
                this.plugin.getSLF4JLogger().error("Could not create menus' directory!");
                this.plugin.getSLF4JLogger().error("The plugin will now disable.");
                Bukkit.getPluginManager().disablePlugin(plugin);
                return;
            }

            this.registerMenu(DEFAULT_INVENTORY_NAME, CustomInventory::defaultServerSelector);
        }

        // Load all menu files
        final File[] menuFiles = directory.listFiles(
                (file) -> file.isFile() && file.getName().toLowerCase().endsWith(CONFIGURATION_FORMAT_EXTENSION)
                && !(this.configurations.containsKey(DEFAULT_INVENTORY_NAME)
                        && file.getName()
                        .equalsIgnoreCase(DEFAULT_INVENTORY_NAME + CONFIGURATION_FORMAT_EXTENSION))
        );

        if (menuFiles == null) {
            plugin.getSLF4JLogger().error("Could not list menu directory files");
            return;
        }

        for (final File file : menuFiles) {
            final String name = file.getName().replace(CONFIGURATION_FORMAT_EXTENSION, "");
            this.registerMenu(directory, name);
        }
    }

    private void registerMenu(
            final @NotNull String name,
            final @Nullable Supplier<CustomInventory> defaultObjectSupplier
    ) {
        final String fileName = name + CONFIGURATION_FORMAT_EXTENSION;

        if (this.configurations.containsKey(name)) {
            this.plugin.getSLF4JLogger()
                    .warn("Skipping file '{}' duplicate of already loaded inventory '{}'", fileName, name);
            return;
        }

        try {
            final @NotNull Path dataPath = plugin.getDataPath();
            ConfigurationContainer<CustomInventory> configContainer = ConfigurationContainer.load(
                    CustomInventory.class,
                    this.plugin.getSLF4JLogger(),
                    dataPath.resolve(DIRECTORY_NAME).resolve(name + CONFIGURATION_FORMAT_EXTENSION),
                    CustomInventory.HEADER,
                    NamingSchemes.SNAKE_CASE,
                    null,
                    defaultObjectSupplier,
                    null
            );
            this.configurations.put(name, configContainer);

            final CustomGUI inventory = new CustomGUI(this.plugin, name, configContainer);
            this.inventories.put(name, inventory);

            this.plugin.getSLF4JLogger().info("Custom menu '{}' loaded successfully!", name);
        } catch (final Exception exception) {
            this.plugin.getSLF4JLogger().error("Failed to load custom menu file '{}'", fileName, exception);
        }
    }

    private void registerMenu(final @NotNull File directory, final @NotNull String name) {
        this.registerMenu(name, null);
    }

    public Map<String, AbstractInventory> getInventories() {
        return inventories;
    }

    public AbstractInventory getInventory(String key) {
        return inventories.get(key);
    }

    public void onDisable() {
        inventories.values().forEach(abstractInventory -> {
            for (UUID uuid : abstractInventory.getOpenInventories()) {
                Player player = Bukkit.getPlayer(uuid);

                if (player != null)
                    player.closeInventory();
            }

            abstractInventory.getOpenInventories().clear();
        });

        this.inventories.clear();
        this.configurations.clear();
    }
}
