package team.devblook.deluxehub.config;

import team.devblook.deluxehub.DeluxeHubPlugin;

import java.util.EnumMap;
import java.util.Map;

public class ConfigManager {
    private final Map<ConfigType, ConfigHandler> configurations;

    public ConfigManager() {
        configurations = new EnumMap<>(ConfigType.class);
    }

    public void loadFiles(DeluxeHubPlugin plugin) {
        registerFile(ConfigType.SETTINGS, new ConfigHandler(plugin, "config"));
        registerFile(ConfigType.MESSAGES, new ConfigHandler(plugin, "messages"));
        registerFile(ConfigType.DATA, new ConfigHandler(plugin, "data"));
        registerFile(ConfigType.COMMANDS, new ConfigHandler(plugin, "commands"));

        configurations.values().forEach(ConfigHandler::saveDefaultConfig);

        Messages.setConfiguration(getFile(ConfigType.MESSAGES).get());
    }

    public ConfigHandler getFile(ConfigType type) {
        return configurations.get(type);
    }

    public void reloadFiles() {
        configurations.values().forEach(ConfigHandler::reload);
        Messages.setConfiguration(getFile(ConfigType.MESSAGES).get());
    }

    public void saveData() {
        getFile(ConfigType.DATA).save();
    }

    public void registerFile(ConfigType type, ConfigHandler config) {
        configurations.put(type, config);
    }

}
