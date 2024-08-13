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

package team.devblook.akropolis.module.modules.visual.tablist;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.util.PlaceholderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TablistManager extends Module {
    private List<UUID> players;
    private int tablistTask;
    private String header;
    private String footer;

    public TablistManager(AkropolisPlugin plugin) {
        super(plugin, ModuleType.TABLIST);
    }

    @Override
    public void onEnable() {
        players = new ArrayList<>();

        FileConfiguration config = getConfig(ConfigType.SETTINGS);

        header = String.join("\n", config.getStringList("tablist.header"));
        footer = String.join("\n", config.getStringList("tablist.footer"));

        if (config.getBoolean("tablist.refresh.enabled")) {
            tablistTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), new TablistUpdateTask(this), 0L,
                    config.getLong("tablist.refresh.rate"));
        }

        getPlugin().getServer().getScheduler()
                .scheduleSyncDelayedTask(getPlugin(),
                        () -> Bukkit.getOnlinePlayers().stream()
                                .filter(player -> !inDisabledWorld(player.getLocation())).forEach(this::createTablist),
                        20L);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(tablistTask);
        Bukkit.getOnlinePlayers().forEach(this::removeTablist);
    }

    public void createTablist(Player player) {
        UUID uuid = player.getUniqueId();
        players.add(uuid);
        updateTablist(uuid);
    }

    public boolean updateTablist(UUID uuid) {
        if (!players.contains(uuid))
            return false;

        Player player = Bukkit.getPlayer(uuid);

        if (player == null)
            return false;

        TablistHelper.sendTabList(player, PlaceholderUtil.setPlaceholders(header, player),
                PlaceholderUtil.setPlaceholders(footer, player));

        return true;
    }

    public void removeTablist(Player player) {
        if (players.contains(player.getUniqueId())) {
            players.remove(player.getUniqueId());
            TablistHelper.sendTabList(player, null, null);
        }
    }

    public List<UUID> getPlayers() {
        return players;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!inDisabledWorld(player.getLocation()))
            createTablist(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeTablist(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        World fromWorld = event.getFrom().getWorld();
        World toWorld = event.getTo().getWorld();

        if (toWorld == null) return;
        if (fromWorld == toWorld) return;

        if (inDisabledWorld(toWorld) && players.contains(player.getUniqueId())) {
            removeTablist(player);
            return;
        }

        createTablist(player);
    }
}
