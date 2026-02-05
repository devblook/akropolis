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

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import me.zetastormy.akropolis.config.transformation.AbstractTransformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.RepresentationHint;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.util.NamingSchemes;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.ScalarStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

/**
 *
 * @param <C> type representing the configuration, can be a class or a record but inner section classes should be of the
 *          same class kind to keep consistent implicit initialization behavior. If using implicit initialization the
 *           fields' default values should not be null for types with non-null implicit empty values (like collections)
 */
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

    private static <R extends Record> Constructor<R> getRecordConstructor(Class<R> clazz) throws
            ReflectiveOperationException {
        Class<?>[] types = Arrays.stream(clazz.getRecordComponents())
                .map(RecordComponent::getType).toArray(Class<?>[]::new);

        return clazz.getDeclaredConstructor(types);
    }

    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> getDefaultConstructor(Class<T> clazz) throws ReflectiveOperationException {
        if (Record.class.isAssignableFrom(clazz)) {
            Class<? extends Record> recordClass = (Class<? extends Record>) clazz;
            return (Constructor<T>) getRecordConstructor(recordClass);
        } else {
            return clazz.getDeclaredConstructor();
        }
    }

    private static <T> T getImplicitRoot(Class<T> clazz) throws ReflectiveOperationException {
        Constructor<T> constructor = getDefaultConstructor(clazz);
        if (Record.class.isAssignableFrom(clazz)) {
            return constructor.newInstance(Arrays.stream(constructor.getParameterTypes()).map(element -> null).toArray());
        } else {
            return constructor.newInstance();
        }
    }

    private static Integer getVersion(final CommentedConfigurationNode node) throws ConfigurateException {
        final var configVersionNode = node.node(AbstractTransformation.VERSION_KEY);
        if (configVersionNode.virtual()) {
            return null;
        } else {
            return node.node(AbstractTransformation.VERSION_KEY).get(Integer.class);
        }
    }

    private static void setVersion(final CommentedConfigurationNode node, final Integer value) throws ConfigurateException {
        node.node(AbstractTransformation.VERSION_KEY).set(value);
    }

    private static <C> CommentedConfigurationNode newNode(final ConfigurationLoader<CommentedConfigurationNode> loader, final C config) throws ConfigurateException {
        final CommentedConfigurationNode node = loader.createNode().set(config);
        node.hint(RepresentationHint.of("configurate:yaml/scalarstyle", ScalarStyle.class), ScalarStyle.DOUBLE_QUOTED);
        return node;
    }

    public static <C> ConfigurationContainer<C> load(
            final @NotNull Class<C> clazz,
            final @NotNull Logger logger,
            final @NotNull Path filePath,
            final @NotNull String header,
            final @NotNull NamingSchemes namingScheme,
            final @Nullable TypeSerializerCollection typeSerializerCollection,
            final @Nullable Supplier<C> defaultObjectSupplier,
            final @Nullable AbstractTransformation transformation
            ) throws ConfigurateException, ReflectiveOperationException {
        final ObjectMapper.Factory customFactory = ObjectMapper.factoryBuilder()
                .defaultNamingScheme(namingScheme).build();

        final ConfigurationOptions options = YamlConfigurationLoader.builder().defaultOptions()
                .header(header)
                // Disable implicit initialization for record classes configurations (allows setting defaults)
                .implicitInitialization(!clazz.isRecord())
                // We don't want to copy defaults (for example default config version value)
                // There is a workaround implemented but just to be safe we won't use it
                .shouldCopyDefaults(false)
                .serializers(build -> build.registerAnnotatedObjects(customFactory)
                        .registerAll(Objects.requireNonNullElseGet(typeSerializerCollection, () -> {
                            return TypeSerializerCollection.builder().build();
                        })));

        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .defaultOptions(options).path(filePath).indent(2).nodeStyle(NodeStyle.BLOCK)
                // Explicitly enable comment processing
                .commentsEnabled(true)
                // Workaround, ideally we should disable line splitting, but it isn't exposed
                .lineLength(1000)
                .headerMode(HeaderMode.PRESERVE)
                .build();

        try {
            CommentedConfigurationNode rootNode = loader.load();
            rootNode.hint(RepresentationHint.of("configurate:yaml/scalarstyle", ScalarStyle.class), ScalarStyle.DOUBLE_QUOTED);
            C config = null;

            boolean fileAlreadyExisted = Files.exists(filePath);
            if (!fileAlreadyExisted) {
                logger.info("Path {} does not exist, saving default values...", filePath);
                if (defaultObjectSupplier != null) {
                    config = Objects.requireNonNull(
                            defaultObjectSupplier.get(),
                            "Default object returned by the supplier is null"
                    );
                } else if (options.implicitInitialization()) {
                    // Implicit initialization is enabled so we can safely get the instance
                    config = rootNode.get(clazz);
                } else {
                    // Handle creating config with implicit initialization disabled
                    config = getImplicitRoot(clazz);
                }

                if (options.implicitInitialization()) {
                    // Save object data to the node and get a new instance from it
                    // so the implicit initialization can work on all classes,
                    // for example classes instances as map entry values.
                    // Finally, create a new node to preserve the default
                    // order, because using the existing node would cause
                    // the keys that were just implicitly initialized
                    // to be added at the end instead.
                    rootNode.set(config);
                    config = rootNode.get(clazz);
                    rootNode = newNode(loader, config);
                } else {
                    rootNode.set(config);
                }

                loader.save(rootNode);
            } else {
                final boolean isLoadedRootVirtualOrEmpty = rootNode.virtual() || rootNode.empty();

                @Nullable Integer originalVersion = null;
                if (options.shouldCopyDefaults() && !isLoadedRootVirtualOrEmpty) {
                    originalVersion = getVersion(rootNode);
                }

                // If the node is virtual or empty it does not make sense
                // to attempt any transformation, instead we should
                // recreate the default configuration.
                if (transformation != null && !isLoadedRootVirtualOrEmpty) {
                    int oldVersion = transformation.version(rootNode);
                    transformation.updateNode(rootNode);
                    int newVersion = transformation.version(rootNode);

                    if (oldVersion != newVersion) {
                        // We don't need implicit initialization fallback because
                        // the node was not empty and the transformations should
                        // not make it empty.
                        config = rootNode.get(clazz);

                        // Save in new node to preserve default order, delete unknown
                        // nodes and replace comments with the default comments
                        rootNode = newNode(loader, config);

                        loader.save(rootNode);
                        if (fileAlreadyExisted) {
                            logger.info(
                                    "Upgraded configuration file {} from version {} to version {} successfully!",
                                    filePath,
                                    oldVersion,
                                    newVersion
                            );
                        }
                    }
                }

                // If the transformation was not executed, the config will be null
                if (config == null) {
                    // We can try to get the instance because the file already existed
                    config = rootNode.get(clazz);

                    // Handle loading null config with implicit initialization disabled
                    if (!options.implicitInitialization() && config == null) {
                        config = getImplicitRoot(clazz);
                    }
                }

                // shouldCopyDefaults workaround to avoid version key default value
                // being copied to an existing configuration when it didn't exist.
                // This should not happen in production because every configuration
                // with a version node should have transformations.
                @Nullable Integer currentVersion = getVersion(rootNode);
                if (
                    options.shouldCopyDefaults()
                    && originalVersion == null && currentVersion != null
                    && transformation == null
                    && !isLoadedRootVirtualOrEmpty
                ) {
                    logger.error(
                        "Using shouldCopyDefaults workaround for {} null version key in file {}, the value will be set to {} in object",
                        AbstractTransformation.VERSION_KEY,
                        filePath,
                        currentVersion
                    );
                    logger.error("This should not affect the saved file but you must add a transformation to avoid this inconsistency");
                    setVersion(rootNode, originalVersion);
                } else if (currentVersion != null && transformation == null) {
                    logger.warn(
                        "Configuration file {} has version {} in key {} but it does not have a transformation!",
                        filePath,
                        currentVersion,
                        AbstractTransformation.VERSION_KEY
                    );
                }
            }

            var instance = new ConfigurationContainer<>(config, clazz, loader, rootNode, logger, filePath);
            logger.info(
                    "Configuration file {} {} successfully!",
                    filePath,
                    (fileAlreadyExisted) ? "loaded" : "created"
            );
            return instance;
        } catch (final ConfigurateException | ReflectiveOperationException exception) {
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
            final @Nullable TypeSerializerCollection typeSerializerCollection,
            final @Nullable AbstractTransformation transformation
    ) throws ConfigurateException, ReflectiveOperationException {
        return load(
                clazz,
                logger,
                filePath,
                header,
                namingScheme,
                typeSerializerCollection,
                null,
                transformation
        );
    }

    public CompletableFuture<Boolean> reload() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CommentedConfigurationNode rootNode = this.loader.load();
                rootNode.hint(RepresentationHint.of("configurate:yaml/scalarstyle", ScalarStyle.class), ScalarStyle.DOUBLE_QUOTED);
                this.root = rootNode;
                C newConfig = rootNode.get(this.clazz);
                // Handle loading null config with implicit initialization disabled
                if (!rootNode.options().implicitInitialization() && newConfig == null) {
                    newConfig = getImplicitRoot(this.clazz);
                }
                this.config.set(newConfig);
                this.logger.info("Configuration file {} reloaded successfully!", this.filePath.getFileName());
                return true;
            } catch (ConfigurateException | ReflectiveOperationException exception) {
                logger.error("An exception occurred while reloading the configuration named {}",
                        this.filePath.getFileName(), exception);
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> save(final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                @Nullable Integer originalVersion = getVersion(this.root);
                this.root.set(this.clazz, this.config.get());
                @Nullable Integer saveVersion = getVersion(this.root);
                // We have to fix the inconsistency when the original version was null, there was no transformation
                // and the object's version is now the default version which is not what we want to save
                if (this.root.options().shouldCopyDefaults() && originalVersion == null && saveVersion != null) {
                    this.logger.info(
                        "Using shouldCopyDefaults workaround in file {} with version key {}, replacing object's version {} with original {}",
                        filePath,
                        AbstractTransformation.VERSION_KEY,
                        saveVersion,
                        originalVersion
                    );
                    setVersion(this.root, originalVersion);
                }
                this.loader.save(this.root);
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
