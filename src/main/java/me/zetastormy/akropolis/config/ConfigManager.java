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
import me.zetastormy.akropolis.config.transformation.CommandsTransformations;
import me.zetastormy.akropolis.config.transformation.DataTransformations;
import me.zetastormy.akropolis.config.transformation.MessagesTransformations;
import me.zetastormy.akropolis.config.transformation.SettingsTransformations;
import me.zetastormy.akropolis.config.type.*;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.util.NamingSchemes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConfigManager {

    private final Map<Class<?>, ConfigurationContainer<?>> configurations;
    private final Logger logger;
    private final ExecutorService configExecutor = Executors.newSingleThreadExecutor();

    public ConfigManager(final Logger logger) {
        this.configurations = new HashMap<>();
        this.logger = logger;
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
                    typeSerializerCollection,
                    new SettingsTransformations(plugin.getSLF4JLogger())
            ));

            registerFile(Messages.class, ConfigurationContainer.load(
                    Messages.class,
                    plugin.getSLF4JLogger(),
                    plugin.getDataPath().resolve("messages.yml"),
                    Messages.HEADER,
                    NamingSchemes.SNAKE_CASE,
                    typeSerializerCollection,
                    new MessagesTransformations(plugin.getSLF4JLogger())
            ));

            registerFile(Data.class, ConfigurationContainer.load(
                    Data.class,
                    plugin.getSLF4JLogger(),
                    plugin.getDataPath().resolve("data.yml"),
                    Data.HEADER,
                    NamingSchemes.SNAKE_CASE,
                    typeSerializerCollection,
                    new DataTransformations()
            ));

            registerFile(Commands.class, ConfigurationContainer.load(
                    Commands.class,
                    plugin.getSLF4JLogger(),
                    plugin.getDataPath().resolve("commands.yml"),
                    Commands.HEADER,
                    NamingSchemes.SNAKE_CASE,
                    typeSerializerCollection,
                    new CommandsTransformations()
            ));
        } catch (final Exception exception) {
            this.logger.error("There was an error loading the configuration.");
            this.logger.error("The plugin will now disable.");
            this.logger.error("");
            this.logger.error("Please check for any common configuration mistakes such as:");
            this.logger.error("- Not using quotes at all on strings with special characters");
            this.logger.error("- Forgetting to end quotes");
            this.logger.error("- Using different open and close quotes in a string");
            this.logger.error("- Using tabs instead of spaces");
            this.logger.error("");
            this.logger.error("You can paste each configuration file in https://yamllint.com");
            this.logger.error("and click 'Go' to automatically check for YAML format mistakes.");
            this.logger.error("If you need further help you can join our Discord server:");
            this.logger.error("https://discord.gg/w438z8TKej");
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
        final @Nullable ConfigurationContainer<Data> dataConfig = this.getFile(Data.class);
        if (dataConfig == null) {
            this.logger.error("Could not save data because it was not loaded.");
        } else {
            this.getFile(Data.class).save(this.configExecutor);
        }

        try {
            this.logger.info("Awaiting configuration executor shutdown...");
            this.configExecutor.shutdown();
            if (!this.configExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                this.logger.error("Timed out while awaiting configuration executor shutdown, data may not have saved.");
            } else {
                this.logger.info("Configuration executor has shut down successfully!");
            }
        } catch (InterruptedException e) {
            this.logger.error("Interrupted while awaiting configuration executor shutdown, data may not have saved.");
        }
    }

    public <T> void registerFile(Class<T> type, ConfigurationContainer<T> config) {
        configurations.put(type, config);
    }

    public @NotNull ExecutorService getConfigurationExecutorService() {
        return this.configExecutor;
    }

}
