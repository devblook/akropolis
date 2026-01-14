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

package me.zetastormy.akropolis.command;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.command.commands.*;
import me.zetastormy.akropolis.command.commands.gamemode.*;
import me.zetastormy.akropolis.config.type.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandManager {
    private final AkropolisPlugin plugin;
    private final Set<InjectableCommand> commands;
    private final List<CustomCommand> customCommands;
    private final CommandMap commandMap;
    private Commands config;

    public CommandManager(AkropolisPlugin plugin) {
        this.plugin = plugin;
        this.commands = new HashSet<>();
        this.customCommands = new ArrayList<>();
        this.commandMap = Bukkit.getCommandMap();
    }

    public void reload() {
        this.config = plugin.getConfigManager().getFile(Commands.class).getConfig();

        commands.forEach(this::unregisterCommand);
        if (!commands.isEmpty()) commands.clear();

        this.registerCommand(new AkropolisCommand(plugin));

        final Map<String, Commands.BuiltinCommand> commandsSection = config.commands();

        commandsSection.forEach((key, builtinCommand) -> {
            if (builtinCommand.enabled()) {
                registerCommand(key, Objects.requireNonNullElse(builtinCommand.aliases(), new ArrayList<>()));
            }
        });

        reloadCustomCommands();
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }

    public void reloadCustomCommands() {
        customCommands.forEach(this::unregisterCommand);
        if (!customCommands.isEmpty()) customCommands.clear();

        final Map<String, Commands.CustomCommand> customCommandsSection = config.customCommands();

        customCommandsSection.forEach((key, customCommand) -> {
            final List<String> actions = Objects.requireNonNullElse(customCommand.actions(), new ArrayList<>());
            final List<String> aliases = Objects.requireNonNullElse(customCommand.aliases(), new ArrayList<>());

            final CustomCommand command = new CustomCommand(this.plugin, key, aliases, actions);

            final String permission = customCommand.permission();
            if (permission != null) {
                command.setPermission(permission);
            }

            this.registerCommand(command);
        });
    }

    private void registerCommand(@NotNull String cmd, @NotNull List<String> aliases) {
        switch (cmd.toUpperCase()) {
            case "GAMEMODE" -> registerCommand(new GamemodeCommand(plugin, aliases));
            case "GMS" -> registerCommand(new SurvivalCommand(plugin, aliases));
            case "GMC" -> registerCommand(new CreativeCommand(plugin, aliases));
            case "GMA" -> registerCommand(new AdventureCommand(plugin, aliases));
            case "GMSP" -> registerCommand(new SpectatorCommand(plugin, aliases));
            case "CLEARCHAT" -> registerCommand(new ClearchatCommand(plugin, aliases));
            case "FLY" -> registerCommand(new FlyCommand(plugin, aliases));
            case "LOCKCHAT" -> registerCommand(new LockchatCommand(plugin, aliases));
            case "SETLOBBY" -> registerCommand(new SetLobbyCommand(plugin, aliases));
            case "LOBBY" -> registerCommand(new LobbyCommand(plugin, aliases));
            case "VANISH" -> registerCommand(new VanishCommand(plugin, aliases));
            default -> {
            }
        }
    }

    private void registerCommand(InjectableCommand command) {
        try {
            commandMap.register("akropolis", command);
            commands.add(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unregisterCommand(InjectableCommand command) {
        try {
            command.getAliases().forEach(a -> commandMap.getKnownCommands().remove(a));
            commandMap.getKnownCommands().remove(command.getName());
            commandMap.getKnownCommands().remove("akropolis:" + command.getName());
            command.unregister(commandMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CustomCommand> getCustomCommands() {
        return customCommands;
    }
}
