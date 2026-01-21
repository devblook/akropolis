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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.util.NamingSchemes;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class ConfigurationContainer<C> {

    private final AtomicReference<C> config;
    private final Class<C> clazz;
    private final YamlConfigurationLoader loader;
    private final Logger logger;
    private final Path filePath;
    private CommentedConfigurationNode root;

    private ConfigurationContainer(
            final C config,
            final Class<C> clazz,
            final YamlConfigurationLoader loader,
            final CommentedConfigurationNode root,
            final Logger logger,
            final Path filePath
    ) {
        this.config = new AtomicReference<>(config);
        this.clazz = clazz;
        this.loader = loader;
        this.root = root;
        this.logger = logger;
        this.filePath = filePath;
    }

    public static <C> ConfigurationContainer<C> load(
            final @NotNull Class<C> clazz,
            final @NotNull Logger logger,
            final @NotNull Path filePath,
            final @NotNull String header,
            final @NotNull NamingSchemes namingScheme,
            final @Nullable TypeSerializerCollection typeSerializerCollection,
            final @Nullable Supplier<C> defaultObjectSupplier
    ) throws ConfigurateException {
        final ObjectMapper.Factory customFactory = ObjectMapper.factoryBuilder()
                .defaultNamingScheme(namingScheme).build();

        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().commentsEnabled(true)
                .defaultOptions(
                options -> options.header(header).shouldCopyDefaults(false)
                        .serializers(build -> build.registerAnnotatedObjects(customFactory)
                                .registerAll(Objects.requireNonNullElseGet(typeSerializerCollection, () -> {
                                    return TypeSerializerCollection.builder().build();
                                })))
        ).path(filePath).indent(2).nodeStyle(NodeStyle.BLOCK).build();

        try {
            CommentedConfigurationNode rootNode = loader.load();
            C config = rootNode.get(clazz);
            if (Files.notExists(filePath)) {
                logger.info("Path {} does not exist, saving default values...", filePath);
                if (defaultObjectSupplier != null) {
                    config = defaultObjectSupplier.get();
                }
                rootNode.set(config);
                loader.save(rootNode);
            }
            var instance = new ConfigurationContainer<>(config, clazz, loader, rootNode, logger, filePath);
            logger.info("Configuration file {} loaded successfully!", filePath);
            return instance;
        } catch (final ConfigurateException exception) {
            logger.error("An exception occurred while loading configuration named {}",
                    filePath.getFileName(), exception);
            throw exception;
        }
    }

    public static <C> ConfigurationContainer<C> load(
            final @NotNull Class<C> clazz,
            final @NotNull Logger logger,
            final @NotNull Path filePath,
            final @NotNull String header,
            final @NotNull NamingSchemes namingScheme,
            final @Nullable TypeSerializerCollection typeSerializerCollection
    ) throws ConfigurateException {
        return load(clazz, logger, filePath, header, namingScheme, typeSerializerCollection, null);
    }

    public CompletableFuture<Boolean> reload() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CommentedConfigurationNode rootNode = this.loader.load();
                this.root = rootNode;
                C newConfig = rootNode.get(this.clazz);
                this.config.set(newConfig);
                this.logger.info("Configuration file {} reloaded successfully!", this.filePath.getFileName());
                return true;
            } catch (ConfigurateException exception) {
                logger.error("An exception occurred while reloading the configuration named {}",
                        this.filePath.getFileName(), exception);
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> save(final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                this.loader.save(this.root.set(this.clazz, this.config.get()));
                this.logger.info("Configuration file {} written successfully!", this.filePath.getFileName());
                return true;
            } catch (ConfigurateException exception) {
                this.logger.error("An exception occurred while saving the configuration named {}",
                        this.filePath.getFileName(), exception);
                return false;
            }
        }, executor);
    }

    public C getConfig() {
        return this.config.get();
    }

    public boolean getBoolean(String... path) {
        var node = this.root.node((Object[]) path);
        if (node.virtual()) {
            this.logger.error("Node path {} not found in configuration file {}", path, this.filePath);
        }
        return node.getBoolean();
    }

}