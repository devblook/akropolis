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

package me.zetastormy.akropolis.module.modules.visual.nametag;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.config.ConfigType;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.util.PlaceholderUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class NametagManager extends Module {
    private ConfigurationSection format;
    private NametagHelper nametagHelper;
    private int nametagTask;

    // TODO: make this work on reload
    public NametagManager(AkropolisPlugin plugin) {
        super(plugin, ModuleType.NAMETAG);
    }

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig(ConfigType.SETTINGS);

        format = config.getConfigurationSection("nametag.format");
        nametagHelper = new NametagHelper();

        if (config.getBoolean("nametag.refresh.enabled")) {
            nametagTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), new NametagUpdateTask(this, nametagHelper), 0L,
                    config.getLong("nametag.refresh.rate"));
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () ->
                Bukkit.getOnlinePlayers().forEach(player -> {
                    Component prefix = PlaceholderUtil.setPlaceholders(format.getString("prefix"), player);
                    TextColor color = TextColor.fromHexString(format.getString("name_color", "#FFFFFF"));
                    Component suffix = PlaceholderUtil.setPlaceholders(format.getString("suffix"), player);

                    nametagHelper.createFormat(prefix, color, suffix, player);
                }), 20L);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(nametagTask);
        Bukkit.getOnlinePlayers().forEach(nametagHelper::deleteFormat);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (inDisabledWorld(player.getWorld())) return;

        Component prefix = PlaceholderUtil.setPlaceholders(format.getString("prefix"), player);
        TextColor color = TextColor.fromHexString(format.getString("name_color", "#FFFFFF"));
        Component suffix = PlaceholderUtil.setPlaceholders(format.getString("suffix"), player);

        nametagHelper.createFormat(prefix, color, suffix, player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        nametagHelper.deleteFormat(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        World fromWorld = event.getFrom().getWorld();
        World toWorld = event.getTo().getWorld();

        if (toWorld == null) return;
        if (fromWorld == toWorld) return;

        if (inDisabledWorld(toWorld)) {
            nametagHelper.deleteFormat(player);
        } else {
            Component prefix = PlaceholderUtil.setPlaceholders(format.getString("prefix"), player);
            TextColor color = TextColor.fromHexString(format.getString("name_color", "#FFFFFF"));
            Component suffix = PlaceholderUtil.setPlaceholders(format.getString("suffix"), player);

            nametagHelper.createFormat(prefix, color, suffix, player);
        }
    }
}
