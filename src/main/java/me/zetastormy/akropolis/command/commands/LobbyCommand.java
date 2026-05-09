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

package me.zetastormy.akropolis.command.commands;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.command.InjectableCommand;
import me.zetastormy.akropolis.config.type.Data;
import me.zetastormy.akropolis.config.type.Settings;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.module.modules.world.LobbySpawn;
import me.zetastormy.akropolis.util.text.TextUtil;
import org.jetbrains.annotations.NotNull;

public class LobbyCommand extends InjectableCommand {
    private final AkropolisPlugin plugin;
    private final Map<UUID, Data.PlayerData> playersSection;
    private final boolean forceJoinFly;

    public LobbyCommand(final @NotNull AkropolisPlugin plugin, final @NotNull List<String> aliases) {
        super(plugin, "lobby", "Teleport to the lobby (if set)", aliases);
        this.plugin = plugin;

        final Settings config = plugin.getConfigManager().getFile(Settings.class).getConfig();
        final Data dataFile = plugin.getConfigManager().getFile(Data.class).getConfig();

        this.playersSection = dataFile.getPlayers();
        this.forceJoinFly = config.fly().forceOnJoin();
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Console cannot teleport to spawn");
            return;
        }

        final var plugin = this.getPlugin();
        Location location = ((LobbySpawn) plugin.getModuleManager().getModule(ModuleType.LOBBY)).getLocation();
        if (location == null) {
            sender.sendMessage(TextUtil.parse("<red>The spawn location has not been set <gray>(/setlobby)<red>."));
            return;
        }

        player.teleportAsync(location);
    }
}
