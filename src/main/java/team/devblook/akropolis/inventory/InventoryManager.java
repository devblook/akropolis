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

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.inventory.inventories.CustomGUI;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class InventoryManager {
    private AkropolisPlugin plugin;
    private final Map<String, AbstractInventory> inventories;

    public InventoryManager() {
        inventories = new HashMap<>();
    }

    public void onEnable(AkropolisPlugin plugin) {
        this.plugin = plugin;

        loadCustomMenus();
        inventories.values().forEach(AbstractInventory::onEnable);

        plugin.getServer().getPluginManager().registerEvents(new InventoryListener(), plugin);
    }

    private void loadCustomMenus() {
        File directory = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "menus");

        if (!directory.exists()) {
            if (!directory.mkdir()) {
                plugin.getLogger().severe("Could not create menus' directory!");
                plugin.getLogger().severe("The plugin will now disable.");
                Bukkit.getPluginManager().disablePlugin(plugin);
                return;
            }

            File file = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "menus",
                    "serverselector.yml");

            try (InputStream inputStream = this.plugin.getResource("serverselector.yml");
                 OutputStream outputStream = new FileOutputStream(file)) {
                if (inputStream == null) {
                    plugin.getLogger().severe("Resource serverselector.yml not available in plugin's JAR!");
                    plugin.getLogger().severe("The plugin will now disable.");
                    Bukkit.getPluginManager().disablePlugin(plugin);
                    return;
                }

                byte[] buffer = new byte[inputStream.available()];

                if (inputStream.read(buffer) != 0) {
                    plugin.getLogger().info("Resource file serverselector.yml written sucessfully!");
                }

                outputStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // Load all menu files
        File[] yamlFiles = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "menus")
                .listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));

        if (yamlFiles == null)
            return;

        for (File file : yamlFiles) {
            String name = file.getName().replace(".yml", "");

            if (inventories.containsKey(name)) {
                plugin.getLogger()
                        .warning("Inventory with name '" + file.getName() + "' already exists, skipping duplicate..");
                continue;
            }

            CustomGUI customGUI;
            try {
                customGUI = new CustomGUI(plugin, YamlConfiguration.loadConfiguration(file));
            } catch (Exception e) {
                plugin.getLogger().severe("Could not load file '" + name + "' (YAML error).");
                e.printStackTrace();
                continue;
            }

            inventories.put(name, customGUI);
            plugin.getLogger().log(Level.INFO, "Loaded custom menu {0}.", name);
        }
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

        inventories.clear();
    }
}
