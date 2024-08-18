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

package team.devblook.akropolis.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.command.commands.*;
import team.devblook.akropolis.command.commands.gamemode.*;
import team.devblook.akropolis.config.ConfigType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandManager {
    private final AkropolisPlugin plugin;
    private final Set<InjectableCommand> commands;
    private final List<CustomCommand> customCommands;
    private final CommandMap commandMap;
    private FileConfiguration config;

    public CommandManager(AkropolisPlugin plugin) {
        this.plugin = plugin;
        this.commands = new HashSet<>();
        this.customCommands = new ArrayList<>();
        this.commandMap = Bukkit.getCommandMap();
    }

    public void reload() {
        config = plugin.getConfigManager().getFile(ConfigType.COMMANDS).get();

        commands.forEach(this::unregisterCommand);
        if (!commands.isEmpty()) commands.clear();

        registerCommand(new AkropolisCommand(plugin));

        ConfigurationSection commandsSection = config.getConfigurationSection("commands");

        if (commandsSection == null) {
            plugin.getLogger().severe("Commands settings configuration section is missing!");
            return;
        }

        for (String command : commandsSection.getKeys(false)) {
            if (!config.getBoolean("commands." + command + ".enabled")) {
                continue;
            }

            registerCommand(command, commandsSection.getStringList(command + ".aliases"));
        }

        reloadCustomCommands();
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }

    public void reloadCustomCommands() {
        customCommands.forEach(this::unregisterCommand);
        if (!customCommands.isEmpty()) customCommands.clear();

        if (!config.isSet("custom_commands")) return;

        ConfigurationSection customCommandsSection = config.getConfigurationSection("custom_commands");

        if (customCommandsSection == null) {
            plugin.getLogger().info("Skipping custom commands registration, configuration section is missing!");
            return;
        }

        for (String entry : customCommandsSection.getKeys(false)) {
            List<String> actions = customCommandsSection.getStringList(entry + ".actions");
            List<String> aliases = new ArrayList<>();

            if (customCommandsSection.contains(entry + ".aliases")) {
                aliases = config.getStringList("custom_commands." + entry + ".aliases");
            }

            CustomCommand customCommand = new CustomCommand(plugin, entry, aliases, actions);

            if (customCommandsSection.contains(entry + ".permission")) {
                customCommand.setPermission(config.getString("custom_commands." + entry + ".permission"));
            }

            registerCommand(customCommand);
        }
    }

    private void registerCommand(String cmd, List<String> aliases) {
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
