package fun.lewisdev.deluxehub.command;

import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.command.commands.*;
import fun.lewisdev.deluxehub.command.commands.gamemode.*;
import fun.lewisdev.deluxehub.config.ConfigType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandManager {
    private final DeluxeHubPlugin plugin;
    private final FileConfiguration config;

    private final Set<InjectableCommand> commands;
    private final List<CustomCommand> customCommands;
    private CommandMap commandMap;

    public CommandManager(DeluxeHubPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.COMMANDS).get();
        this.commands = new HashSet<>();
        this.customCommands = new ArrayList<>();
        setCommandMap();
    }

    public void reload() {
        commands.forEach(c -> c.unregister(commandMap));
        if (!commands.isEmpty()) commands.clear();

        registerCommand(new DeluxeHubCommand(plugin));

        ConfigurationSection commandsSection = config.getConfigurationSection("commands");

        if (commandsSection == null) {
            plugin.getLogger().severe("Commands settings configuration section is missing!");
            return;
        }

        for (String command : commandsSection.getKeys(false)) {
            if (!config.getBoolean("commands." + command + ".enabled")) continue;

            registerCommand(command, commandsSection.getStringList(command + ".aliases"));
        }

        reloadCustomCommands();
    }

    public void reloadCustomCommands() {
        if (!customCommands.isEmpty()) customCommands.clear();
        if (!config.isSet("custom_commands")) return;

        ConfigurationSection customCommandsSection = config.getConfigurationSection("custom_commands");

        if (customCommandsSection == null) {
            plugin.getLogger().severe("Custom commands configuration section is missing!");
            return;
        }

        for (String entry : customCommandsSection.getKeys(false)) {

            CustomCommand customCommand = new CustomCommand(entry,
                    customCommandsSection.getStringList(entry + ".actions"));

            if (customCommandsSection.contains(entry + ".aliases")) {
                customCommand.addAliases(config.getStringList("custom_commands." + entry + ".aliases"));
            }

            if (customCommandsSection.contains(entry + ".permission")) {
                customCommand.setPermission(config.getString("custom_commands." + entry + ".permission"));
            }

            this.customCommands.add(customCommand);
        }
    }

    private void registerCommand(String cmd, List<String> aliases) {
        switch (cmd.toUpperCase()) {
            case "GAMEMODE":
                registerCommand(new GamemodeCommand(plugin, aliases));
                break;
            case "GMS":
                registerCommand(new SurvivalCommand(plugin, aliases));
                break;
            case "GMC":
                registerCommand(new CreativeCommand(plugin, aliases));
                break;
            case "GMA":
                registerCommand(new AdventureCommand(plugin, aliases));
                break;
            case "GMSP":
                registerCommand(new SpectatorCommand(plugin, aliases));
                break;
            case "CLEARCHAT":
                registerCommand(new ClearchatCommand(plugin, aliases));
                break;
            case "FLY":
                registerCommand(new FlyCommand(plugin, aliases));
                break;
            case "LOCKCHAT":
                registerCommand(new LockchatCommand(plugin, aliases));
                break;
            case "SETLOBBY":
                registerCommand(new SetLobbyCommand(plugin, aliases));
                break;
            case "LOBBY":
                registerCommand(new LobbyCommand(plugin, aliases));
                break;
            case "VANISH":
                registerCommand(new VanishCommand(plugin, aliases));
                break;
            default:
                break;
        }
    }

    private void registerCommand(InjectableCommand command) {
        try {
            commandMap.register("deluxehub", command);
            commands.add(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CustomCommand> getCustomCommands() {
        return customCommands;
    }

    public void setCommandMap() {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);

            this.commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
