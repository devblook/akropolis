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

package team.devblook.akropolis.config;

import team.devblook.akropolis.AkropolisPlugin;

import java.util.EnumMap;
import java.util.Map;

public class ConfigManager {
    private final Map<ConfigType, ConfigHandler> configurations;

    public ConfigManager() {
        configurations = new EnumMap<>(ConfigType.class);
    }

    public void loadFiles(AkropolisPlugin plugin) {
        registerFile(ConfigType.SETTINGS, new ConfigHandler(plugin, "config"));
        registerFile(ConfigType.MESSAGES, new ConfigHandler(plugin, "messages"));
        registerFile(ConfigType.DATA, new ConfigHandler(plugin, "data"));
        registerFile(ConfigType.COMMANDS, new ConfigHandler(plugin, "commands"));

        configurations.values().forEach(ConfigHandler::saveDefaultConfig);

        Message.setConfiguration(getFile(ConfigType.MESSAGES).get());
    }

    public ConfigHandler getFile(ConfigType type) {
        return configurations.get(type);
    }

    public void reloadFiles() {
        configurations.values().forEach(ConfigHandler::reload);
        Message.setConfiguration(getFile(ConfigType.MESSAGES).get());
    }

    public void saveData() {
        getFile(ConfigType.DATA).save();
    }

    public void registerFile(ConfigType type, ConfigHandler config) {
        configurations.put(type, config);
    }

}
