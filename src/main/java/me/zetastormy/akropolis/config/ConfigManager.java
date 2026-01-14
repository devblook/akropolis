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

package me.zetastormy.akropolis.config;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.config.serializer.LocationSerializer;
import me.zetastormy.akropolis.config.type.*;
import org.bukkit.Location;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.util.NamingSchemes;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final Map<Class<?>, ConfigurationContainer<?>> configurations;

    public ConfigManager() {
        this.configurations = new HashMap<>();
    }

    public void loadFiles(AkropolisPlugin plugin) {
        TypeSerializerCollection typeSerializerCollection = TypeSerializerCollection.builder()
                .register(Location.class, LocationSerializer.INSTANCE).build();

        try {
            registerFile(Settings.class, ConfigurationContainer.load(
                    Settings.class,
                    plugin.getSLF4JLogger(),
                    plugin.getDataPath().resolve("config.yml"),
                    Settings.HEADER,
                    NamingSchemes.SNAKE_CASE,
                    typeSerializerCollection
            ));

            registerFile(Messages.class, ConfigurationContainer.load(
                    Messages.class,
                    plugin.getSLF4JLogger(),
                    plugin.getDataPath().resolve("messages.yml"),
                    Messages.HEADER,
                    NamingSchemes.SNAKE_CASE,
                    typeSerializerCollection
            ));

            registerFile(Data.class, ConfigurationContainer.load(
                    Data.class,
                    plugin.getSLF4JLogger(),
                    plugin.getDataPath().resolve("data.yml"),
                    Data.HEADER,
                    NamingSchemes.SNAKE_CASE,
                    typeSerializerCollection
            ));

            registerFile(Commands.class, ConfigurationContainer.load(
                    Commands.class,
                    plugin.getSLF4JLogger(),
                    plugin.getDataPath().resolve("commands.yml"),
                    Commands.HEADER,
                    NamingSchemes.SNAKE_CASE,
                    typeSerializerCollection
            ));
        } catch (final ConfigurateException exception) {
            plugin.getLogger().severe("There was an error loading the configuration.");
            plugin.getLogger().severe("Please check for any obvious configuration mistakes");
            plugin.getLogger().severe("such as using tabs for spaces or forgetting to end quotes");
            plugin.getLogger().severe("before reporting to the developer. The plugin will now disable.");
            plugin.getLogger().severe("You can paste each configuration file in https://yamllint.com");
            plugin.getLogger().severe("and click 'Go' to automatically check for YAML format mistakes.");
            plugin.getLogger().severe("If you need further help you can join our Discord server:");
            plugin.getLogger().severe("https://discord.gg/w438z8TKej");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> ConfigurationContainer<T> getFile(Class<T> type) {
        return (ConfigurationContainer<T>) configurations.get(type);
    }

    public void reloadFiles() {
        configurations.values().forEach(ConfigurationContainer::reload);
    }

    public void saveData() {
        getFile(Data.class).save();
    }

    public <T> void registerFile(Class<T> type, ConfigurationContainer<T> config) {
        configurations.put(type, config);
    }

}
