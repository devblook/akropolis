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

package team.devblook.akropolis.hook;

import org.bukkit.Bukkit;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.hook.hooks.head.BaseHead;
import team.devblook.akropolis.hook.hooks.head.DatabaseHead;
import team.devblook.akropolis.util.PlaceholderUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HooksManager {
    private final Map<String, PluginHook> hooks;

    public HooksManager(AkropolisPlugin plugin) {
        hooks = new HashMap<>();

        // Base64 head
        hooks.put("BASE64", new BaseHead());

        // PlaceholderAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            hooks.put("PLACEHOLDER_API", null);
            PlaceholderUtil.setPapiState(true);
            plugin.getLogger().info("Hooked into PlaceholderAPI");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("HeadDatabase")) {
            hooks.put("HEAD_DATABASE", new DatabaseHead());
            plugin.getLogger().info("Hooked into HeadDatabase");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            hooks.put("MINIPLACEHOLDERS", null);
            PlaceholderUtil.setMPState(true);
            plugin.getLogger().info("Hooked into MiniPlaceholders");
        }

        hooks.values().stream().filter(Objects::nonNull).forEach(pluginHook -> pluginHook.onEnable(plugin));
    }

    public boolean isHookEnabled(String id) {
        return hooks.containsKey(id);
    }

    public PluginHook getPluginHook(String id) {
        return hooks.get(id);
    }
}
