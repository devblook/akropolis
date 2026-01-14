package me.zetastormy.akropolis.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
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
            final Class<C> clazz,
            final Logger logger,
            final Path filePath,
            final String header,
            final NamingSchemes namingScheme,
            final TypeSerializerCollection typeSerializerCollection
    ) throws ConfigurateException {
        final ObjectMapper.Factory customFactory = ObjectMapper.factoryBuilder()
                .defaultNamingScheme(namingScheme).build();

        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().defaultOptions(
                options -> options.header(header).shouldCopyDefaults(false)
                        .serializers(build -> build.registerAnnotatedObjects(customFactory)
                                .registerAll(typeSerializerCollection))
        ).path(filePath).indent(2).nodeStyle(NodeStyle.BLOCK).build();

        try {
            CommentedConfigurationNode rootNode = loader.load();
            C config = rootNode.get(clazz);
            if (Files.notExists(filePath)) {
                logger.info("Path {} does not exist, saving...", filePath);
                rootNode.set(config);
                loader.save(rootNode);
            }
            return new ConfigurationContainer<>(config, clazz, loader, rootNode, logger, filePath);
        } catch (final ConfigurateException exception) {
            logger.error("An exception occurred while loading configuration named {}",
                    filePath.getFileName(), exception);
            throw exception;
        }
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

    public CompletableFuture<Boolean> save() {
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
        });
    }

    public C getConfig() {
        return this.config.get();
    }

    public boolean getBoolean(String... path) {
        return this.root.node((Object[]) path).getBoolean();
    }

}